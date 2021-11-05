package com.fetel.ltdd.team9.share.recipes.ui.model;

import android.content.Context;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Locale;

@IgnoreExtraProperties
public class Category {
    private String id;
    private String titleEn;
    private String titleVi;
    private String url;
    private boolean isSub;
    private boolean isTopPage;

    public Category() {
    }

    public Category(String titleEn, String titleVi, boolean isSub, boolean isTopPage) {
        this.titleEn = titleEn;
        this.titleVi = titleVi;
        this.isSub = isSub;
        this.isTopPage = isTopPage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleVi() {
        return titleVi;
    }

    public void setTitleVi(String titleVi) {
        this.titleVi = titleVi;
    }

    public String getTitle(Context context) {
        if (context == null) {
            return getTitleEn();
        }
        Locale current = context.getResources().getConfiguration().locale;
        if (current.getCountry().equals("VN")) {
            return getTitleVi();
        } else {
            return getTitleEn();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSub() {
        return isSub;
    }

    public void setSub(boolean sub) {
        isSub = sub;
    }

    public boolean isTopPage() {
        return isTopPage;
    }

    public void setTopPage(boolean topPage) {
        isTopPage = topPage;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", titleVi='" + titleVi + '\'' +
                ", url='" + url + '\'' +
                ", isSub=" + isSub +
                ", isTopPage=" + isTopPage +
                '}';
    }
}
