package com.example.mad_step_counter;

import static com.example.mad_step_counter.MainActivity.round;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    Button page1;
    TextView stepsDone, totalTime, metersDone, caloriesLost, exerciseDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        stepsDone = findViewById(R.id.tvStepsDone);
        totalTime = findViewById(R.id.tvExerciseTime);
        metersDone = findViewById(R.id.tvMetersRun);
        caloriesLost = findViewById(R.id.tvCaloriesLost);
        exerciseDate = findViewById(R.id.tvDate);

        //show date
        String currentDate = java.text.DateFormat.getDateInstance().format(new Date());
        exerciseDate.setText("Exercise on " + currentDate);

        //show steps
        Integer stepsRecorded = getIntent().getIntExtra("stepsDone", -1);
        stepsDone.setText(String.valueOf(stepsRecorded));

        //show time
        String timeRecorded = getIntent().getStringExtra("timeDone");
        totalTime.setText(String.valueOf(timeRecorded));

        //calculate seconds
        String[] units = timeRecorded.split(" : "); //will break the string up into an array
        int hours = Integer.parseInt(units[0]); //first element
        int minutes = Integer.parseInt(units[1]); //second element
        int seconds = Integer.parseInt(units[2]); //third element
        int duration = 360 * hours + 60 * minutes + seconds; //adding up our values

        //calculate meters run
        double distance = stepsRecorded * 0.8;
        metersDone.setText(Double.toString(round(distance,2)) + " Meters Ran!");

        //calculate calories lost
        double cal = stepsRecorded * 0.04;
        caloriesLost.setText(Double.toString(cal) + " Calories Lost!");

        page1 = findViewById(R.id.returnBtn);
        page1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage1();
            }
        });
    }

    public void openPage1()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}