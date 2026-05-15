package org.chromium.chrome.browser.nexvora.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.chromium.chrome.R;

public class DownloadsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nexvora_fragment_downloads, container, false);

        TextView emptyText = view.findViewById(R.id.nexvora_downloads_empty_text);
        emptyText.setText(R.string.nexvora_no_downloads);

        return view;
    }
}
