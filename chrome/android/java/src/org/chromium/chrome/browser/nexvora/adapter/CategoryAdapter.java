package org.chromium.chrome.browser.nexvora.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.model.CategoryItem;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryItem> mItems;
    private OnCategoryClickListener mListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem item);
    }

    public CategoryAdapter(List<CategoryItem> items, OnCategoryClickListener listener) {
        mItems = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nexvora_category_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = mItems.get(position);
        holder.mNameText.setText(item.getName());
        if (item.getIconResId() != 0) {
            holder.mIconImage.setImageResource(item.getIconResId());
        }
        holder.mCardView.setOnClickListener(v -> {
            if (mListener != null) mListener.onCategoryClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        ImageView mIconImage;
        TextView mNameText;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView;
            mIconImage = itemView.findViewById(R.id.nexvora_category_icon);
            mNameText = itemView.findViewById(R.id.nexvora_category_name);
        }
    }
}
