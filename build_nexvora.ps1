# build_nexvora.ps1 - Nexvora Android APK Build Script (Windows)
#
# Prerequisites:
#   1. depot_tools installed and in PATH
#   2. Android SDK at C:\Users\<user>\AppData\Local\Android\Sdk
#   3. Android NDK r25+ installed via SDK Manager or manually
#   4. JDK 17+ (available in this environment)
#   5. Python 3 (available in this environment)
#
# Usage:
#   .\build_nexvora.ps1 -Arch arm64    # Build ARM64 APK (default)
#   .\build_nexvora.ps1 -Arch arm      # Build 32-bit ARM APK
#   .\build_nexvora.ps1 -Arch x86      # Build x86 APK
#   .\build_nexvora.ps1 -Arch x64      # Build x64 APK
#   .\build_nexvora.ps1 -SetupOnly     # Only configure, don't build
#
# Steps:
#   1. Verifies depot_tools, gn, ninja, SDK, NDK, Java, Python
#   2. Sets ANDROID_HOME, ANDROID_NDK_HOME, DEPOT_TOOLS_WIN_TOOLCHAIN
#   3. Runs gn gen to configure the build
#   4. Runs ninja to build chrome_public_apk

param(
    [ValidateSet("arm", "arm64", "x86", "x64")]
    [string]$Arch = "arm64",
    [switch]$SetupOnly,
    [switch]$Verbose
)

function Write-Step($msg) {
    Write-Host ">>> $msg" -ForegroundColor Cyan
}

function Write-Error($msg) {
    Write-Host "ERROR: $msg" -ForegroundColor Red
}

function Write-Success($msg) {
    Write-Host "OK: $msg" -ForegroundColor Green
}

function Check-Command($cmd) {
    $found = Get-Command $cmd -ErrorAction SilentlyContinue
    if (-not $found) {
        Write-Error "$cmd not found in PATH"
        return $false
    }
    Write-Success "$cmd found: $($found.Source)"
    return $true
}

# --- Step 1: Verify environment ---
Write-Step "Checking build environment..."

$envOk = $true

# depot_tools
if (-not (Check-Command "gn")) { $envOk = $false }
if (-not (Check-Command "ninja")) { $envOk = $false }
if (-not (Check-Command "gclient")) { 
    Write-Host "  (gclient not critical if gn+ninja are available)" -ForegroundColor Yellow
}

# Java
if (-not (Check-Command "java")) { $envOk = $false }
if (-not (Check-Command "javac")) { $envOk = $false }

# Python
if (-not (Check-Command "python3") -and -not (Check-Command "python")) { 
    Write-Error "Python 3 not found"
    $envOk = $false
}

# Android SDK
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
if (-not (Test-Path $sdkPath)) {
    $sdkPath = "$env:USERPROFILE\Android\Sdk"
}
if (-not (Test-Path $sdkPath)) {
    Write-Error "Android SDK not found. Set ANDROID_HOME."
    $envOk = $false
} else {
    Write-Success "Android SDK: $sdkPath"
    $env:ANDROID_HOME = $sdkPath
    $env:ANDROID_SDK_ROOT = $sdkPath
    
    # Check build-tools
    $btDir = Join-Path $sdkPath "build-tools"
    if (Test-Path $btDir) {
        $btVersions = Get-ChildItem $btDir -Directory | Select-Object -ExpandProperty Name
        Write-Success "Build-tools: $($btVersions -join ', ')"
    }
}

# Android NDK
$ndkPaths = @(
    "$sdkPath\ndk",
    "$env:USERPROFILE\Android\Sdk\ndk",
    "C:\Android\ndk"
)
$ndkFound = $false
foreach ($np in $ndkPaths) {
    if (Test-Path $np) {
        $ndkVersions = Get-ChildItem $np -Directory -ErrorAction SilentlyContinue
        if ($ndkVersions) {
            $ndkVersion = $ndkVersions[0].Name
            $env:ANDROID_NDK_HOME = Join-Path $np $ndkVersion
            $env:ANDROID_NDK_ROOT = Join-Path $np $ndkVersion
            Write-Success "Android NDK: $ndkVersion at $($env:ANDROID_NDK_HOME)"
            $ndkFound = $true
            break
        }
    }
}
if (-not $ndkFound) {
    Write-Error "Android NDK not found. Install via SDK Manager (NDK r25+)."
    $envOk = $false
}

# DEPOT_TOOLS_WIN_TOOLCHAIN
if (-not $env:DEPOT_TOOLS_WIN_TOOLCHAIN) {
    Write-Host "  Setting DEPOT_TOOLS_WIN_TOOLCHAIN=0 (using system toolchain)" -ForegroundColor Yellow
    $env:DEPOT_TOOLS_WIN_TOOLCHAIN = "0"
}

# Source root
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcRoot = Resolve-Path $scriptDir
if (Test-Path (Join-Path $srcRoot "chrome\android\BUILD.gn")) {
    Write-Success "Source root: $srcRoot"
} else {
    Write-Error "Cannot find chrome/android/BUILD.gn. Run from source root."
    $envOk = $false
}

if (-not $envOk) {
    Write-Host "`nFIX: Install missing tools above, then re-run." -ForegroundColor Red
    exit 1
}

# --- Step 2: Configure build ---
Write-Step "Configuring build for $Arch..."

$outDir = Join-Path $srcRoot "out\android_$Arch"
$argsFile = Join-Path $srcRoot ".build\android_$Arch\args.gn"

if (-not (Test-Path $argsFile)) {
    Write-Error "Args file not found: $argsFile"
    exit 1
}

# Create output directory
if (-not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir -Force | Out-Null
    Write-Success "Created output directory: $outDir"
}

# Copy args.gn
Copy-Item $argsFile (Join-Path $outDir "args.gn") -Force
Write-Success "Copied args.gn to output directory"

# Generate build files
Write-Step "Running gn gen..."
Push-Location $srcRoot
try {
    gn gen $outDir --args="$(Get-Content $argsFile -Raw | Out-String)"
    if ($LASTEXITCODE -eq 0) {
        Write-Success "gn gen completed successfully"
    } else {
        Write-Error "gn gen failed with exit code $LASTEXITCODE"
        exit 1
    }
} catch {
    Write-Error "gn gen failed: $_"
    Pop-Location
    exit 1
}
Pop-Location

if ($SetupOnly) {
    Write-Host "`nSetup complete. To build: ninja -C $outDir chrome_public_apk" -ForegroundColor Green
    exit 0
}

# --- Step 3: Build ---
Write-Step "Building chrome_public_apk for $Arch..."
Write-Host "  Output: $outDir" -ForegroundColor Yellow
Write-Host "  This will take 2-4 hours on modern hardware." -ForegroundColor Yellow

Push-Location $srcRoot
try {
    ninja -C $outDir chrome_public_apk
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Build completed successfully!"
        $apkPath = Join-Path $outDir "apks\chrome_public_apk.apk"
        if (Test-Path $apkPath) {
            Write-Host "APK: $apkPath" -ForegroundColor Green
        }
    } else {
        Write-Error "Build failed with exit code $LASTEXITCODE"
        exit 1
    }
} catch {
    Write-Error "Build failed: $_"
    Pop-Location
    exit 1
}
Pop-Location

Write-Host "`nDONE! Nexvora APK built successfully." -ForegroundColor Green
