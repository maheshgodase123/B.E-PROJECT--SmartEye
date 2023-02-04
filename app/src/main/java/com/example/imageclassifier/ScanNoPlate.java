package com.example.imageclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class ScanNoPlate extends AppCompatActivity {

    Button capture, select, detect;
    TextView data;
    ImageView NoPlateimageView;
    Bitmap NoPlatebitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_no_plate);

        capture = findViewById(R.id.button1);
        select = findViewById(R.id.button2);
        detect = findViewById(R.id.verify);
        NoPlateimageView = findViewById(R.id.NoPlateimageView);
        data = findViewById(R.id.NoPlateresult);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent,10);
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(intent,11);
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectText();
            }
        });
    }

    // need to get permission from user to access data from mob

    void getPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  // M means marshmello because after marshmello we can access storage
        {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(ScanNoPlate.this,new String[]{Manifest.permission.CAMERA},12);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 12)   // same verifying request code
        {
            if(grantResults.length > 0) // checking if we have passed request in above string arrray or not line 80
            {
                // we know that we have camera request at 0th index in array
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED)    // checking if permission is given or not
                {
                    // if permission not granted
                    // ask again for permission
                    this.getPermission();       // line73
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 10)
        {
            if(resultCode != RESULT_CANCELED)
            {
                NoPlatebitmap = (Bitmap) data.getExtras().get("data");  // now we have image in bitmap
                // capture by user
            }

        }
        else if(requestCode == 11)
        {
            if(data != null)
            {
                Uri uri =data.getData();
                try {
                    NoPlatebitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        NoPlateimageView.setImageBitmap(NoPlatebitmap);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void detectText()
    {
        InputImage image = InputImage.fromBitmap(NoPlatebitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block: text.getTextBlocks())
                {
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line : block.getLines())
                    {
                        String lineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();
                        for(Text.Element element : line.getElements())
                        {
                            String elementText = element.getText();
                            result.append(elementText);
                        }
                        data.setText(blockText);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScanNoPlate.this,"Fail To Detect"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}