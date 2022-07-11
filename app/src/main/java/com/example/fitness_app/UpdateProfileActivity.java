package com.example.fitness_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText newUserName;
    private EditText newUserHeight;
    private EditText newUserWeight;
    private EditText newUSerTarget;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ImageView getNewImageInImageView;
    private StorageReference storageReference;
    private String ImageURIAccessToken;
    private ProgressBar progressBarOfUpdateProfile;
    private Uri imagePath;
    Intent intent;

    private static final int PICK_IMAGE = 123;

    android.widget.Button updateProfileButton;
    String newName;
    String newHeight;
    String newWeight;
    String newTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //get the view elements into activity
        androidx.appcompat.widget.Toolbar toolbarOfUpdateProfile = findViewById(R.id.tool_bar_of_update_profile);
        ImageButton backButtonOfUpdateProfile = findViewById(R.id.back_button_of_update_profile);
        getNewImageInImageView = findViewById(R.id.get_new_user_image_in_image_view);
        progressBarOfUpdateProfile = findViewById(R.id.progress_bar_of_update_profile);
        newUserName = findViewById(R.id.get_new_username);
        newUserWeight = findViewById(R.id.get_new_weight);
        newUserHeight = findViewById(R.id.get_new_height);
        newUSerTarget = findViewById(R.id.get_new_wanted_weight);
        updateProfileButton = findViewById(R.id.update_profile_button);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        intent = getIntent();

        setSupportActionBar(toolbarOfUpdateProfile);

        backButtonOfUpdateProfile.setOnClickListener(view -> finish());

        //set the data on the text views
        newUserName.setText(intent.getStringExtra("name_of_user"));
        newUserWeight.setText(intent.getStringExtra("weight_of_user"));
        newUserHeight.setText(intent.getStringExtra("height_of_user"));
        newUSerTarget.setText(intent.getStringExtra("target_of_user"));

        // save the data in the database
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        updateProfileButton.setOnClickListener(view -> {
            newName = newUserName.getText().toString();
            newHeight = newUserHeight.getText().toString();
            newWeight = newUserWeight.getText().toString();
            newTarget = newUSerTarget.getText().toString();

            //check if fields are empty before saving.
            if (newName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Name Is Empty", Toast.LENGTH_SHORT).show();
            } else {
                if (newTarget.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Target Is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (newHeight.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Height Is Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        if (newWeight.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Weight Is Empty", Toast.LENGTH_SHORT).show();
                        } else if (imagePath != null) {
                            progressBarOfUpdateProfile.setVisibility(View.VISIBLE);
                            User userProfile = new User(newName, firebaseAuth.getUid(), newHeight, newWeight, newTarget);
                            databaseReference.setValue(userProfile);

                            updateImageToStorage();

                            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                            progressBarOfUpdateProfile.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressBarOfUpdateProfile.setVisibility(View.VISIBLE);
                            User userProfile = new User(newName, firebaseAuth.getUid(), newHeight, newWeight, newTarget);
                            databaseReference.setValue(userProfile);
                            updateNameOnCloudFirestore();
                            Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                            progressBarOfUpdateProfile.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });

        //pick image from device.
        getNewImageInImageView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });
        //save image into storage database.
        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(uri -> {
            ImageURIAccessToken = uri.toString();
            Picasso.get().load(uri).into(getNewImageInImageView);
        });
    }

    private void updateNameOnCloudFirestore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name", newName);
        userdata.put("image", ImageURIAccessToken);
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("height", newHeight);
        userdata.put("weight", newWeight);
        userdata.put("target_weight", newTarget);
        documentReference.set(userdata).addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show());
    }

    private void updateImageToStorage() {

        StorageReference imageRef = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");
        //Image compression
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        ///putting image to storage
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                ImageURIAccessToken = uri.toString();
                Toast.makeText(getApplicationContext(), "URI get success", Toast.LENGTH_SHORT).show();
                updateNameOnCloudFirestore();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "URI get Failed", Toast.LENGTH_SHORT).show());
            Toast.makeText(getApplicationContext(), "Image is Updated", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Image Not Updated", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imagePath = data.getData();
            getNewImageInImageView.setImageURI(imagePath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}