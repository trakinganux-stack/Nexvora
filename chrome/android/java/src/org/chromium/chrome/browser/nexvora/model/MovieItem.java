package org.chromium.chrome.browser.nexvora.model;

public class MovieItem {
    private String mId;
    private String mTitle;
    private String mSubtitle;
    private String mPosterUrl;
    private String mBackgroundUrl;
    private String mUrl;
    private String mCategory;
    private float mRating;
    private boolean mIsContinueWatching;
    private float mProgress;

    public MovieItem(String id, String title, String subtitle, String posterUrl,
                     String backgroundUrl, String url, String category,
                     float rating, boolean isContinueWatching, float progress) {
        mId = id;
        mTitle = title;
        mSubtitle = subtitle;
        mPosterUrl = posterUrl;
        mBackgroundUrl = backgroundUrl;
        mUrl = url;
        mCategory = category;
        mRating = rating;
        mIsContinueWatching = isContinueWatching;
        mProgress = progress;
    }

    public String getId() { return mId; }
    public String getTitle() { return mTitle; }
    public String getSubtitle() { return mSubtitle; }
    public String getPosterUrl() { return mPosterUrl; }
    public String getBackgroundUrl() { return mBackgroundUrl; }
    public String getUrl() { return mUrl; }
    public String getCategory() { return mCategory; }
    public float getRating() { return mRating; }
    public boolean isContinueWatching() { return mIsContinueWatching; }
    public float getProgress() { return mProgress; }
}
