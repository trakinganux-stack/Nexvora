package org.chromium.chrome.browser.nexvora.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.NexvoraCoordinator;

public class SettingsFragment extends Fragment {

    private NexvoraCoordinator mCoordinator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nexvora_fragment_settings, container, false);

        setupSetting(view, R.id.nexvora_settings_privacy, "Privacy & Security",
                "Manage cookies, cache, and site data", () -> {
            if (mCoordinator != null) mCoordinator.loadUrl("chrome://settings/privacy");
        });

        setupSetting(view, R.id.nexvora_settings_extensions, "Extensions",
                "Manage installed extensions", () -> {
            if (mCoordinator != null) mCoordinator.loadUrl("chrome://extensions");
        });

        setupSetting(view, R.id.nexvora_settings_search_engine, "Search Engine",
                "Change default search provider", () -> {
            if (mCoordinator != null) mCoordinator.loadUrl("chrome://settings/searchEngines");
        });

        setupSetting(view, R.id.nexvora_settings_cookies, "Cookie Editor",
                "View, edit, import, and export cookies", () -> {
            if (mCoordinator != null) mCoordinator.loadUrl("chrome://settings/content/cookies");
        });

        setupSetting(view, R.id.nexvora_settings_notifications, "Notifications",
                "Manage notification preferences", () -> {
            Toast.makeText(getContext(), "Notification settings", Toast.LENGTH_SHORT).show();
        });

        setupSetting(view, R.id.nexvora_settings_about, "About Nexvora",
                "Version 1.0.0", () -> {
            Toast.makeText(getContext(), "Nexvora v1.0.0 - Powered by Chromium",
                    Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    public void setCoordinator(NexvoraCoordinator coordinator) {
        mCoordinator = coordinator;
    }

    private void setupSetting(View parent, int viewId, String title,
                              String summary, Runnable onClick) {
        View setting = parent.findViewById(viewId);
        if (setting == null) return;

        TextView titleText = setting.findViewById(R.id.nexvora_setting_title);
        TextView summaryText = setting.findViewById(R.id.nexvora_setting_summary);

        if (titleText != null) titleText.setText(title);
        if (summaryText != null) summaryText.setText(summary);

        setting.setOnClickListener(v -> {
            if (onClick != null) onClick.run();
        });
    }
}
