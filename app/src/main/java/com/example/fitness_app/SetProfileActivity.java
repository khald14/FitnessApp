package com.example.fitness_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfileActivity extends AppCompatActivity {

    private ImageView getUserImageInImageView;
    private static final int PICK_IMAGE=123;
    private Uri imagePath;
    private EditText getUserName;
    private EditText getUserHeight;
    private EditText getUserWeight;
    private EditText getUserTargetWeight;
    private FirebaseAuth firebaseAuth;
    private String name, height, weight, target;
    private StorageReference storageReference;
    private String ImageUriAccessToken;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBarOfSetProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //get the view elements into activity
        getUserName =findViewById(R.id.get_user_name);
        getUserHeight =findViewById(R.id.get_user_height);
        getUserWeight =findViewById(R.id.get_user_weight);
        getUserTargetWeight =findViewById(R.id.get_user_wanted_weight);


        CardView getUserImage = findViewById(R.id.get_user_image);
        getUserImageInImageView =findViewById(R.id.get_user_image_in_image_view);
        android.widget.Button saveProfile = findViewById(R.id.saveProfile);
        progressBarOfSetProfile =findViewById(R.id.progressbarofsetProfile);


        getUserImage.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent,PICK_IMAGE);
        });

        // save the data in the database
        saveProfile.setOnClickListener(view -> {
            name= getUserName.getText().toString();
            height= getUserHeight.getText().toString();
            weight = getUserWeight.getText().toString();
            target = getUserTargetWeight.getText().toString();

            //check if fields are empty before saving.
            if(name.isEmpty())
                Toast.makeText(getApplicationContext(),"Name Is Empty",Toast.LENGTH_SHORT).show();
            else{
                if(target.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Target Weight Is Empty",Toast.LENGTH_SHORT).show();
                }else {
                    if (height.isEmpty())
                        Toast.makeText(getApplicationContext(), "Height Is Empty", Toast.LENGTH_SHORT).show();
                    else {
                        if (weight.isEmpty())
                            Toast.makeText(getApplicationContext(), "Weight Is Empty", Toast.LENGTH_SHORT).show();
                        else if (imagePath == null)
                            Toast.makeText(getApplicationContext(), "Image is Empty", Toast.LENGTH_SHORT).show();
                        else {
                            progressBarOfSetProfile.setVisibility(View.VISIBLE);
                            sendDataForNewUser();
                            progressBarOfSetProfile.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(SetProfileActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    private void sendDataForNewUser()
    {
        sendDataToRealTimeDatabase();
    }

    private void sendDataToRealTimeDatabase()
    {
        name= getUserName.getText().toString().trim();
        height= getUserHeight.getText().toString().trim();
        weight= getUserWeight.getText().toString().trim();
        target= getUserTargetWeight.getText().toString().trim();


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        User userProfile=new User(name,firebaseAuth.getUid(),height,weight,target);
        databaseReference.setValue(userProfile);
        Toast.makeText(getApplicationContext(),"User Profile Added Successfully",Toast.LENGTH_SHORT).show();
        sendImageToStorage();
    }

    private void sendImageToStorage()
    {
        StorageReference imageRef=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");
        //Image compression
        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        ///putting image to storage

        UploadTask uploadTask=imageRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                ImageUriAccessToken =uri.toString();
                Toast.makeText(getApplicationContext(),"URI get success",Toast.LENGTH_SHORT).show();
                sendDataToCloudFirestore();
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),"URI get Failed",Toast.LENGTH_SHORT).show());
            Toast.makeText(getApplicationContext(),"Image is uploaded",Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),"Image Not Uploaded",Toast.LENGTH_SHORT).show());

    }

    private void sendDataToCloudFirestore() {
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String , Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("image", ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("height",height);
        userdata.put("weight",weight);
        userdata.put("target_weight",target);
        documentReference.set(userdata).addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(),"Data on Cloud Firestore send success",Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagePath=data.getData();
            getUserImageInImageView.setImageURI(imagePath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}