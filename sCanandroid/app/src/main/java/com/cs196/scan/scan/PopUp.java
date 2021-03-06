package com.cs196.scan.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The pop-up interface that appears on long-press.
 * Created by ayushranjan on 15/11/16.
 */

public class PopUp extends Activity {
    final Context context = this;
    public String myData = "";
    Date dateobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_layout);

        SharedPreferences preferences = context.getSharedPreferences(Prefs.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final String addressEntered = preferences.getString(Prefs.KEY_EMAIL, "default");

        // possibly duplicate code
        String shortName = preferences.getString(Prefs.KEY_NAME, "default");
        if (shortName.contains(" "))
            shortName = shortName.substring(0, shortName.indexOf(" ")); // trim to first name
        shortName = shortName.substring(0, 1).toUpperCase() + shortName.substring(1, shortName.length());
        final String finalShortName = shortName;

        final DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Intent intent = this.getIntent();

        final String name = intent.getExtras().getString("FileName");

        String filepath = "MyFileStorage";
        final File attachmentExternal = new File(getExternalFilesDir(filepath), name);
        final File attachment = getApplicationContext().getFileStreamPath(name);
        try {
            FileInputStream fis = new FileInputStream(attachment);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(attachmentExternal);
            fos.write(myData.getBytes());
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels*0.4),(int) (dm.heightPixels*0.4));

        Button bMailSelf = (Button) findViewById(R.id.selfMail);
        Button bMailOther = (Button) findViewById(R.id.otherMail);
        Button bView = (Button) findViewById(R.id.viewContents);

        bView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputStream in = openFileInput(name);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    // pipe in text
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str);
                        buf.append("\n\n"); // 2 line breaks between each block. you can fix that later.
                    }

                    in.close();

                    // displayText
                    Intent intent = new Intent(PopUp.this, DisplayText.class);
                    intent.putExtra("text", buf.toString());
                    startActivity(intent);

                    //Toast.makeText(ListFilesActivity.this, buf.toString(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Couldn't find that file.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.except_other, Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            }
        });
        bMailSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateobj = new Date();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/Message");
                email.putExtra(Intent.EXTRA_SUBJECT, "Text File from sCan");
                email.putExtra(Intent.EXTRA_TEXT, "Hello!\n\n" + "Attached below is the text file named " + name + " sent from the sCan app in your android phone on " + df.format(dateobj) + ".\n\nRegards,\nsCan Team");
                email.putExtra(Intent.EXTRA_EMAIL,new String[]{addressEntered});
                if (!attachmentExternal.exists() || !attachmentExternal.canRead()) {
                    Toast.makeText(getApplicationContext(), "Attachment Error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Uri uri = Uri.fromFile(attachmentExternal);
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                }
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        bMailOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateobj = new Date();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/Message");
                email.putExtra(Intent.EXTRA_SUBJECT, "Text File from sCan");
                email.putExtra(Intent.EXTRA_TEXT, "Hello!\n\n" + "Attached below is the text file named " + name +" sent from the sCan app in "+ finalShortName + "'s android phone on " + df.format(dateobj) + ".\n\nRegards,\nsCan Team");

                if (!attachmentExternal.exists() || !attachmentExternal.canRead()) {
                    Toast.makeText(getApplicationContext(), "Attachment Error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Uri uri = Uri.fromFile(attachmentExternal);
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                }
                startActivity(Intent.createChooser(email, "If no email client appear, kindly log into Android Email App.\nChoose an Email client :"));
            }
        });

    }
}
