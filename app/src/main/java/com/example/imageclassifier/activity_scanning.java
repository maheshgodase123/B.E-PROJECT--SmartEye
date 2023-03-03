package com.example.imageclassifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class activity_scanning extends AppCompatActivity {

    TextView personName,noPlate;
    Spinner status;
    ImageButton scanimage, scanNoPlate;
    Button saveToDataBase;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    ArrayList<String> list = new ArrayList<>(Arrays.asList("21bh2345aa","dl7co19399","mh04jb8199","mh43bu9429","mh43pr2356","kl01cf7777","mh09bz3366"));

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


//        you’ll have to create an ArrayAdapter.
//        The ArrayAdapter will be responsible for rendering every item in the languages string array to the screen when the Java dropdown menu is accessed.

        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this,R.array.STATUS, R.layout.spinner_item);

//        createFromResources() is a built-in method for the ArrayAdapter class which takes three input parameters:
//        the environment of the application—within an Activity, you can just use this
//        the name of the StringArray that you declared in the strings.xml file
//        the layout type - i have created customised layout spinner_item.xml
//        For this particular example, we’re using a basic spinner layout.
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item); this is not required as i have madhe customised spinner_item.xml file
//        The adapter that we declared above is useless unless it is attached to our dropdown menu (spinner). Therefore, set the spinner to use that adapter.
        status.setAdapter(adapter);



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
                String statusVal = status.getSelectedItem().toString();

                if(nameVal.isEmpty() || noplateVal.isEmpty() || statusVal.isEmpty())
                {
                    if(nameVal.isEmpty())
                    {
                        personName.setError("Empty!!");
                    }
                    else if(noplateVal.isEmpty())
                    {
                        noPlate.setError("Empty!!");
                    }
                    Toast.makeText(activity_scanning.this, "Fields Cannot Be Empty !!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    noplateVal = noplateVal.toLowerCase();

                    if(!list.contains(noplateVal))  // check if noplate present in list or not if present then only allow vehicle
                    {
                        Toast.makeText(activity_scanning.this, "Vehicle Not Allowed !!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // getting todays date
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
                        String formattedDate = df.format(c);

                        HashMap<String,String> map = new HashMap<>();
                        map.put("Driver",nameVal);
                        map.put("NoPlate",noplateVal.toUpperCase());
                        map.put("Status",statusVal.toUpperCase());
                        map.put("DateTime",formattedDate);

                        root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(activity_scanning.this, "Data Saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }

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
            personName.setError(null);
            // setError null bcoz if error is set in texview for previous tap on submit button and after user again filled the textview
            // so setError should not display previous error there in texview so when user again taps on scan and result is collected here
            // then we should setError as null and again when clicking on submit button if new result is again empty so if condition in submit
            // button set Error again
            // Thats why we need to reset the error here
        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
            String value = data.getStringExtra("NoPlateResult");
            // Do something with the data
            noPlate.setText(value);
            noPlate.setError(null);

        }
    }

}