package com.example.imageclassifier;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openActivity(View v)
    {
        Toast.makeText(this, "Opening Image Activity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,ImageRecognition.class);
        startActivity(intent);
    }
    public void openActivityNoPlate(View v)
    {
        Toast.makeText(this, "Opening Image Activity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,ScanNoPlate.class);
        startActivity(intent);
    }
}