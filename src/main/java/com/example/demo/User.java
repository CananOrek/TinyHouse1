package com.example.demo;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty userID;
    private final StringProperty fullName;
    private final StringProperty email;
    private final StringProperty role;
    private final BooleanProperty isActive;


    public User(int userID, String fullName, String email, String role, boolean isActive) {
        this.userID = new SimpleIntegerProperty(userID);
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
        this.isActive = new SimpleBooleanProperty(isActive);
    }

    public IntegerProperty userIDProperty() {
        return userID;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty roleProperty() {
        return role;
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public int getUserID() {
        return userID.get();
    }

    public boolean getIsActive() {
        return isActive.get();
    }
}
