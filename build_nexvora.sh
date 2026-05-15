#!/bin/bash
# build_nexvora.sh - Nexvora Android APK Build Script (Linux/macOS)
#
# Prerequisites:
#   1. depot_tools installed and in PATH
#   2. Android SDK at ~/Android/Sdk or $ANDROID_HOME
#   3. Android NDK r25+ installed via SDK Manager or manually
#   4. JDK 17+
#   5. Python 3
#
# Usage:
#   ./build_nexvora.sh                   # Build ARM64 APK (default)
#   ./build_nexvora.sh --arch arm        # Build 32-bit ARM APK
#   ./build_nexvora.sh --arch x86        # Build x86 APK
#   ./build_nexvora.sh --arch x64        # Build x64 APK
#   ./build_nexvora.sh --setup-only      # Only configure, don't build
#   ./build_nexvora.sh --verbose         # Detailed output

set -euo pipefail

ARCH="${1:-arm64}"
SETUP_ONLY=false
VERBOSE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        --arch) ARCH="$2"; shift 2 ;;
        --setup-only) SETUP_ONLY=true; shift ;;
        --verbose) VERBOSE=true; shift ;;
        *) ARCH="$1"; shift ;;
    esac
done

# Validate arch
case "$ARCH" in
    arm|arm64|x86|x64) ;;
    *) echo "ERROR: Invalid arch '$ARCH'. Use: arm, arm64, x86, x64"; exit 1 ;;
esac

echo ">>> Nexvora Build Script"
echo ">>> Target: $ARCH"

# --- Step 1: Verify environment ---
echo ">>> Checking build environment..."

# Check commands
for cmd in gn ninja java javac python3; do
    if command -v "$cmd" &>/dev/null; then
        echo "  OK: $cmd found at $(command -v "$cmd")"
    else
        echo "  ERROR: $cmd not found in PATH"
        ENV_OK=false
    fi
done

# Check gclient (optional)
if command -v gclient &>/dev/null; then
    echo "  OK: gclient found"
else
    echo "  WARN: gclient not found (not critical if gn+ninja are available)"
fi

# Android SDK
if [ -z "${ANDROID_HOME:-}" ]; then
    SDK_PATH="$HOME/Android/Sdk"
    if [ -d "$SDK_PATH" ]; then
        export ANDROID_HOME="$SDK_PATH"
        export ANDROID_SDK_ROOT="$SDK_PATH"
        echo "  OK: Android SDK at $SDK_PATH"
    else
        echo "  ERROR: Android SDK not found. Set ANDROID_HOME."
        exit 1
    fi
else
    echo "  OK: ANDROID_HOME=$ANDROID_HOME"
fi

# Android NDK
if [ -z "${ANDROID_NDK_HOME:-}" ]; then
    NDK_SEARCH_PATHS=(
        "$ANDROID_HOME/ndk"
        "$HOME/Android/Sdk/ndk"
    )
    NDK_FOUND=false
    for np in "${NDK_SEARCH_PATHS[@]}"; do
        if [ -d "$np" ]; then
            NDK_VERSION=$(ls "$np" 2>/dev/null | head -1)
            if [ -n "$NDK_VERSION" ]; then
                export ANDROID_NDK_HOME="$np/$NDK_VERSION"
                export ANDROID_NDK_ROOT="$np/$NDK_VERSION"
                echo "  OK: Android NDK $NDK_VERSION at $ANDROID_NDK_HOME"
                NDK_FOUND=true
                break
            fi
        fi
    done
    if [ "$NDK_FOUND" = false ]; then
        echo "  ERROR: Android NDK not found. Install via SDK Manager (NDK r25+)."
        exit 1
    fi
else
    echo "  OK: ANDROID_NDK_HOME=$ANDROID_NDK_HOME"
fi

# DEPOT_TOOLS_WIN_TOOLCHAIN (Windows only, harmless on Linux)
export DEPOT_TOOLS_WIN_TOOLCHAIN=0

# Source root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_ROOT="$SCRIPT_DIR"
if [ -f "$SRC_ROOT/chrome/android/BUILD.gn" ]; then
    echo "  OK: Source root at $SRC_ROOT"
else
    echo "  ERROR: Cannot find chrome/android/BUILD.gn. Run from source root."
    exit 1
fi

# --- Step 2: Configure build ---
echo ">>> Configuring build for $ARCH..."

OUT_DIR="$SRC_ROOT/out/android_$ARCH"
ARGS_FILE="$SRC_ROOT/.build/android_$ARCH/args.gn"

if [ ! -f "$ARGS_FILE" ]; then
    echo "  ERROR: Args file not found: $ARGS_FILE"
    exit 1
fi

mkdir -p "$OUT_DIR"
cp "$ARGS_FILE" "$OUT_DIR/args.gn"
echo "  OK: args.gn copied to $OUT_DIR"

echo ">>> Running gn gen..."
cd "$SRC_ROOT"
if ! gn gen "$OUT_DIR" --args="$(cat "$ARGS_FILE")"; then
    echo "  ERROR: gn gen failed"
    exit 1
fi
echo "  OK: gn gen completed"

if [ "$SETUP_ONLY" = true ]; then
    echo ""
    echo "Setup complete. To build: ninja -C $OUT_DIR chrome_public_apk"
    exit 0
fi

# --- Step 3: Build ---
echo ">>> Building chrome_public_apk for $ARCH..."
echo "  Output: $OUT_DIR"
echo "  This will take 2-4 hours on modern hardware."

cd "$SRC_ROOT"
if ! ninja -C "$OUT_DIR" chrome_public_apk; then
    echo "  ERROR: Build failed"
    exit 1
fi

APK_PATH="$OUT_DIR/apks/chrome_public_apk.apk"
if [ -f "$APK_PATH" ]; then
    echo ""
    echo "  SUCCESS: Nexvora APK built!"
    echo "  APK: $APK_PATH"
else
    echo ""
    echo "  Build completed. Searching for APK..."
    find "$OUT_DIR" -name "*.apk" -type f 2>/dev/null
fi
