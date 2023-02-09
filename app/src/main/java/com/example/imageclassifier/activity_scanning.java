package com.example.imageclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class activity_scanning extends AppCompatActivity {

    TextView personName,noPlate;
    ImageButton scanimage, scanNoPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        personName = findViewById(R.id.textView4);
        noPlate = findViewById(R.id.textView5);
        scanimage = findViewById(R.id.imageButton8);
        scanNoPlate = findViewById(R.id.imageButton);


        scanimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_scanning.this, ImageRecognition.class);
                startActivityForResult(intent, 1);
            }
        });

        scanNoPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_scanning.this, ScanNoPlate.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String value = data.getStringExtra("ImageResult");
            // Do something with the data
            personName.setText(value);
        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
            String value = data.getStringExtra("NoPlateResult");
            // Do something with the data
            noPlate.setText(value);
        }
    }

}