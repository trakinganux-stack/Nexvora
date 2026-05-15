package org.chromium.chrome.browser.nexvora.model;

public class CategoryItem {
    private String mId;
    private String mName;
    private int mIconResId;

    public CategoryItem(String id, String name, int iconResId) {
        mId = id;
        mName = name;
        mIconResId = iconResId;
    }

    public String getId() { return mId; }
    public String getName() { return mName; }
    public int getIconResId() { return mIconResId; }
}
