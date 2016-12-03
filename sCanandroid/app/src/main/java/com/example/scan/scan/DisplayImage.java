package com.example.scan.scan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class DisplayImage extends AppCompatActivity {

    /*
    This activity is just for testing.
    It just displays the image that we selected.
    Preprocessing code should make sure that we do stuff to the image.
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        ImageView image = (ImageView) findViewById(R.id.displayImage);
        assert image != null;

        Bundle extras = getIntent().getExtras();

        byte[] byteArray = extras.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image.setImageBitmap(bmp);
    }
}
