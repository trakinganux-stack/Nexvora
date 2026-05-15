package org.chromium.chrome.browser.nexvora.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.adapter.MovieCardAdapter;
import org.chromium.chrome.browser.nexvora.model.MovieItem;
import org.chromium.chrome.browser.nexvora.ui.CinematicHeroView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private CinematicHeroView mHeroView;
    private RecyclerView mTrendingList;
    private RecyclerView mContinueWatchingList;
    private RecyclerView mRecommendedList;
    private RecyclerView mPopularList;
    private MovieCardAdapter mTrendingAdapter;
    private MovieCardAdapter mContinueAdapter;
    private MovieCardAdapter mRecommendedAdapter;
    private MovieCardAdapter mPopularAdapter;
    private View mLoadingShimmer;
    private MovieCardAdapter.OnMovieClickListener mMovieClickListener;
    private Handler mLoadHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nexvora_fragment_home, container, false);

        mHeroView = view.findViewById(R.id.nexvora_home_hero);
        mLoadingShimmer = view.findViewById(R.id.nexvora_home_loading);

        mTrendingList = view.findViewById(R.id.nexvora_home_trending_list);
        mContinueWatchingList = view.findViewById(R.id.nexvora_home_continue_list);
        mRecommendedList = view.findViewById(R.id.nexvora_home_recommended_list);
        mPopularList = view.findViewById(R.id.nexvora_home_popular_list);

        setupRecyclerViews();
        loadContent();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLoadHandler != null) {
            mLoadHandler.removeCallbacksAndMessages(null);
            mLoadHandler = null;
        }
    }

    public void setMovieClickListener(MovieCardAdapter.OnMovieClickListener listener) {
        mMovieClickListener = listener;
    }

    private void setupRecyclerViews() {
        mTrendingAdapter = new MovieCardAdapter(new ArrayList<>(), mMovieClickListener);
        mContinueAdapter = new MovieCardAdapter(new ArrayList<>(), mMovieClickListener);
        mRecommendedAdapter = new MovieCardAdapter(new ArrayList<>(), mMovieClickListener);
        mPopularAdapter = new MovieCardAdapter(new ArrayList<>(), mMovieClickListener);

        mTrendingList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mTrendingList.setAdapter(mTrendingAdapter);

        mContinueWatchingList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mContinueWatchingList.setAdapter(mContinueAdapter);

        mRecommendedList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecommendedList.setAdapter(mRecommendedAdapter);

        mPopularList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mPopularList.setAdapter(mPopularAdapter);
    }

    private void loadContent() {
        String[] titles = {"Nexvora Originals", "Trending Now", "Must Watch"};
        String[] subtitles = {"Experience cinematic browsing", "Discover what's popular",
                "Curated for you"};
        int[] colors = {0xFF8B0000, 0xFF1A1A2E, 0xFF16213E};
        mHeroView.setHeroContent(titles, subtitles, colors);

        mHeroView.setActionClickListener(v -> {
        });

        mLoadHandler = new Handler();
        mLoadHandler.postDelayed(() -> {
            if (!isAdded()) return;
            mLoadingShimmer.setVisibility(View.GONE);
            populateSampleData();
        }, 1500);
    }

    private void populateSampleData() {
        List<MovieItem> trending = new ArrayList<>();
        trending.add(new MovieItem("t1", "Web Explorer", "Browse the web", null, null,
                "https://www.google.com", "Trending", 4.5f, false, 0));
        trending.add(new MovieItem("t2", "Video Stream", "Watch videos", null, null,
                "https://www.youtube.com", "Trending", 4.8f, false, 0));
        trending.add(new MovieItem("t3", "Social Hub", "Stay connected", null, null,
                "https://www.reddit.com", "Trending", 4.2f, false, 0));
        trending.add(new MovieItem("t4", "News Today", "Latest updates", null, null,
                "https://news.google.com", "Trending", 4.0f, false, 0));
        trending.add(new MovieItem("t5", "Music World", "Listen & discover", null, null,
                "https://open.spotify.com", "Trending", 4.6f, false, 0));
        mTrendingAdapter.updateItems(trending);

        List<MovieItem> continueWatching = new ArrayList<>();
        continueWatching.add(new MovieItem("c1", "Research Article", "Technology", null, null,
                "https://en.wikipedia.org", "Continue", 4.3f, true, 0.65f));
        continueWatching.add(new MovieItem("c2", "Tutorial Series", "Learning", null, null,
                "https://www.khanacademy.org", "Continue", 4.7f, true, 0.30f));
        continueWatching.add(new MovieItem("c3", "Documentary", "Education", null, null,
                "https://www.nationalgeographic.com", "Continue", 4.9f, true, 0.80f));
        mContinueAdapter.updateItems(continueWatching);

        List<MovieItem> recommended = new ArrayList<>();
        recommended.add(new MovieItem("r1", "Developer Docs", "Programming", null, null,
                "https://developer.mozilla.org", "Recommended", 4.4f, false, 0));
        recommended.add(new MovieItem("r2", "Creative Studio", "Design", null, null,
                "https://dribbble.com", "Recommended", 4.1f, false, 0));
        recommended.add(new MovieItem("r3", "Gaming Zone", "Entertainment", null, null,
                "https://store.steampowered.com", "Recommended", 4.5f, false, 0));
        mRecommendedAdapter.updateItems(recommended);

        List<MovieItem> popular = new ArrayList<>();
        popular.add(new MovieItem("p1", "Shopping Mall", "Online shopping", null, null,
                "https://www.amazon.com", "Popular", 4.6f, false, 0));
        popular.add(new MovieItem("p2", "Travel Guide", "Explore places", null, null,
                "https://www.tripadvisor.com", "Popular", 4.3f, false, 0));
        popular.add(new MovieItem("p3", "Fitness Hub", "Health & wellness", null, null,
                "https://www.myfitnesspal.com", "Popular", 4.0f, false, 0));
        mPopularAdapter.updateItems(popular);
    }
}
