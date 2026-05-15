package org.chromium.chrome.browser.nexvora;

import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.ChromeTabbedActivity;
import org.chromium.chrome.browser.nexvora.ui.BottomNavView;
import org.chromium.chrome.browser.tab.Tab;
import org.chromium.chrome.browser.tabmodel.TabModel;
import org.chromium.url.GURL;

public class NexvoraIntegration implements NexvoraCoordinator.NexvoraBrowserBridge {

    private ChromeTabbedActivity mActivity;
    private NexvoraCoordinator mCoordinator;
    private FrameLayout mCinematicContainer;
    private boolean mInitialized;

    public NexvoraIntegration(ChromeTabbedActivity activity) {
        mActivity = activity;
    }

    public void initialize() {
        if (mInitialized) return;
        mInitialized = true;

        mActivity.getWindow().getDecorView().post(() -> {
            ViewGroup contentParent = mActivity.findViewById(android.R.id.content);
            if (contentParent == null) return;

            while (contentParent.getParent() instanceof ViewGroup
                    && !(contentParent.getParent() instanceof FrameLayout)) {
                contentParent = (ViewGroup) contentParent.getParent();
            }

            ViewGroup root = (ViewGroup) contentParent.getParent();
            if (root == null) root = contentParent;

            mCinematicContainer = (FrameLayout) mActivity.getLayoutInflater()
                    .inflate(R.layout.nexvora_cinematic_container, root, false);

            BottomNavView bottomNav = mCinematicContainer.findViewById(R.id.nexvora_bottom_nav);

            FragmentManager fragmentManager = null;
            if (mActivity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) mActivity).getSupportFragmentManager();
            }

            mCoordinator = new NexvoraCoordinator(
                    mActivity,
                    mCinematicContainer,
                    contentParent,
                    bottomNav,
                    fragmentManager,
                    this
            );

            root.addView(mCinematicContainer,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            mCinematicContainer.setVisibility(View.VISIBLE);
            mCoordinator.showCinematicUI(false);
        });
    }

    public boolean onBackPressed() {
        if (mCoordinator != null && mCoordinator.isCinematicMode()) {
            mCoordinator.onBackPressed();
            return true;
        }
        return false;
    }

    public void onTabSwitched(Tab tab) {
        if (mCoordinator != null) {
            mCoordinator.setActiveTab(tab);
        }
    }

    @Override
    public void loadUrl(String url) {
        if (mActivity == null || url == null || url.isEmpty()) return;
        Tab tab = mActivity.getTabModelSelector().getCurrentTab();
        if (tab != null) {
            tab.loadUrl(new GURL(url));
        }
    }

    @Override
    public Tab getCurrentTab() {
        if (mActivity == null) return null;
        return mActivity.getTabModelSelector().getCurrentTab();
    }

    @Override
    public TabModel getCurrentTabModel() {
        if (mActivity == null) return null;
        return mActivity.getTabModelSelector().getCurrentModel();
    }

    @Override
    public void openExtensionsPage() {
        loadUrl("chrome://extensions");
    }

    @Override
    public void openCookieEditor() {
        loadUrl("chrome://settings/content/cookies");
    }

    @Override
    public boolean isIncognito() {
        if (mActivity == null) return false;
        return mActivity.getTabModelSelector().isIncognitoSelected();
    }

    public NexvoraCoordinator getCoordinator() {
        return mCoordinator;
    }
}
