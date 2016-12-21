package me.storm.ninegag.model;

/**
 * Created by storm on 14-3-25.
 */
public enum Category {
    hot("美女"), trending("动漫"), fresh("明星");
    //返回枚举的描述
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
