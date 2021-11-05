package com.fetel.ltdd.team9.share.recipes.ui.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class TagCategory extends Category {
    private boolean isCheck;

    private List<TagCategory> selectedCategories;

    public TagCategory() {
        super();
    }

    public TagCategory(String titleEn, String titleVi, boolean isSub, boolean isTopPage) {
        super(titleEn, titleVi, isSub, isTopPage);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public List<TagCategory> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<TagCategory> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }
}
