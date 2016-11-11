package com.example.scan.scan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.util.SparseArray;
import android.widget.ImageView;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Builder;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class homepage extends AppCompatActivity {
    private static final int
            PICK_IMAGE = 100, CAPTURE_IMAGE = 1777;

    public String textFileName = "";

    Button btnscan;
    Button btnPicLib;
    Button btnDocList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        String s = intent.getStringExtra("username");
        s = s.trim();

        if (s.contains(" "))
            s = s.substring(0, s.indexOf(" ")); // trim to first name

        s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
        TextView user = (TextView) findViewById(R.id.showuser);
        user.setText("Hello, " + s + "!");

        // Scan button invokes Camera
        btnscan = (Button) findViewById(R.id.scanbutton);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });

        btnDocList = (Button) findViewById(R.id.doclibrarybutton);
        btnDocList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startListView = new Intent(homepage.this, ListFilesActivity.class);
                startActivity(startListView);
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

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            if (BuildConfig.DEBUG)
                Toast.makeText(getApplicationContext(), "Canceled.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap imageBitmap = null;

        // IF PHOTO IS TAKEN USING CAMERA
        if (requestCode == CAPTURE_IMAGE) {
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            imageBitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
        }
        // IF PICTURE IS TAKEN FROM PHOTO LIBRARY
        if (requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();

            // primary method
            try {
                imageBitmap = decodeUri(imageUri);
            } catch (FileNotFoundException e) { // this should never happen
                e.printStackTrace();
                return;
            }

            //BACKUP METHOD
            /*try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        if (BuildConfig.DEBUG && imageBitmap == null)
            throw new RuntimeException("how is this null?");

        // PREPROCESSING (has been removed temporarily due to dysfunctionality.
        // imageBitmap = PreProcessing.doStuff(imageBitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type text file name:");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        final Bitmap finalImageBitmap = imageBitmap;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // OCR stuff
                // move this outside if you uncomment the textview
                String answer = "";
                Context context = getApplicationContext();
                TextRecognizer ocrFrame = new TextRecognizer.Builder(context).build();
                Frame frame = new Frame.Builder().setBitmap(finalImageBitmap).build();
                SparseArray<TextBlock> textBlocks = ocrFrame.detect(frame);
                for (int i = 0; i < textBlocks.size(); i++) // probably should use stringbuilder
                    answer += textBlocks.get(textBlocks.keyAt(i)).getValue();

                // save plz
                textFileName = input.getText().toString();
                if (textFileName.equals("")) {
                    // random values
                    textFileName = "sCan-" + (int) (Math.random() * 100000);
                }

                try {
                    OutputStreamWriter out = new OutputStreamWriter(openFileOutput(textFileName + ".txt", 0));
                    out.write(answer);
                    out.close();
                    Toast.makeText(getApplicationContext(), "Saved text to " + textFileName + ".txt", Toast.LENGTH_SHORT).show();
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
                    if (BuildConfig.DEBUG)
                        throw new RuntimeException(t);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        // OCR
        // uncomment this code to just show the text without saving it
        /*if (false) {Intent intent = new Intent(this, DisplayText.class);
        intent.putExtra("text", answer);
        startActivity(intent);}*/

        // uncomment this code to just show what was captured
        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Intent intent = new Intent(this, DisplayImage.class);
        intent.putExtra("picture", byteArray);
        startActivity(intent);*/

    }

    //Method to Decode URI to Bitmap
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    // Helper Function
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
}