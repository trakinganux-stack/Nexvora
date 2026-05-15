package org.chromium.chrome.browser.nexvora.ui;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import org.chromium.chrome.R;

public class BottomNavView extends FrameLayout {

    public static final int TAB_HOME = 0;
    public static final int TAB_SEARCH = 1;
    public static final int TAB_DOWNLOADS = 2;
    public static final int TAB_EXTENSIONS = 3;
    public static final int TAB_SETTINGS = 4;

    private OnTabSelectedListener mListener;
    private int mSelectedTab = TAB_HOME;

    public interface OnTabSelectedListener {
        void onTabSelected(int tabId);
    }

    public BottomNavView(Context context) {
        super(context);
        init();
    }

    public BottomNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.nexvora_bottom_nav, this);

        findViewById(R.id.nexvora_nav_home).setOnClickListener(v -> selectTab(TAB_HOME, true));
        findViewById(R.id.nexvora_nav_search).setOnClickListener(v -> selectTab(TAB_SEARCH, true));
        findViewById(R.id.nexvora_nav_downloads).setOnClickListener(v -> selectTab(TAB_DOWNLOADS, true));
        findViewById(R.id.nexvora_nav_extensions).setOnClickListener(v -> selectTab(TAB_EXTENSIONS, true));
        findViewById(R.id.nexvora_nav_settings).setOnClickListener(v -> selectTab(TAB_SETTINGS, true));

        selectTab(TAB_HOME, false);
    }

    private int getIconViewId(int tabId) {
        switch (tabId) {
            case TAB_HOME: return R.id.nexvora_nav_icon_home;
            case TAB_SEARCH: return R.id.nexvora_nav_icon_search;
            case TAB_DOWNLOADS: return R.id.nexvora_nav_icon_downloads;
            case TAB_EXTENSIONS: return R.id.nexvora_nav_icon_extensions;
            case TAB_SETTINGS: return R.id.nexvora_nav_icon_settings;
            default: return 0;
        }
    }

    private int getLabelViewId(int tabId) {
        switch (tabId) {
            case TAB_HOME: return R.id.nexvora_nav_label_home;
            case TAB_SEARCH: return R.id.nexvora_nav_label_search;
            case TAB_DOWNLOADS: return R.id.nexvora_nav_label_downloads;
            case TAB_EXTENSIONS: return R.id.nexvora_nav_label_extensions;
            case TAB_SETTINGS: return R.id.nexvora_nav_label_settings;
            default: return 0;
        }
    }

    public void selectTab(int tabId, boolean animate) {
        if (tabId == mSelectedTab) return;

        deselectAll();

        ImageView icon = findViewById(getIconViewId(tabId));
        TextView labelText = findViewById(getLabelViewId(tabId));

        if (icon != null) {
            ImageViewCompat.setImageTintList(icon,
                    ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.nexvora_accent_red)));
            if (animate) {
                ObjectAnimator scaleAnim = ObjectAnimator.ofPropertyValuesHolder(icon,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f));
                scaleAnim.setDuration(300);
                scaleAnim.start();
            }
        }
        if (labelText != null) {
            labelText.setTextColor(ContextCompat.getColor(getContext(), R.color.nexvora_accent_red));
            labelText.setTypeface(labelText.getTypeface(), Typeface.BOLD);
        }

        mSelectedTab = tabId;

        if (mListener != null) {
            mListener.onTabSelected(tabId);
        }
    }

    private void deselectAll() {
        for (int i = TAB_HOME; i <= TAB_SETTINGS; i++) {
            ImageView icon = findViewById(getIconViewId(i));
            TextView labelText = findViewById(getLabelViewId(i));
            if (icon != null) {
                ImageViewCompat.setImageTintList(icon,
                        ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.nexvora_text_secondary)));
            }
            if (labelText != null) {
                labelText.setTextColor(ContextCompat.getColor(getContext(), R.color.nexvora_text_secondary));
                labelText.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    public int getSelectedTab() {
        return mSelectedTab;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mListener = listener;
    }
}
