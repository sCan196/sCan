package com.example.scan.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class homepage extends AppCompatActivity {
    Button btnscan;
    Button btnPicLib;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        String s = intent.getStringExtra("username");
        s = s.trim();
        if (s.indexOf(" ") >= 0 || s.indexOf("\n") >= 0){
            s = s.substring(0,s.indexOf(" "));
        }
        s = s.substring(0,1).toUpperCase() + s.substring(1,s.length());
        TextView user = (TextView) findViewById(R.id.showuser);
        user.setText("Hello " + s + "!");

        //Scan button invokes Camera

        btnscan = (Button) findViewById(R.id.scanbutton);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }
            }
        });


        //Photo Library button helps select a photo from Picture Library and use it

        btnPicLib = (Button) findViewById(R.id.photolibrarybutton);
        btnPicLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    //returns the image clicked by the camera to displayimage.class activity
    @Override  //NOTICE PREPROCESSING TEAM! The image can be passed on for preprocessing with this method as a mit map.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //IF PHOTO IS TAKEN USING CAMERA
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //PRE PROCESSING TEAM ENTRY
            imageBitmap = PreProcessing.toBlackWhite(imageBitmap); // converts the image from colour to balck white so its easier to detect
                                                                   // corners and text.
            imageBitmap = PreProcessing.correctSpin(imageBitmap,PreProcessing.getCorners(imageBitmap)); // corrects the alignment of the image.
            //END OF PRE PROCESSING


            /*  OCR TEAM
            take the bitmap named "imageBitmap" from here. This is what you want.
            I have created another activity to display your string. We will link the string you return
            to that activity on Saturday. Below is the code to display the processed image on a new activity
            just for testing if pre processing is working. Will remove it on Saturday.
            If preprocessing methods are not working then remove them.
            */


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent intent = new Intent(this, displayimage.class);
            intent.putExtra("picture", byteArray);
            intent.putExtra("source", "cam");
            startActivity(intent);
        }

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){ //IF PICTURE IS TAKEN FROM PHOTO LIBRARY
            Uri imageUri = data.getData();

            /*  OCR TEAM
            First convert the Uri into bitmap and pass it on to the same function which you used above for preprocessing. Process it.
            return the bitmap to the OCR team for further processing into string.
            I have created another activity to display your string. We will link the string you return
            to that activity on Saturday. Below is the code to display the processed image on a new activity
            just for testing if pre processing is working. Will remove it on Saturday.
            If pre processing methods are not working then remove them.
            */

            Intent intent = new Intent(this, displayimage.class);
            intent.putExtra("pictureURI", imageUri.toString());
            intent.putExtra("source", "lib");
            startActivity(intent);
        }
    }
}
