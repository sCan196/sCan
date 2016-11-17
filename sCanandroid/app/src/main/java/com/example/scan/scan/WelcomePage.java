package com.example.scan.scan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WelcomePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String KEY = "entered";
        final String EMAIL_KEY = "UserEmail";
        final String SHARED_PREF_NAME = "generals_prefs";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button btn = (Button) findViewById(R.id.signinbutton);

        final Context context = this;

        /*
        On click of button, app saves user name and when the app is reopened,
        it automatically displays the name in the text edit. The button is linked to the home page.
        Data of user name is saved using SharedPreferences.
        */

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomePage.class);
                EditText editText = (EditText) findViewById(R.id.username);
                EditText emailEdit = (EditText) findViewById(R.id.emailaddress);
                String message = editText.getText().toString();
                if(message.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_LONG).show();
                    return;
                }
                if(emailEdit.getText().toString().indexOf('@') < 0){
                    Toast.makeText(getApplicationContext(), "Please enter a valid email id", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                editor.putString(KEY, message.trim());
                editor.putString(EMAIL_KEY,emailEdit.getText().toString().trim());
                editor.commit();
                intent.putExtra("username", message);
                startActivity(intent);
                }
        });

        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        EditText name = (EditText) findViewById(R.id.username);
        EditText addressName = (EditText) findViewById(R.id.emailaddress);

        final String kludge = "qwuidfghj" + Math.random() + System.currentTimeMillis(); // surely no one will break this
        String entered = preferences.getString(KEY, kludge);
        String addressEntered = preferences.getString(EMAIL_KEY, kludge);
        if (entered.equals(kludge) || addressEntered.equals(kludge) || entered.equals("") || addressEntered.equals("")) {
            if(!entered.equals(kludge) && !entered.equals(""))
                name.setText(entered);
            if(!addressEntered.equals(kludge) && !addressEntered.equals(""))
                addressName.setText(addressEntered);
        }
        else {
            name.setText(entered);
            addressName.setText(addressEntered);
            Intent intent = new Intent(context, HomePage.class);
            intent.putExtra("username", entered);
            startActivity(intent);
        }

    }

}