package com.example.fitness_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Locale;

public class ExerciseStartedActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private ImageView imageView;
    private TextView desc, timer;
    private Button startBtn,nextButton,prevBtn;
    private ProgressBar progressBar;
    private int index, time,counter;
    private boolean repeat;
    private CountDownTimer cdt;
    private TextToSpeech textToSpeech;
    private String name;



    private ArrayList<Exercise> exercises;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_started);


          imageView = findViewById(R.id.imageView);
          desc = findViewById(R.id.desc);
          startBtn = findViewById(R.id.start_btn);
          nextButton = findViewById(R.id.next_btn);
          prevBtn = findViewById(R.id.prev_btn);
          timer = findViewById(R.id.timer);
          progressBar = findViewById(R.id.progressBar);
          textToSpeech = new TextToSpeech(this,this);

          index = 0;

        exercises = (ArrayList<Exercise>) getIntent().getSerializableExtra("list");

        //display gif.
        Glide.with(this)
                .load(exercises.get(index).getGifURL())
                .into(imageView);
        desc.setText(exercises.get(index).getDescription());

        //setup the timer.
        repeat = exercises.get(index).getTime().length() > 10;
        time = Integer.parseInt(exercises.get(index).getTime().substring(0,2))*1000;
        progressBar.setMax(Integer.parseInt(exercises.get(index).getTime().substring(0,2)));
        name =  exercises.get(index).getName();

        //if start pressed, start the timer.
        startBtn.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                textToSpeak();
                startBtn.setText("Restart");
                counter = 0;
                progressBar.setProgress(counter);
                cdt = new CountDownTimer(time, 1000){
                    public void onTick(long millisUntilFinished){
                        timer.setText(String.valueOf(counter));
                        counter++;
                        progressBar.setProgress(counter);
                    }
                    public  void onFinish(){
                        if(repeat){
                            repeat = false;
                            counter = 0 ;
                            progressBar.setProgress(counter);
                            this.start();
                        }else {
                            timer.setText("FINISH!!");
                            textToSpeech.speak("Finished", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                }.start();

                startBtn.setOnClickListener(view -> {
                    cdt.cancel();
                    counter = 0;
                    progressBar.setProgress(counter);
                    cdt.start();
                    textToSpeak();
                });
            }
        });

        //move to next exercise and handle the timer.
        nextButton.setOnClickListener(view -> {
            index++;
            if(index>=exercises.size())
                index = 0;
            Glide.with(ExerciseStartedActivity.this)
                    .load(exercises.get(index).getGifURL())
                    .into(imageView);
            desc.setText(exercises.get(index).getDescription());
            repeat = exercises.get(index).getTime().length() > 10;
            time = Integer.parseInt(exercises.get(index).getTime().substring(0,2))*1000;
            int k = Integer.parseInt(exercises.get(index).getTime().substring(0,2));
            progressBar.setMax(k);
            name =  exercises.get(index).getName();
            if(cdt!=null) {
                cdt.cancel();
            }
            startBtn.setText("Start");
            counter = 0;
            timer.setText(""+counter);
            progressBar.setProgress(counter);

        });

        //move to previous exercise and handle the timer.
        prevBtn.setOnClickListener(view -> {
            index--;
            if(index<0)
                index = exercises.size()-1;
            Glide.with(ExerciseStartedActivity.this)
                    .load(exercises.get(index).getGifURL())
                    .into(imageView);
            desc.setText(exercises.get(index).getDescription());
            repeat = exercises.get(index).getTime().length() > 10;
            time = Integer.parseInt(exercises.get(index).getTime().substring(0,2))*1000;
            progressBar.setMax(Integer.parseInt(exercises.get(index).getTime().substring(0,2)));
            name =  exercises.get(index).getName();
            if(cdt!=null) {
                cdt.cancel();
            }
            startBtn.setText("Start");
            counter = 0;
            timer.setText(""+counter);
            progressBar.setProgress(counter);

        });
    }

    //function to speak the name of the exercise
    private void textToSpeak() {
        String text = name;
        if ("".equals(text)) {
            text = "Please enter some text to speak.";
        }
        textToSpeech.speak("Start "+text, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        } else {
            Log.e("error", "Failed to Initialize");
        }
    }
}