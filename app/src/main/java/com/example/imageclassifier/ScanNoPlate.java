package com.example.imageclassifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ScanNoPlate extends AppCompatActivity {

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    Button capture, select, detect, confirm;
    EditText data;
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
        confirm = findViewById(R.id.confirmNoPlateImage);

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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String detectedNo = data.getText().toString();

                String formattedNumber = noplateFormat(detectedNo);

                if(formattedNumber != null)
                {
                    Intent intent = new Intent();
                    intent.putExtra("NoPlateResult", formattedNumber);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Toast.makeText(ScanNoPlate.this, "Invalid Vehicle Number !!", Toast.LENGTH_SHORT).show();
                }

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

    private Bitmap detectNoPlate(Bitmap originalbitmap)
    {
        Mat mat_image = new Mat();  // initializing mat object
        Bitmap bmp32 = originalbitmap.copy(Bitmap.Config.ARGB_8888, true);   // creating bitmap format for coverting from which we are creating mat image
        Utils.bitmapToMat(bmp32, mat_image);    // here we have converted bitmap to mat and img saved in mat_image
        CascadeClassifier licensePlateDetector = null;
        try {

            InputStream is = getAssets().open("haarcascade_russian_plate_number.xml");
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File NoPlateCascadeFile = new File(cascadeDir, "indian_license_plate.xml");
            FileOutputStream os = new FileOutputStream(NoPlateCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            licensePlateDetector = new CascadeClassifier(NoPlateCascadeFile.getAbsolutePath());
            Log.d("NoPlate_Classifier","Classifier is Loaded");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(licensePlateDetector != null)   // checking if harcascade loaded or not
        {
            // if loaded detect faces
            Mat grayImage = new Mat();
            Imgproc.cvtColor(mat_image, grayImage, Imgproc.COLOR_BGR2GRAY);

            MatOfRect licensePlateRectangles = new MatOfRect();
            licensePlateDetector.detectMultiScale(grayImage, licensePlateRectangles);

            List<Mat> licensePlateImages = new ArrayList<>();
            for (org.opencv.core.Rect rect : licensePlateRectangles.toArray()) {
                Mat licensePlate = new Mat(grayImage, rect);
                licensePlateImages.add(licensePlate);
            }

            for (Mat licensePlate : licensePlateImages) {
                Mat croppedLicensePlate = new Mat();
                Imgproc.resize(licensePlate, croppedLicensePlate, new Size(240, 80));

                // Save the cropped license plate as a bitmap
                Imgcodecs.imwrite("F:/B.E/NoPlate/outdsd.bmp", croppedLicensePlate);
                Mat mat = croppedLicensePlate; // the Mat object that you want to convert to a Bitmap
                Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    private String noplateFormat(String number)
    {
        number.trim();
        StringBuilder formattedNumber = new StringBuilder();

        for(int i = 0; i < number.length(); i++)
        {
            if(checkCharacter(number.charAt(i)))
            {
                formattedNumber.append(number.charAt(i));
            }
            else if(checkNumber(number.charAt(i)))
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
    private void detectText()
    {
//        Bitmap detectedNoPlate = detectNoPlate(NoPlatebitmap);
//        if(detectedNoPlate == null)
//        {
//            detectedNoPlate = NoPlatebitmap; // so we will overwrite with original image if no noplate is detected
//        }
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