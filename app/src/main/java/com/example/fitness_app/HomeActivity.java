package com.example.fitness_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {


    private ArrayList<ExercisesPackage> exercisesPackages;
    private PackageAdapter packageAdapter;
    private RecyclerView packageRV;
    private TextView primaryPackageName;
    private TextView primaryPackageExNumber;
    private ImageView primaryPackageIV, profile;
    private ProgressBar progressBar;

    private String packageImageUrl;
    private String exercisesNumber;
    private String packageName;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set the elements from the view.
        primaryPackageName = findViewById(R.id.name_prime_video);
        primaryPackageExNumber = findViewById(R.id.videos_number_prime_video);
        primaryPackageIV = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progress_bar_of_home_screen);
        packageRV = findViewById(R.id.recycler_view_videos);
        profile = findViewById(R.id.imageView2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar.setVisibility(View.VISIBLE);
        primaryPackageIV.setVisibility(View.INVISIBLE);
        primaryPackageExNumber.setVisibility(View.INVISIBLE);
        primaryPackageName.setVisibility(View.INVISIBLE);
        packageRV.setVisibility(View.INVISIBLE);

        exercisesPackages = new ArrayList<>(); // arraylist of videos we get from firebase
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); //instance of firebaseFirestore

        //get all the exercises packages from the firebase.
        firebaseFirestore.collection("workouts_videos") // here we get the collection from database , and then get all the data in the collection
            .get().addOnCompleteListener(task -> {// once we have all the data, this function will start.

                //here is the attributes of the video.
                String name;
                String imageURL;
                Double exercises;
                int i = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {// for each document in the collection

                    name = document.getString("video-name"); // we get the data
                    imageURL = document.getString("video-pic");
                    exercises = document.getDouble("ex_num");
                    assert exercises != null;
                    ExercisesPackage exercisesPackage = new ExercisesPackage(name, imageURL, exercises.intValue()); // and make new video
                    if(i==0){
                        primaryPackageName.setText(name);
                        primaryPackageExNumber.setText(exercises.intValue() + " exercises");
                        Picasso.get().load(imageURL).into(primaryPackageIV);
                        packageImageUrl = imageURL;
                        packageName = name;
                        exercisesNumber = ""+exercises.intValue();

                    }else
                        exercisesPackages.add(exercisesPackage);// then add the video to the arraylist
                    i++;
                }
            }).addOnCompleteListener(task -> {

                //after we finnish getting the data, we pass the data to the adapter to show it on the screen.

                packageAdapter = new PackageAdapter(HomeActivity.this, exercisesPackages);
                packageRV.setAdapter(packageAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
                packageRV.setLayoutManager(linearLayoutManager);

                progressBar.setVisibility(View.INVISIBLE);
                primaryPackageIV.setVisibility(View.VISIBLE);
                primaryPackageExNumber.setVisibility(View.VISIBLE);
                primaryPackageName.setVisibility(View.VISIBLE);
                packageRV.setVisibility(View.VISIBLE);

            });

        primaryPackageIV.setOnClickListener(view -> {
            Intent intent=new Intent(HomeActivity.this, SpecificExerciseActivity.class);
            intent.putExtra("name", packageName);
            intent.putExtra("exercises_num", exercisesNumber);
            intent.putExtra("image_url", packageImageUrl);
            intent.putExtra("vid_num","1");
            startActivity(intent);
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });
    }
}