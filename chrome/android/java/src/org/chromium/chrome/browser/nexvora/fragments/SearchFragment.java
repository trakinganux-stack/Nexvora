package org.chromium.chrome.browser.nexvora.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.adapter.MovieCardAdapter;
import org.chromium.chrome.browser.nexvora.model.MovieItem;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText mSearchInput;
    private RecyclerView mResultsList;
    private View mEmptyState;
    private TextView mTrendingLabel;
    private MovieCardAdapter mResultsAdapter;
    private View mClearButton;
    private Handler mSearchHandler;
    private Runnable mSearchRunnable;

    private String[] mAllTitles = {
        "Web Explorer", "Video Stream", "Social Hub", "News Today",
        "Music World", "Research Article", "Tutorial Series", "Documentary",
        "Developer Docs", "Creative Studio", "Gaming Zone", "Shopping Mall",
        "Travel Guide", "Fitness Hub", "Weather Center", "Stock Market"
    };

    private String[] mAllUrls = {
        "https://www.google.com", "https://www.youtube.com", "https://www.reddit.com",
        "https://news.google.com", "https://open.spotify.com", "https://en.wikipedia.org",
        "https://www.khanacademy.org", "https://www.nationalgeographic.com",
        "https://developer.mozilla.org", "https://dribbble.com",
        "https://store.steampowered.com", "https://www.amazon.com",
        "https://www.tripadvisor.com", "https://www.myfitnesspal.com",
        "https://weather.com", "https://finance.yahoo.com"
    };

    private MovieCardAdapter.OnMovieClickListener mMovieClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nexvora_fragment_search, container, false);

        mSearchInput = view.findViewById(R.id.nexvora_search_input);
        mResultsList = view.findViewById(R.id.nexvora_search_results);
        mEmptyState = view.findViewById(R.id.nexvora_search_empty);
        mTrendingLabel = view.findViewById(R.id.nexvora_search_trending_label);
        mClearButton = view.findViewById(R.id.nexvora_search_clear);

        mResultsAdapter = new MovieCardAdapter(new ArrayList<>(), item -> {
            if (mMovieClickListener != null) mMovieClickListener.onMovieClick(item);
        });
        mResultsList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mResultsList.setAdapter(mResultsAdapter);

        mSearchHandler = new Handler();

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (mSearchHandler != null && mSearchRunnable != null) {
                    mSearchHandler.removeCallbacks(mSearchRunnable);
                }
                mSearchRunnable = () -> performSearch(s.toString());
                mSearchHandler.postDelayed(mSearchRunnable, 300);
                mClearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        mSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(mSearchInput.getText().toString());
                return true;
            }
            return false;
        });

        mClearButton.setOnClickListener(v -> {
            mSearchInput.setText("");
            mResultsList.setVisibility(View.GONE);
            mEmptyState.setVisibility(View.VISIBLE);
            mTrendingLabel.setText(getString(R.string.nexvora_trending_searches));
        });

        loadTrendingContent();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSearchHandler != null) {
            mSearchHandler.removeCallbacksAndMessages(null);
            mSearchHandler = null;
        }
    }

    public void setMovieClickListener(MovieCardAdapter.OnMovieClickListener listener) {
        mMovieClickListener = listener;
    }

    private void loadTrendingContent() {
        List<MovieItem> trending = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            trending.add(new MovieItem("s" + i, mAllTitles[i], mAllUrls[i].replace("https://", ""),
                    null, null, mAllUrls[i], "Trending", 4.5f, false, 0));
        }
        mResultsAdapter.updateItems(trending);
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadTrendingContent();
            mEmptyState.setVisibility(View.GONE);
            mResultsList.setVisibility(View.VISIBLE);
            mTrendingLabel.setText(getString(R.string.nexvora_trending_searches));
            return;
        }

        String lower = query.toLowerCase();
        List<MovieItem> results = new ArrayList<>();

        for (int i = 0; i < mAllTitles.length; i++) {
            if (mAllTitles[i].toLowerCase().contains(lower)
                    || mAllUrls[i].toLowerCase().contains(lower)) {
                results.add(new MovieItem("r" + i, mAllTitles[i],
                        mAllUrls[i].replace("https://", ""),
                        null, null, mAllUrls[i], "Search Result", 0, false, 0));
            }
        }

        if (results.isEmpty()) {
            try {
                results.add(new MovieItem("web", "Search Web",
                        "Open \"" + query + "\" in browser",
                        null, null,
                        "https://www.google.com/search?q="
                        + java.net.URLEncoder.encode(query, "UTF-8"),
                        "Web Search", 0, false, 0));
            } catch (Exception e) {
                results.add(new MovieItem("web", "Search Web",
                        "Open \"" + query + "\" in browser",
                        null, null,
                        "https://www.google.com/search?q=" + query.replace(" ", "+"),
                        "Web Search", 0, false, 0));
            }
        }

        mResultsAdapter.updateItems(results);
        mResultsList.setVisibility(View.VISIBLE);
        mEmptyState.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
        mTrendingLabel.setText(results.size() + " result" + (results.size() != 1 ? "s" : ""));
    }
}
