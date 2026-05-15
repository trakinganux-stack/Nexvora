package org.chromium.chrome.browser.nexvora.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.chromium.chrome.R;

public class CinematicHeroView extends FrameLayout {

    private ImageView mBackgroundImage;
    private View mGradientOverlay;
    private TextView mTitleText;
    private TextView mSubtitleText;
    private TextView mActionButton;
    private ProgressBar mProgressBar;
    private LinearLayout mIndicatorContainer;

    private String[] mTitles;
    private String[] mSubtitles;
    private int[] mBackgroundColors;
    private int mCurrentIndex;
    private Handler mHandler;
    private Runnable mAutoAdvance;
    private boolean mIsAutoPlaying;

    public CinematicHeroView(Context context) {
        super(context);
        init();
    }

    public CinematicHeroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CinematicHeroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.nexvora_hero_view, this);
        mBackgroundImage = findViewById(R.id.nexvora_hero_background);
        mGradientOverlay = findViewById(R.id.nexvora_hero_gradient);
        mTitleText = findViewById(R.id.nexvora_hero_title);
        mSubtitleText = findViewById(R.id.nexvora_hero_subtitle);
        mActionButton = findViewById(R.id.nexvora_hero_action);
        mProgressBar = findViewById(R.id.nexvora_hero_progress);
        mIndicatorContainer = findViewById(R.id.nexvora_hero_indicators);

        mHandler = new Handler();
    }

    public void setHeroContent(String[] titles, String[] subtitles, int[] backgroundColors) {
        mTitles = titles;
        mSubtitles = subtitles;
        mBackgroundColors = backgroundColors;

        for (int i = 0; i < titles.length; i++) {
            View indicator = new View(getContext());
            indicator.setLayoutParams(new LinearLayout.LayoutParams(
                    dpToPx(8), dpToPx(8)));
            ((LinearLayout.LayoutParams) indicator.getLayoutParams()).setMargins(dpToPx(4), 0, dpToPx(4), 0);
            indicator.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.nexvora_hero_indicator));
            indicator.setAlpha(i == 0 ? 1.0f : 0.4f);
            mIndicatorContainer.addView(indicator);
        }

        showItem(0);
        startAutoAdvance();
    }

    private void showItem(int index) {
        if (mTitles == null || index >= mTitles.length) return;

        mCurrentIndex = index;

        int color = mBackgroundColors != null && index < mBackgroundColors.length
                ? mBackgroundColors[index] : Color.BLACK;

        mTitleText.setText(mTitles[index]);
        mSubtitleText.setText(mSubtitles != null && index < mSubtitles.length
                ? mSubtitles[index] : "");

        animateBackgroundColor(color);

        for (int i = 0; i < mIndicatorContainer.getChildCount(); i++) {
            View indicator = mIndicatorContainer.getChildAt(i);
            indicator.animate().alpha(i == index ? 1.0f : 0.4f).setDuration(300).start();
        }
    }

    private void animateBackgroundColor(int targetColor) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(600);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            updateGradientOverlay(targetColor, animation.getAnimatedFraction());
        });
        anim.start();
    }

    public void setActionClickListener(OnClickListener listener) {
        mActionButton.setOnClickListener(listener);
    }

    private void startAutoAdvance() {
        mIsAutoPlaying = true;
        mAutoAdvance = () -> {
            if (!mIsAutoPlaying || mTitles == null) return;
            int next = (mCurrentIndex + 1) % mTitles.length;
            animateTransition(next);
            mHandler.postDelayed(mAutoAdvance, 5000);
        };
        mHandler.postDelayed(mAutoAdvance, 5000);
    }

    public void stopAutoAdvance() {
        mIsAutoPlaying = false;
        if (mAutoAdvance != null) {
            mHandler.removeCallbacks(mAutoAdvance);
        }
    }

    private void animateTransition(int nextIndex) {
        showItem(nextIndex);
    }

    private void updateGradientOverlay(int targetColor, float fraction) {
        if (fraction <= 0) {
            mGradientOverlay.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        int red = (int) (Color.red(targetColor) * fraction);
        int green = (int) (Color.green(targetColor) * fraction);
        int blue = (int) (Color.blue(targetColor) * fraction);
        int alpha = (int) (180 * fraction);
        mGradientOverlay.setBackgroundColor(Color.argb(Math.min(255, alpha), Math.min(255, red), Math.min(255, green), Math.min(255, blue)));
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoAdvance();
    }
}
