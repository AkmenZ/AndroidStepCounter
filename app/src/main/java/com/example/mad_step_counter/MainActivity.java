package com.example.mad_step_counter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // experimental values for hi and lo magnitude limits
    private final double HI_STEP = 11.0;     // upper mag limit
    private final double LO_STEP = 8.0;      // lower mag limit
    boolean highLimit = false;      // detect high limit
    int counter = 0;                // step counter

    GifImageView run;
    ImageView idle;

    TextView tvSteps, tvTime;
    Button start;
    Button finnish;

    boolean started;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSteps = findViewById(R.id.tvSteps);
        tvTime = findViewById(R.id.tvTimer);

        timer = new Timer();

        run = findViewById(R.id.imgRun);
        idle = findViewById(R.id.imgIdle);
        run.setVisibility(View.INVISIBLE);//initially invisible
        idle.setVisibility(View.VISIBLE);//initially visible

        started = false;
        start = findViewById(R.id.startBtn);
        finnish = findViewById(R.id.finnishBtn);

        //start button clicked
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (started == false)
                {
                    run.setVisibility(View.VISIBLE);
                    idle.setVisibility(View.INVISIBLE);
                    start.setText("PAUSE");
                    started = true;
                    startTimer();//start timer
                }
                else
                {
                    run.setVisibility(View.INVISIBLE);
                    idle.setVisibility(View.VISIBLE);
                    start.setText("START");
                    started = false;
                    timerTask.cancel();
                }
            }
        });

        //finnish button clicked
        finnish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(started == false)
                {
                    //reset timer
                    if(timerTask != null)
                    {
                        //open page 2
                        openPage2();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Do Some Running First!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Press Pause Before Finishing!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // get a magnitude number using Pythagoras Theorem
        double mag = round(Math.sqrt((x*x) + (y*y) + (z*z)), 2);

        // for me! if msg > 11 and then drops below 9, we have a step
        // you need to do your own mag calculating
        if ((mag > HI_STEP) && (highLimit == false)) {
            highLimit = true;
        }
        if ((mag < LO_STEP) && (highLimit == true) && (started == true)) {
            // we have a step
            counter++;
            tvSteps.setText(String.valueOf(counter));
            highLimit = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    //method to start timer
    private void startTimer()
    {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        tvTime.setText(getTimerValue());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0, 1000);
    }

    //method to get timer value
    private String getTimerValue()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    //method to format time value
    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

    //open 2nd page method
    public void openPage2()
    {
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("stepsDone", Integer.valueOf(tvSteps.getText().toString()));
        intent.putExtra("timeDone", tvTime.getText().toString());
        startActivity(intent);
    }
}

