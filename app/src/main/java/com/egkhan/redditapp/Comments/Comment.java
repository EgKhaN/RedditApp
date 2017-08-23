package com.egkhan.redditapp.Comments;

import org.simpleframework.xml.Element;

/**
 * Created by EgK on 8/23/2017.
 */

public class Comment {
    @Element(name = "id")
    private String id;
    @Element(name = "author")
    private String author;
    @Element(name = "comment")
    private String comment;
    @Element(name = "updated")
    private String updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Comment(String id, String author, String comment, String updated) {
        this.id = id;
        this.author = author;
        this.comment = comment;
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", author=" + author +
                ", comment='" + comment + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }
}
