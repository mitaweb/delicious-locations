package com.fetel.ltdd.team9.share.recipes.ui.model;

public class User {
    private String id;
    private String displayName;
    private String email;
    private String url;

    public User() {
    }

    public User(String displayName, String email, String url) {
        this.displayName = displayName;
        this.email = email;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                ", displayName='" + getDisplayName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", url='" + getUrl() + '\'' +
                '}';
    }
}
