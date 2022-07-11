package com.example.fitness_app;

public class User {

    //The user of the system.
    public String username, userUID, height, weight, targetWeight;

    public User() {
    }

    public User(String username, String userUID, String height, String weight, String targetWeight) {
        this.username = username;
        this.userUID = userUID;
        this.height = height;
        this.weight = weight;
        this.targetWeight = targetWeight;
    }

    //getters and setters.
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
    }
}
