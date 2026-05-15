package org.chromium.chrome.browser.nexvora.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.model.MovieItem;

import java.util.List;

public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> {

    private List<MovieItem> mItems;
    private OnMovieClickListener mListener;

    public interface OnMovieClickListener {
        void onMovieClick(MovieItem item);
    }

    public MovieCardAdapter(List<MovieItem> items, OnMovieClickListener listener) {
        mItems = items;
        mListener = listener;
    }

    public void updateItems(List<MovieItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nexvora_movie_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieItem item = mItems.get(position);
        holder.mTitleText.setText(item.getTitle());
        holder.mSubtitleText.setText(item.getSubtitle());

        if (item.isContinueWatching()) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mProgressBar.setProgress((int) (item.getProgress() * 100));
        } else {
            holder.mProgressBar.setVisibility(View.GONE);
        }

        if (item.getRating() > 0) {
            holder.mRatingBar.setVisibility(View.VISIBLE);
            holder.mRatingBar.setRating(item.getRating());
        } else {
            holder.mRatingBar.setVisibility(View.GONE);
        }

        holder.mCardView.setOnClickListener(v -> {
            if (mListener != null) mListener.onMovieClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        ImageView mPosterImage;
        TextView mTitleText;
        TextView mSubtitleText;
        RatingBar mRatingBar;
        ProgressBar mProgressBar;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView;
            mPosterImage = itemView.findViewById(R.id.nexvora_movie_poster);
            mTitleText = itemView.findViewById(R.id.nexvora_movie_title);
            mSubtitleText = itemView.findViewById(R.id.nexvora_movie_subtitle);
            mRatingBar = itemView.findViewById(R.id.nexvora_movie_rating);
            mProgressBar = itemView.findViewById(R.id.nexvora_movie_progress);
        }
    }
}
