package com.example.fitness_app;

public class ExercisesPackage {

    private final String name;
    private final String imageURL;
    private final int exercises;

    public ExercisesPackage(String name, String imageURL, int num) {
        this.name = name;
        this.imageURL = imageURL;
        exercises = num;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getExercises() {
        return exercises;
    }
}
