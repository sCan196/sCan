package com.example.scan.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class displayimage extends AppCompatActivity {

    /*
    This activity is TEMPORARY. Just used for testing. It just displays the image clicked by camera
    or taken from Photo Library
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayimage);
        ImageView image = (ImageView) findViewById(R.id.displayImage);
        Intent intent = this.getIntent();
        String source = intent.getExtras().getString("source");
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image.setImageBitmap(bmp);

    }
}