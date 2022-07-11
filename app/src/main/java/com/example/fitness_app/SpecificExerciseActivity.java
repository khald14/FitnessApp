package com.example.fitness_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;

public class SpecificExerciseActivity extends AppCompatActivity {

    private ArrayList<Exercise> exercises;
    private ExerciseAdapter exerciseAdapter ;
    private RecyclerView exercisesRV;
    private TextView videoName;
    private TextView videoExNumber;
    private ImageView videoIV;
    private ProgressBar progressBar;
    private Button go;
    private String name;
    private String number;
    private String imageUrl;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific);

        //get the data from the previous activity.
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("exercises_num");
        imageUrl = getIntent().getStringExtra("image_url");

        videoName = findViewById(R.id.name_prime_video);
        videoExNumber = findViewById(R.id.videos_number_prime_video);
        videoIV = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progress_bar_of_specific_screen);
        exercisesRV = findViewById(R.id.recycler_view_exercises);
        go = findViewById(R.id.button);

        videoName.setText(name);
        videoExNumber.setText(number + " exercises");
        Picasso.get().load(imageUrl).into(videoIV);

        //set the fields to be invisible until the data fetched from the firebase.
        progressBar.setVisibility(View.VISIBLE);
        videoIV.setVisibility(View.INVISIBLE);
        videoExNumber.setVisibility(View.INVISIBLE);
        videoName.setVisibility(View.INVISIBLE);
        exercisesRV.setVisibility(View.INVISIBLE);

        exercises = new ArrayList<>(); // arraylist of videos we get from firebase
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //instance of firebaseFirestore

        String doc = "video-"+getIntent().getStringExtra("vid_num");
        firebaseFirestore.collection("workouts_videos").document(doc).collection("exercises")// here we get the collection from database , and then get all the data in the collection
                .get().addOnCompleteListener(task -> {// once we have all the data, this function will start.

                    //here is the attributes of the exercise.
                     String time;
                     String description;
                     String gifURL;
                     String name;
                     String rank;

                    for (QueryDocumentSnapshot document : task.getResult()) {// for each document in the collection

                        name = document.getString("name"); // we get the data
                        description = document.getString("description");
                        gifURL = document.getString("gif-url");
                        time = document.getString("Time");
                        rank = document.getString("rank");
                        assert rank != null;
                        Exercise exercise = new Exercise(time, description, gifURL, name, Integer.parseInt(rank)); // and make new exercise
                        exercises.add(exercise);// then add the video to the arraylist
                    }
                }).addOnCompleteListener(task -> {

                    //after we finnish getting the data, we pass the data to the adapter to show it on the screen.

                    //sort the array of packages to display the exercises inorder.
                    Collections.sort(exercises);
                    exerciseAdapter = new ExerciseAdapter(SpecificExerciseActivity.this, exercises);
                    exercisesRV.setAdapter(exerciseAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SpecificExerciseActivity.this, LinearLayoutManager.VERTICAL, false);
                    exercisesRV.setLayoutManager(linearLayoutManager);

                    //display the fields after getting the data from the firebase.
                    progressBar.setVisibility(View.INVISIBLE);
                    videoIV.setVisibility(View.VISIBLE);
                    videoExNumber.setVisibility(View.VISIBLE);
                    videoName.setVisibility(View.VISIBLE);
                    exercisesRV.setVisibility(View.VISIBLE);

                });

        //button to start the exercises.
        go.setOnClickListener(view -> {
            Intent intent = new Intent(SpecificExerciseActivity.this, ExerciseStartedActivity.class);
            intent.putExtra("list",exercises);
            startActivity(intent);
        });
    }
}