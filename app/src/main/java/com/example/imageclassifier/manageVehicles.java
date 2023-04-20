package com.example.imageclassifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class manageVehicles extends AppCompatActivity {

    private EditText vehicleNo;
    private Button AddVehicle, DeleteVehicle, showVehicle;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference vehicle = db.getReference().child("Allowed_Vehicles");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vehicles);

        vehicleNo = findViewById(R.id.AddNo);
        AddVehicle = findViewById(R.id.AddNoBtn);
        DeleteVehicle = findViewById(R.id.DeleteBtn);
        showVehicle = findViewById(R.id.noplateDatabase);

        AddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = vehicleNo.getText().toString().toLowerCase();
                value = noplateFormat(value);

                if(value != null)
                {
                    Query query = vehicle.orderByValue().equalTo(value);

                    String finalValue = value;
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
//                            String key = snapshot.getKey();
//                            Object value = snapshot.getValue();
//                            System.out.println(key + ": " + value);
                                Toast.makeText(manageVehicles.this, "Data Already Exist!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                vehicle.push().setValue(finalValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(manageVehicles.this, "Vehicle Added To The Database!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(manageVehicles.this, "Invalid Vehicle Number !!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        DeleteVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = vehicleNo.getText().toString().toLowerCase();

                Query query = vehicle.orderByValue().equalTo(value);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
//                            String key = snapshot.getKey();
//                            Object value = snapshot.getValue();
//                            System.out.println(key + ": " + value);
                            // for deletion we need get childrens of key as above snapshot returns keys
                            for (DataSnapshot childSnapshot: snapshot.getChildren()) {
//                                String key = childSnapshot.getKey();
//                                Object value = childSnapshot.getValue();
//                                System.out.println(key + ": " + value);
                                // remove the child with the matching value
                                childSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Vehicle number removed successfully
                                            Toast.makeText(getApplicationContext(), "Vehicle number removed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Failed to remove vehicle number
                                            Toast.makeText(getApplicationContext(), "Failed to remove vehicle number", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            Toast.makeText(manageVehicles.this, "Vehicle Does Not Exists!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        showVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(manageVehicles.this,showVehicles.class));
            }
        });
    }

    private String noplateFormat(String number)
    {
        number.trim();
        StringBuilder formattedNumber = new StringBuilder();

        for(int i = 0; i < number.length(); i++)
        {
            if((number.charAt(i) >= 'a' && number.charAt(i) <= 'z') || (number.charAt(i) >= 'A' && number.charAt(i) <= 'Z'))
            {
                formattedNumber.append(number.charAt(i));
            }
            else if((number.charAt(i) >= '0' && number.charAt(i) <= '9'))
            {
                formattedNumber.append(number.charAt(i));
            }
        }

        if(formattedNumber.toString().length() != 10)
        {
            return null;
        }

        return checkValid(formattedNumber.toString());
    }

    private String checkValid(String formattedNumber){

        if(checkCharacter(formattedNumber.charAt(0)) && checkCharacter(formattedNumber.charAt(1))){
            if(checkNumber(formattedNumber.charAt(2)) && checkNumber(formattedNumber.charAt(3))){
                if(checkCharacter(formattedNumber.charAt(4)) && checkCharacter(formattedNumber.charAt(5))){
                    if(checkNumber(formattedNumber.charAt(6)) && checkNumber(formattedNumber.charAt(7)) && checkNumber(formattedNumber.charAt(8)) && checkNumber(formattedNumber.charAt(9))){
                        return formattedNumber;
                    }
                    else{
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    private boolean checkCharacter(char inputChar){
        if((inputChar >= 'a' && inputChar <= 'z') || (inputChar >= 'A' && inputChar <= 'Z'))
        {
            return true;
        }

        return false;
    }

    private boolean checkNumber(char inputChar){

        if(inputChar >= '0' && inputChar <= '9')
        {
            return true;
        }

        return false;
    }
}