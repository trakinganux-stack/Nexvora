package org.chromium.chrome.browser.nexvora;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.fragments.DownloadsFragment;
import org.chromium.chrome.browser.nexvora.fragments.ExtensionsFragment;
import org.chromium.chrome.browser.nexvora.fragments.HomeFragment;
import org.chromium.chrome.browser.nexvora.fragments.SearchFragment;
import org.chromium.chrome.browser.nexvora.fragments.SettingsFragment;
import org.chromium.chrome.browser.nexvora.ui.BottomNavView;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.chrome.browser.tabmodel.TabModel;

public class NexvoraCoordinator {

    private static final String PREFS_NAME = "nexvora_prefs";

    private Context mContext;
    private FrameLayout mCinematicContainer;
    private View mBrowserContainer;
    private BottomNavView mBottomNav;
    private FragmentManager mFragmentManager;
    private NexvoraBrowserBridge mBrowserBridge;

    private HomeFragment mHomeFragment;
    private SearchFragment mSearchFragment;
    private DownloadsFragment mDownloadsFragment;
    private ExtensionsFragment mExtensionsFragment;
    private SettingsFragment mSettingsFragment;

    private boolean mIsCinematicMode;
    private int mCurrentCinematicTab = BottomNavView.TAB_HOME;
    private Tab mActiveTab;

    public interface NexvoraBrowserBridge {
        void loadUrl(String url);
        Tab getCurrentTab();
        TabModel getCurrentTabModel();
        void openExtensionsPage();
        void openCookieEditor();
        boolean isIncognito();
    }

    public NexvoraCoordinator(Context context, FrameLayout cinematicContainer,
                               View browserContainer, BottomNavView bottomNav,
                               FragmentManager fragmentManager,
                               NexvoraBrowserBridge bridge) {
        mContext = context;
        mCinematicContainer = cinematicContainer;
        mBrowserContainer = browserContainer;
        mBottomNav = bottomNav;
        mFragmentManager = fragmentManager;
        mBrowserBridge = bridge;

        mIsCinematicMode = getPrefs().getBoolean("cinematic_mode", true);

        setupBottomNav();
        setupFragments();
    }

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void setupBottomNav() {
        mBottomNav.setOnTabSelectedListener(tabId -> {
            mCurrentCinematicTab = tabId;
            showCinematicFragment(tabId);
        });
    }

    private void setupFragments() {
        mHomeFragment = new HomeFragment();
        mSearchFragment = new SearchFragment();
        mDownloadsFragment = new DownloadsFragment();
        mExtensionsFragment = new ExtensionsFragment();
        mSettingsFragment = new SettingsFragment();

        mExtensionsFragment.setCoordinator(this);
        mSettingsFragment.setCoordinator(this);

        MovieCardClickListener clickListener = item -> {
            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                loadUrl(item.getUrl());
            }
        };

        mHomeFragment.setMovieClickListener(clickListener);
        mSearchFragment.setMovieClickListener(clickListener);

        if (mIsCinematicMode) {
            showCinematicUI(false);
        }
    }

    public void showCinematicUI(boolean animate) {
        mIsCinematicMode = true;
        getPrefs().edit().putBoolean("cinematic_mode", true).apply();

        mCinematicContainer.setVisibility(View.VISIBLE);
        mCinematicContainer.setAlpha(0f);
        mCinematicContainer.animate()
                .alpha(1f)
                .setDuration(animate ? 400 : 0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        showCinematicFragment(mCurrentCinematicTab);
    }

    public void hideCinematicUI(boolean animate) {
        mIsCinematicMode = false;
        getPrefs().edit().putBoolean("cinematic_mode", false).apply();

        if (animate) {
            mCinematicContainer.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> mCinematicContainer.setVisibility(View.GONE))
                    .start();
        } else {
            mCinematicContainer.setVisibility(View.GONE);
        }
    }

    public boolean isCinematicMode() {
        return mIsCinematicMode;
    }

    public void toggleCinematicMode() {
        if (mIsCinematicMode) {
            hideCinematicUI(true);
        } else {
            showCinematicUI(true);
        }
    }

    public void loadUrl(String url) {
        hideCinematicUI(true);
        if (mBrowserBridge != null) {
            mBrowserBridge.loadUrl(url);
        }
    }

    public void onBackPressed() {
        if (mIsCinematicMode && mCurrentCinematicTab != BottomNavView.TAB_HOME) {
            mBottomNav.selectTab(BottomNavView.TAB_HOME, true);
        } else if (mIsCinematicMode) {
            hideCinematicUI(true);
        }
    }

    public void setActiveTab(Tab tab) {
        mActiveTab = tab;
    }

    private void showCinematicFragment(int tabId) {
        if (mFragmentManager == null) return;

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        hideAllFragments(transaction);

        switch (tabId) {
            case BottomNavView.TAB_HOME:
                if (!mHomeFragment.isAdded()) {
                    transaction.add(R.id.nexvora_cinematic_content, mHomeFragment, "home");
                }
                transaction.show(mHomeFragment);
                break;

            case BottomNavView.TAB_SEARCH:
                if (!mSearchFragment.isAdded()) {
                    transaction.add(R.id.nexvora_cinematic_content, mSearchFragment, "search");
                }
                transaction.show(mSearchFragment);
                break;

            case BottomNavView.TAB_DOWNLOADS:
                if (!mDownloadsFragment.isAdded()) {
                    transaction.add(R.id.nexvora_cinematic_content, mDownloadsFragment, "downloads");
                }
                transaction.show(mDownloadsFragment);
                break;

            case BottomNavView.TAB_EXTENSIONS:
                if (!mExtensionsFragment.isAdded()) {
                    transaction.add(R.id.nexvora_cinematic_content, mExtensionsFragment, "extensions");
                }
                transaction.show(mExtensionsFragment);
                break;

            case BottomNavView.TAB_SETTINGS:
                if (!mSettingsFragment.isAdded()) {
                    transaction.add(R.id.nexvora_cinematic_content, mSettingsFragment, "settings");
                }
                transaction.show(mSettingsFragment);
                break;
        }

        transaction.commit();
    }

    private void hideAllFragments(FragmentTransaction transaction) {
        if (mHomeFragment.isAdded()) transaction.hide(mHomeFragment);
        if (mSearchFragment.isAdded()) transaction.hide(mSearchFragment);
        if (mDownloadsFragment.isAdded()) transaction.hide(mDownloadsFragment);
        if (mExtensionsFragment.isAdded()) transaction.hide(mExtensionsFragment);
        if (mSettingsFragment.isAdded()) transaction.hide(mSettingsFragment);
    }

    public interface MovieCardClickListener {
        void onMovieClick(org.chromium.chrome.browser.nexvora.model.MovieItem item);
    }
}
