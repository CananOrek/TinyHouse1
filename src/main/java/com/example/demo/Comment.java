package com.example.demo;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Comment {
    private final StringProperty tenantName;
    private final StringProperty houseName;
    private final StringProperty commentText;
    private final IntegerProperty rating;
    private final ObjectProperty<LocalDate> commentDate;
    private final StringProperty replyText;


    public Comment(String tenantName, String houseName, String commentText, int rating, LocalDate commentDate, String replyText) {
        this.tenantName = new SimpleStringProperty(tenantName);
        this.houseName = new SimpleStringProperty(houseName);
        this.commentText = new SimpleStringProperty(commentText);
        this.rating = new SimpleIntegerProperty(rating);
        this.commentDate = new SimpleObjectProperty<>(commentDate);
        this.replyText = new SimpleStringProperty(replyText);
    }

    public StringProperty replyTextProperty() { return replyText; }
    public String getReplyText() { return replyText.get(); }
    public StringProperty tenantNameProperty() { return tenantName; }
    public StringProperty houseNameProperty() { return houseName; }
    public StringProperty commentTextProperty() { return commentText; }
    public IntegerProperty ratingProperty() { return rating; }
    public ObjectProperty<LocalDate> commentDateProperty() { return commentDate; }


    public String getCommentText() {

        return commentText.get();
    }

    public String getTenantName() {

        return tenantName.get();
    }

}
