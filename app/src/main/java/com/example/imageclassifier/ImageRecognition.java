package com.example.imageclassifier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imageclassifier.ml.Model2;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageRecognition extends AppCompatActivity{

    // this below function is written before oncreate bcoz app was calling oncreate function before
    // loading the OpenCv so it was giving error to the Mat lib in OpenCv i.e
    // File Not Found
    // The reason for the error is that Android calls the "onCreate" method before loading the OpenCV4Android library.
    // So i have used Async Initialization of OpenCV using OpenCVManager.
    // I have created BaseLoaderCallback before onCreate method.
    // So it will initialize the loading of OpenCv library first then it will call main func

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

    // after initializing of mLoader, on resume will check the OpenCv is loaded or not
    // if not it will again go to the mLoaderCallback so it will recall it
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

    Button selectBtn, captureBtn, verifyBtn, confirm;
    ImageView imageView;
    EditText result;
    Bitmap bitmap;      // for storing image
    Boolean isFaceDetected = false;
    // below variables use for Loading Classifier Model in detect_Face
    CascadeClassifier cascadeClassifier;    // declaring object of cascadeClassifier
    int height = 0;
    int width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);

        getPermission();    // for accessing camera
        // assigning buttons to our var
        selectBtn = findViewById(R.id.selectbtn);
        captureBtn = findViewById(R.id.capturebtn);
        verifyBtn = findViewById(R.id.verifybtn);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        confirm = findViewById(R.id.confirmImage);

        // Basically in above R is Resources of our app so it contains all things we have added

        // code for selectBtn when user clicks on it
        // setOnClickListener means what will happen if user clicks on it
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // what we want is we want to start an activity which will show you the all images in you file
                //So Intent is generally use to start new activity or navigate user from one activity to other
                // like clicking on links, sending one application to other

                Intent intent = new Intent();   // declaring
                intent.setAction(Intent.ACTION_GET_CONTENT);    // specifying that intent is going
                // to get us content that is images from device
                intent.setType("image/*");  // setting type it as image as we wants images

                startActivityForResult(intent,10);   // starting intent/activity

                // as we know intent is use to navigate in between activities it could be implicit/explicit
                // activity
                // thats why we pass requestCode so the result pass from startActivityForResult
                // to onaAtivityResult  will be verify by that code and we will save the data recived from
                // startActivityForResult
            }

        });

        // code for capture button
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // again we have to start an new activity after clicking on capture button
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  // this time we have to capture
                // image from this activity
                // before it were selecting/showing the images from storage
                // so now start Activity and pass intent and request code to it
                startActivityForResult(intent,12);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFaceDetected)
                {
                    try {
                        //FaceRecModel model = FaceRecModel.newInstance(getApplicationContext());
                        Model2 model = Model2.newInstance(ImageRecognition.this);
                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                        int image_size = 224;

                        bitmap = Bitmap.createScaledBitmap(bitmap,image_size,image_size,false);

                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * image_size * image_size * 3);

                        byteBuffer.order(ByteOrder.nativeOrder());

                        int[] intValues = new int[image_size * image_size];

                        bitmap.getPixels(intValues,0, bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

                        int pixel = 0;

                        //iterate over each pixel and extract R,G,B. Add those value individually to the byte buffer.

                        for(int i = 0; i < image_size; i++)
                        {
                            for(int j = 0; j < image_size; j++)
                            {
                                int val = intValues[pixel++];  //RGB
                                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                                byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                            }
                        }
                        inputFeature0.loadBuffer(byteBuffer);

                        // Runs model inference and gets result.

                        Model2.Outputs outputs = model.process(inputFeature0);

                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                        float[] confidence_value = outputFeature0.getFloatArray();

                        //String [] labels = {"Courteney_Cox", "Jennifer_Aniston", "Lisa_Kudrow"};

                        String id = "";
                        if(confidence_value[0] <= 0.2)
                        {
                            id = "Courteney Cox";
                        }
                        else if(confidence_value[0] >= 1 && confidence_value[0] <= 1.4)
                        {
                            id = "Jennifer Aniston";
                        }
                        else if(confidence_value[0] >= 2 && confidence_value[0] <= 2.2)
                        {
                            id = "Lisa Kudrow";
                        }
                        else
                        {
                            id = "Cant Recognize";
                        }

                        result.setText(id + "");
                        // Releases model resources if no longer used.
                        model.close();

                    } catch (IOException e) {
                        // TODO Handle the exception
                    }
                }
                else
                {
                    result.setText("Face Not Detected!!");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("ImageResult", result.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // as we know we will get results of startActivityForResult in onaAtivityResult so defining it


    void getPermission()
    {
        // checking if permission is already given or not for camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // as we can checkPermission from android from version MarshMello
            // below Marshmello not supported
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)    // if permission not granted
            {
                ActivityCompat.requestPermissions(ImageRecognition.this,new String[]{Manifest.permission.CAMERA},11);
                //    request permission from                main,         pass permissions u want in that array  , pass requestcode
                // just like startActivityForResult have onActivityResult
                // for requestPermission we have onRequestPermissionResult
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 11)   // same verifying request code
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
        // below we are checking requestCode as we said
        // because their could be many activities going on so request code is used to verify that
        // is it that activity from which we want results ex line no = 48 from which we want images
        if(requestCode == 10)
        {
            if(data != null)
            {
                Uri uri = data.getData();
//              URI(Uniform resource identifier) as its name suggests is used to identify resource
//     (whether it be a page of text, a video or sound clip, a still or animated image,or a program).

                // now store the image in bitmap format in our var
                // mediadtore - If scoped storage is enabled, the collection shows only the photos,
                // videos, and audio files that your app has created.
                //getbitmap will return selected image by user from uri
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    // first set that image in imageview
                    imageView.setImageBitmap(bitmap);
                    // after setting image pass that image to detectFace algorithm and now
                    Bitmap detected_face = detectFace(bitmap);
                    // after we get the cropped face from image set bitmap = that cropped face
                    // so that it will be sent to model
                    if(detected_face != null)   // this is becouse if no face is detected then face_rec model will throw error and app stops
                    // if detectFace will return null so bitmap will be not set to detected face so original bitmap image will be send to model
                    {
                        bitmap = detected_face;
                        isFaceDetected = true;
                    }
                    // after getting image in bitmap set it in our imageview

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == 12)  // here we are getting request from another code
        {
            // this time user have captured image and it is sent in data
            // https://www.vogella.com/tutorials/AndroidIntent/article.html#:~:text=getExtras()%20method%20call.,method%20on%20the%20bundle%20object.
            bitmap = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            Bitmap detected_face = detectFace(bitmap);
            // getExtras is use to retrive data sended from intent for that 1st get data in getExtras and then
            // convert that data into Bitmap

            // after setting original image in imageview set bitmap = detected face so that model will process it correctly
            if(detected_face != null)   // this is becouse if no face is detected then face_rec model will throw error and app stops
            {
                bitmap = detected_face;
                isFaceDetected = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // detect face method is use to detect faces from images
    // it takes image in bitmap format and then convert it into mat format for cascade processing
    // in last it again convert into bitmap format so we can directly sent it to model

    public Bitmap detectFace(Bitmap image)
    {
        Mat mat_image = new Mat();  // initializing mat object
        Bitmap bmp32 = image.copy(Bitmap.Config.ARGB_8888, true);   // creating bitmap format for coverting from which we are creating mat image
        Utils.bitmapToMat(bmp32, mat_image);    // here we have converted bitmap to mat and img saved in mat_image

        // covert mat image to grayscale image for further processing
        Mat grayscaleImage = new Mat();

        Imgproc.cvtColor(mat_image,grayscaleImage,Imgproc.COLOR_RGBA2GRAY);
        height = grayscaleImage.height();
        width = grayscaleImage.width();


        // loading CascadeClasifier
        // i have tried directly giving path to loader but it gives error that is haarcascade_frontalface_alt.xml not in read mode
        // so we have copy pasted file in res/raw/ folder we are going to read in byte array so using input stream

        try {
            // reading from directory
            InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            // creating directory for saving data
            File cascadeDir = getApplicationContext().getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir,"haarcascade_frontalface_alt");
            FileOutputStream outputStream = new FileOutputStream(mCascadeFile);
            // readed data is going to stored in buffer link
            byte[] buffer = new byte[4096];
            int byteRead;

            while((byteRead = inputStream.read(buffer)) != -1)  // when byteread = -1 means file is empty we have readed all data
            {
                outputStream.write(buffer,0,byteRead);
            }

            // close after use
            inputStream.close();
            outputStream.close();

            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());  // now loading the redead file from haarcascade_frontalface_alt.xml
            // saved in directory
            Log.d("Classifier_Loaded","Classifier is Loaded");
        }
        catch (Exception e) {   // inputstream can generate filenot found error and elso byteread can generate io exceptino
            e.printStackTrace();
        }

        // now detect faces
        int absoluteFaceSize = (int) (height * 0.1);
        MatOfRect faces = new MatOfRect();

        if(cascadeClassifier != null)   // checking if haarcascade loaded or not
        {
            // if loaded detect faces
            cascadeClassifier.detectMultiScale(grayscaleImage,faces,1.1,2,2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }

        Rect[] facesArray = faces.toArray();
        // detected faces will be stored in faces
        Bitmap bitmapimg = null;        // here we will store bitmap converted image
        for(int i = 0; i < facesArray.length; i++)
        {
            // drawing rectangle on detected faces
            //                 input   face_starting_point   ending point                R  R  B  A       rectangle thikness
            Imgproc.rectangle(mat_image,facesArray[i].tl(),facesArray[i].br(),new Scalar(0,255,0,255),1);
            // tl means image starting point and br means ending point

            //                      starting point              ending point
            Rect roi = new Rect((int)facesArray[i].tl().x,(int)facesArray[i].tl().y,
                    ((int)facesArray[i].br().x) - ((int)facesArray[i].tl().x),      // width - ending point - starting point of x axis so its width
                    ((int)facesArray[i].br().y) - ((int)facesArray[i].tl().y)      // same for height its for y axis so its height
            );
            Mat cropped_rgb = new Mat(mat_image,roi);   // roi is rectangle created around image so it will croped according to roi
            // and save croped image in mat_image

            bitmapimg = Bitmap.createBitmap(cropped_rgb.cols(),cropped_rgb.rows(),Bitmap.Config.ARGB_8888);
            // same convert Mat to Bitmap
            Utils.matToBitmap(cropped_rgb,bitmapimg);
        }

        return bitmapimg;   // return Bitmap
    }

    // now we have to code for capture image so for that we required to give permission to camera
    // so open manifest and give permission


}