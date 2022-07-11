package com.example.fitness_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.Viewholder> {

    private final Context context;
    private ArrayList<ExercisesPackage> arraylist;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    // Constructor
    public PackageAdapter(Context context, ArrayList<ExercisesPackage> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        //display the data on the card.
        ExercisesPackage exercisesPackage = arraylist.get(position);
        holder.packageNameTV.setText(exercisesPackage.getName());
        holder.exercisesNumberTV.setText(exercisesPackage.getExercises()+" exercises");
        Picasso.get().load(exercisesPackage.getImageURL()).into(holder.packageIV);

        int k = position;

        //if card is pressed go to the specific exercise activity.
        holder.itemView.setOnClickListener(v -> {
            Intent intent=new Intent(context, SpecificExerciseActivity.class);
            intent.putExtra("name",exercisesPackage.getName());
            intent.putExtra("exercises_num",""+exercisesPackage.getExercises());
            intent.putExtra("image_url",exercisesPackage.getImageURL());
            intent.putExtra("vid_num",""+(k+2));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return arraylist.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView packageIV;
        private TextView packageNameTV, exercisesNumberTV;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            packageIV = itemView.findViewById(R.id.video_image_card);
            packageNameTV = itemView.findViewById(R.id.name_card);
            exercisesNumberTV = itemView.findViewById(R.id.videos_number_card);
        }
    }
}
