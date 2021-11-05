package com.fetel.ltdd.team9.share.recipes.ui.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    private String id;
    private String title;
    private String description;
    private String url;
    private String foodIngredients;
    private String cookingRecipe;
    private List<String> tagCategories;
    private String userIdCreated;
    private Date createTime;
    private List<String> likes;

    public Post() {
    }

    public Post(String title, String description, String url, String foodIngredients, String cookingRecipe, List<String> tagCategories, String userIdCreated) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.foodIngredients = foodIngredients;
        this.cookingRecipe = cookingRecipe;
        this.tagCategories = tagCategories;
        this.userIdCreated = userIdCreated;
        this.createTime = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFoodIngredients() {
        return foodIngredients;
    }

    public void setFoodIngredients(String foodIngredients) {
        this.foodIngredients = foodIngredients;
    }

    public String getCookingRecipe() {
        return cookingRecipe;
    }

    public void setCookingRecipe(String cookingRecipe) {
        this.cookingRecipe = cookingRecipe;
    }

    public List<String> getTagCategories() {
        return tagCategories;
    }

    public void setTagCategories(List<String> tagCategories) {
        this.tagCategories = tagCategories;
    }

    public String getUserIdCreated() {
        return userIdCreated;
    }

    public void setUserIdCreated(String userIdCreated) {
        this.userIdCreated = userIdCreated;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", getTitle());
        result.put("description", getDescription());
        result.put("url", getUrl());
        result.put("foodIngredients", getFoodIngredients());
        result.put("cookingRecipe", getCookingRecipe());
        result.put("tagCategories", getTagCategories());
        result.put("userIdCreated", getUserIdCreated());
        result.put("createTime", getCreateTime());
        result.put("likes", getLikes());

        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", foodIngredients='" + foodIngredients + '\'' +
                ", cookingRecipe='" + cookingRecipe + '\'' +
                ", tagCategories=" + tagCategories +
                ", userIdCreated='" + userIdCreated + '\'' +
                ", createTime=" + createTime +
                ", likes=" + likes +
                '}';
    }
}
