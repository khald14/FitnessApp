package com.example.fitness_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Exercise> arraylist;


    // Constructor
    public ExerciseAdapter(Context context, ArrayList<Exercise> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout2, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = arraylist.get(position);
        holder.exerciseNameTV.setText(exercise.getName());
        holder.exerciseTimeTV.setText(exercise.getTime());
        Glide.with(context)
                .load(exercise.getGifURL())
                .into(holder.exerciseIV);

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return arraylist.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView exerciseIV;
        private final TextView exerciseNameTV;
        private final TextView exerciseTimeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseIV = itemView.findViewById(R.id.video_image_card);
            exerciseNameTV = itemView.findViewById(R.id.name_card);
            exerciseTimeTV = itemView.findViewById(R.id.videos_number_card);
        }
    }
}
