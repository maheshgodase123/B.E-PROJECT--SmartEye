package com.example.imageclassifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class activity_scanning extends AppCompatActivity {

    TextView personName,noPlate;
    EditText status;
    ImageButton scanimage, scanNoPlate;
    Button saveToDataBase;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        personName = findViewById(R.id.textView4);
        noPlate = findViewById(R.id.textView5);
        status = findViewById(R.id.VeihicleStatus);
        scanimage = findViewById(R.id.imageButton8);
        scanNoPlate = findViewById(R.id.imageButton);
        saveToDataBase = findViewById(R.id.saveToDatabase);


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

        saveToDataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameVal = personName.getText().toString();
                String noplateVal = noPlate.getText().toString();
                String statusVal = status.getText().toString();
                // getting todays date

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
                String formattedDate = df.format(c);

                HashMap<String,String> map = new HashMap<>();
                map.put("Driver",nameVal);
                map.put("NoPlate",noplateVal);
                map.put("Status",statusVal);
                map.put("DateTime",formattedDate);

                root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity_scanning.this, "Data Saved", Toast.LENGTH_SHORT).show();
                    }
                });
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