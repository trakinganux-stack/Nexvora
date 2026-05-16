package com.nexvora.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_HOME = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_DOWNLOADS = 2;
    private static final int TAB_EXTENSIONS = 3;
    private static final int TAB_SETTINGS = 4;

    private int mCurrentTab = TAB_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNav();
        setupHeroSection();
        setupMovieRows();
    }

    private void setupBottomNav() {
        findViewById(R.id.nav_home).setOnClickListener(v -> selectTab(TAB_HOME));
        findViewById(R.id.nav_search).setOnClickListener(v -> selectTab(TAB_SEARCH));
        findViewById(R.id.nav_downloads).setOnClickListener(v -> selectTab(TAB_DOWNLOADS));
        findViewById(R.id.nav_extensions).setOnClickListener(v -> selectTab(TAB_EXTENSIONS));
        findViewById(R.id.nav_settings).setOnClickListener(v -> selectTab(TAB_SETTINGS));

        selectTab(TAB_HOME);
    }

    private void selectTab(int tabId) {
        if (tabId == mCurrentTab) return;

        int[] iconIds = {
            R.id.nav_icon_home, R.id.nav_icon_search, R.id.nav_icon_downloads,
            R.id.nav_icon_extensions, R.id.nav_icon_settings
        };
        int[] labelIds = {
            R.id.nav_label_home, R.id.nav_label_search, R.id.nav_label_downloads,
            R.id.nav_label_extensions, R.id.nav_label_settings
        };

        for (int i = 0; i < 5; i++) {
            ImageView icon = findViewById(iconIds[i]);
            TextView label = findViewById(labelIds[i]);
            if (i == tabId) {
                icon.setColorFilter(getColor(R.color.nexvora_accent_red));
                label.setTextColor(getColor(R.color.nexvora_accent_red));
            } else {
                icon.setColorFilter(getColor(R.color.nexvora_text_secondary));
                label.setTextColor(getColor(R.color.nexvora_text_secondary));
            }
        }

        mCurrentTab = tabId;
    }

    private void setupHeroSection() {
        TextView title = findViewById(R.id.hero_title);
        TextView subtitle = findViewById(R.id.hero_subtitle);
        title.setText("Nexvora Originals");
        subtitle.setText("Experience cinematic browsing");
    }

    private void setupMovieRows() {
        setupRow(R.id.trending_list, R.id.trending_label, "Trending",
                new String[]{"Web Explorer", "Video Stream", "Social Hub", "News Today", "Music World"},
                new String[]{"Browse the web", "Watch videos", "Stay connected", "Latest updates", "Listen & discover"});

        setupRow(R.id.recommended_list, R.id.recommended_label, "Recommended For You",
                new String[]{"Developer Docs", "Creative Studio", "Gaming Zone"},
                new String[]{"Programming", "Design", "Entertainment"});
    }

    private void setupRow(int recyclerViewId, int labelId, String label, String[] titles, String[] subtitles) {
        TextView labelView = findViewById(labelId);
        if (labelView != null) labelView.setText(label);

        RecyclerView recyclerView = findViewById(recyclerViewId);
        if (recyclerView == null) return;

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<MovieItem> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MovieItem item = new MovieItem(
                String.valueOf(i), titles[i], subtitles[i], null, null, "#",
                label, 4.5f, false, 0
            );
            items.add(item);
        }

        MovieCardAdapter adapter = new MovieCardAdapter(items, null);
        recyclerView.setAdapter(adapter);
    }

    public static class MovieItem {
        private String mId, mTitle, mSubtitle, mPosterUrl, mBackgroundUrl, mUrl, mCategory;
        private float mRating;
        private boolean mIsContinueWatching;
        private float mProgress;

        public MovieItem(String id, String title, String subtitle, String posterUrl,
                        String backgroundUrl, String url, String category,
                        float rating, boolean isContinueWatching, float progress) {
            mId = id; mTitle = title; mSubtitle = subtitle;
            mPosterUrl = posterUrl; mBackgroundUrl = backgroundUrl; mUrl = url;
            mCategory = category; mRating = rating;
            mIsContinueWatching = isContinueWatching; mProgress = progress;
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

    public static class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> {
        private List<MovieItem> mItems;
        private OnMovieClickListener mListener;

        public interface OnMovieClickListener { void onMovieClick(MovieItem item); }

        public MovieCardAdapter(List<MovieItem> items, OnMovieClickListener listener) {
            mItems = items; mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater().inflate(R.layout.movie_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MovieItem item = mItems.get(position);
            holder.title.setText(item.getTitle());
            holder.subtitle.setText(item.getSubtitle());
            holder.card.setOnClickListener(v -> {
                if (mListener != null) mListener.onMovieClick(item);
            });
        }

        @Override
        public int getItemCount() { return mItems != null ? mItems.size() : 0; }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView card;
            TextView title, subtitle;

            ViewHolder(android.view.View itemView) {
                super(itemView);
                card = (CardView) itemView;
                title = itemView.findViewById(R.id.movie_title);
                subtitle = itemView.findViewById(R.id.movie_subtitle);
            }
        }
    }
}
