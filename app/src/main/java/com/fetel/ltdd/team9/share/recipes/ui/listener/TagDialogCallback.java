package com.fetel.ltdd.team9.share.recipes.ui.listener;

import com.fetel.ltdd.team9.share.recipes.ui.model.TagCategory;

import java.util.List;

public interface TagDialogCallback {
    void onTagSelected(List<TagCategory> tagCategories, int position);
}