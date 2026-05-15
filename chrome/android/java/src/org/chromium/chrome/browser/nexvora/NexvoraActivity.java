package org.chromium.chrome.browser.nexvora;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.ChromeTabbedActivity;

public class NexvoraActivity extends ChromeTabbedActivity {

    private NexvoraIntegration mNexvoraIntegration;

    @Override
    public void finishNativeInitialization() {
        super.finishNativeInitialization();

        if (mNexvoraIntegration == null) {
            mNexvoraIntegration = new NexvoraIntegration(this);
            mNexvoraIntegration.initialize();
        }
    }

    @Override
    public boolean handleBackPressed() {
        if (mNexvoraIntegration != null && mNexvoraIntegration.onBackPressed()) return true;
        return super.handleBackPressed();
    }

    public NexvoraIntegration getNexvoraIntegration() {
        return mNexvoraIntegration;
    }
}
