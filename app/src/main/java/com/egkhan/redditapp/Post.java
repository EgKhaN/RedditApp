package com.egkhan.redditapp;

/**
 * Created by EgK on 8/22/2017.
 */

public class Post {
    private String title;
    private String author;
    private String date_updated;
    private String postUrl;
    private String thumbnailUrl;
    private String id;

    public Post(String title, String author, String date_updated, String postUrl, String thumbnailUrl, String id) {
        this.title = title;
        this.author = author;
        this.date_updated = date_updated;
        this.postUrl = postUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
