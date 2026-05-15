package org.chromium.chrome.browser.nexvora.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.nexvora.NexvoraCoordinator;

public class ExtensionsFragment extends Fragment {

    private LinearLayout mExtensionsList;
    private View mEmptyState;
    private NexvoraCoordinator mCoordinator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nexvora_fragment_extensions, container, false);

        mExtensionsList = view.findViewById(R.id.nexvora_extensions_list);
        mEmptyState = view.findViewById(R.id.nexvora_extensions_empty);

        Button installButton = view.findViewById(R.id.nexvora_extensions_install);
        installButton.setOnClickListener(v -> openChromeWebStore());

        Button manageButton = view.findViewById(R.id.nexvora_extensions_manage);
        manageButton.setOnClickListener(v -> openExtensionManager());

        return view;
    }

    public void setCoordinator(NexvoraCoordinator coordinator) {
        mCoordinator = coordinator;
    }

    public void addExtension(String name, String id, boolean isEnabled) {
        mEmptyState.setVisibility(View.GONE);
        mExtensionsList.setVisibility(View.VISIBLE);

        View extView = LayoutInflater.from(getContext())
                .inflate(R.layout.nexvora_extension_item, mExtensionsList, false);

        TextView nameText = extView.findViewById(R.id.nexvora_extension_name);
        TextView idText = extView.findViewById(R.id.nexvora_extension_id);
        Switch toggle = extView.findViewById(R.id.nexvora_extension_toggle);

        nameText.setText(name);
        idText.setText(id);
        toggle.setChecked(isEnabled);

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(),
                    (isChecked ? "Enabled: " : "Disabled: ") + name,
                    Toast.LENGTH_SHORT).show();
        });

        mExtensionsList.addView(extView);
    }

    private void openChromeWebStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://chrome.google.com/webstore"));
        startActivity(intent);
    }

    private void openExtensionManager() {
        if (mCoordinator != null) {
            mCoordinator.loadUrl("chrome://extensions");
        }
    }
}
