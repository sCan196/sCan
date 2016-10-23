package com.example.scan.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class homepage extends AppCompatActivity {
    Button btnscan;
    Button btnPicLib;
    private static final int
        REQUEST_IMAGE_CAPTURE = 1, PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        String s = intent.getStringExtra("username");
        s = s.trim();

        /* this shouldn't be needed now
        if (s.indexOf("\n" >= 0)
            s = s.substring(0, s.indexOf("\n");
         */
        if (s.indexOf(" ") >= 0)
            s = s.substring(0,s.indexOf(" ")); // trim to first name


        s = s.substring(0,1).toUpperCase() + s.substring(1,s.length());
        TextView user = (TextView) findViewById(R.id.showuser);
        user.setText("Hello " + s + "!");

        // Scan button invokes Camera
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

        // Photo Library button helps select a photo from Picture Library and use it
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
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // returns the image clicked by the camera to displayimage.class activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { //IF PHOTO IS TAKEN USING CAMERA
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //PRE PROCESSING TEAM ENTRY
            imageBitmap = PreProcessing.cloneToMute(imageBitmap);
            imageBitmap = PreProcessing.toBlackWhite(imageBitmap);
            //imageBitmap = PreProcessing.correctSpin(imageBitmap,PreProcessing.getCorners(imageBitmap));
            // HELLO CRASHY CRASH FACE
            //END OF PRE PROCESSING


            /*
            OCR TEAM
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
            First convert the URI into bitmap and pass it on to the same function which you used above for preprocessing.
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
