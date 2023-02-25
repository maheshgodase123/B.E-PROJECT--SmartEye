package com.example.imageclassifier;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ImageButton records;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.button3);
        records = findViewById(R.id.imageButton2);
        mAuth = FirebaseAuth.getInstance();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,login.class);
                startActivity(intent);
            }
        });

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,showDatabase.class));
            }
        });
    }

    public void openScanningActivity(View v)
    {
        Intent intent = new Intent(this,activity_scanning.class);
        startActivity(intent);
    }
    public void openAboutUsActivity(View v)
    {
        Intent intent = new Intent(this,AboutUs.class);
        startActivity(intent);
    }
}