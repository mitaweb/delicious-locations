package com.fetel.ltdd.team9.share.recipes.ui.enums;

public enum ExtraKeyType {
    DEFAULT_EXTRA("default_extra"),
    EXTRA_KEY_CATEGORY_ID("category_id"),
    EXTRA_KEY_CATEGORY_PATH("category_path"),
    EXTRA_KEY_CATEGORY_POSITION("category_position"),
    EXTRA_KEY_POST_ID("post_id"),
    EXTRA_KEY_SEARCH_KEY("search_key");

    private String value;

    ExtraKeyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
