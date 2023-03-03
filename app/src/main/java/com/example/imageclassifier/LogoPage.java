package com.example.imageclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoPage extends AppCompatActivity {

    //Handler allows you to send and process Runnable Objects (Classes in this case)
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_page);

        //postDelayed method, Causes the Runnable r (in this case Class login) to be added to the message queue, to be run
        // after the specified amount of time elapses.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Create a new Intent to go from Class B to Class C and start the new Activity.
                Intent intent = new Intent(LogoPage.this, login.class);
                startActivity(intent);
                finish();
            }
            //Here after the comma you specify the amount of time you want the screen to be delayed. 2000 is for 2 seconds.
        }, 1000);
    }
}