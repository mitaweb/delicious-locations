package com.fetel.ltdd.team9.share.recipes.ui.enums;

import android.content.Intent;

public enum DetailType {
    LIST_SUB,
    LIST_POST,
    SHARE_RECIPE,
    SEARCH_ALL_POST;

    private static final String name = DetailType.class.getName();

    public void attachTo(Intent intent) {
        intent.putExtra(name, ordinal());
    }

    public static DetailType detachFrom(Intent intent) {
        if (!intent.hasExtra(name)) throw new IllegalStateException();
        return values()[intent.getIntExtra(name, -1)];
    }
}
