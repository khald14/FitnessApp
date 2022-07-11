package com.example.fitness_app;

import java.io.Serializable;

public class Exercise implements Comparable<Exercise>, Serializable {

    private final String time;
    private final String description;
    private final String gifURL;
    private final String name;
    private final int rank;

    public Exercise(String time, String description, String gifURL, String name, int rank) {
        this.time = time;
        this.description = description;
        this.gifURL = gifURL;
        this.name = name;
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
    public String getTime() {
        return time;
    }
    public String getDescription() {
        return description;
    }
    public String getGifURL() {
        return gifURL;
    }
    public String getName() {
        return name;
    }
    @Override
    public int compareTo(Exercise exercise) {
        //  For Ascending order
        return this.rank - exercise.getRank();
    }
}
