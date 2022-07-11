package com.example.fitness_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {


    EditText viewUserName;
    EditText viewUserHeight;
    EditText viewUserWeight;
    EditText viewUserTarget;

    ImageView name, height, weight, target, profileImage;
    TextView wantToUpdateProfileText;
    androidx.cardview.widget.CardView cardView;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    TextView moveToUpdateProfile;
    ImageView viewUserImageInImageView;
    ProgressBar progressBar;

    androidx.appcompat.widget.Toolbar toolBarOfViewProfile;
    ImageButton backButtonOfViewProfile;


    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewUserImageInImageView = findViewById(R.id.view_user_image_in_image_view);
        viewUserName = findViewById(R.id.view_user_name);
        viewUserHeight = findViewById(R.id.view_user_height);
        viewUserWeight = findViewById(R.id.view_user_weight);
        viewUserTarget = findViewById(R.id.view_user_target);
        moveToUpdateProfile = findViewById(R.id.move_to_update_profile);
        toolBarOfViewProfile = findViewById(R.id.tool_bar_of_view_profile);
        backButtonOfViewProfile = findViewById(R.id.back_button_of_view_profile);
        progressBar = findViewById(R.id.progress_bar_for_profile);
        name = findViewById(R.id.logo_of_view_profile);
        weight = findViewById(R.id.logo_of_view_weight);
        height = findViewById(R.id.logo_of_view_height);
        target = findViewById(R.id.logo_of_view_target);
        cardView = findViewById(R.id.view_user_image);

        progressBar.setVisibility(View.VISIBLE);
        name.setVisibility(View.INVISIBLE);
        weight.setVisibility(View.INVISIBLE);
        height.setVisibility(View.INVISIBLE);
        target.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        viewUserImageInImageView.setVisibility(View.INVISIBLE);
        moveToUpdateProfile.setVisibility(View.INVISIBLE);
        viewUserName.setVisibility(View.INVISIBLE);
        viewUserTarget.setVisibility(View.INVISIBLE);
        viewUserWeight.setVisibility(View.INVISIBLE);
        viewUserHeight.setVisibility(View.INVISIBLE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolBarOfViewProfile);
        backButtonOfViewProfile.setOnClickListener(view -> finish());


        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(uri -> {

            Picasso.get().load(uri).into(viewUserImageInImageView);

        });

        //get data from firebase.
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                viewUserName.setText(userProfile.getUsername());
                viewUserHeight.setText(userProfile.getHeight());
                viewUserWeight.setText(userProfile.getWeight());
                viewUserTarget.setText(userProfile.getTargetWeight());
                progressBar.setVisibility(View.INVISIBLE);
                name.setVisibility(View.VISIBLE);
                weight.setVisibility(View.VISIBLE);
                height.setVisibility(View.VISIBLE);
                target.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
                viewUserImageInImageView.setVisibility(View.VISIBLE);
                moveToUpdateProfile.setVisibility(View.VISIBLE);
                viewUserName.setVisibility(View.VISIBLE);
                viewUserTarget.setVisibility(View.VISIBLE);
                viewUserWeight.setVisibility(View.VISIBLE);
                viewUserHeight.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Failed To Fetch", Toast.LENGTH_SHORT).show();
            }
        });



        moveToUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass the data to the next activity
                Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                intent.putExtra("name_of_user", viewUserName.getText().toString());
                intent.putExtra("weight_of_user", viewUserWeight.getText().toString());
                intent.putExtra("height_of_user", viewUserHeight.getText().toString());
                intent.putExtra("target_of_user", viewUserTarget.getText().toString());
                startActivity(intent);
            }
        });
    }
}
