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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final String KEY_NAME = "userName";
        final String KEY_EMAIL = "userEmail";

        final String SHARED_PREF_NAME = "generals_prefs"; // why is this the name

        final EditText etUserName = (EditText) findViewById(R.id.username);
        final EditText etUserEmail = (EditText) findViewById(R.id.useremail);
        assert etUserName != null;
        assert etUserEmail != null;
        System.out.println(etUserName);
        // suppress Android Studio warnings, since both of these should always be true!

        Button btn = (Button) findViewById(R.id.signinbutton);
        assert btn != null;

        final Context context = this;

        /*
        On click of button, app saves user name and when the app is reopened,
        it automatically displays the name in the text edit. The button is linked to the home page.
        Data of user name is saved using SharedPreferences.
        */

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString().trim();
                String userEmail = etUserEmail.getText().toString().trim();

                if (userName.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.enter_name, Toast.LENGTH_LONG).show();
                    return;
                }
                if (userEmail.indexOf('@') <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
                editor.putString(KEY_NAME, userName);
                editor.putString(KEY_EMAIL, userEmail);
                editor.apply();

                finishWelcomeScreen(userName);
            }
        });


        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        final String kludge = "kludge" + ((int) (Math.random() * Integer.MAX_VALUE)) + System.currentTimeMillis();
        // surely no one would try this

        String prevUserName = preferences.getString(KEY_NAME, kludge);
        String prevUserEmail = preferences.getString(KEY_EMAIL, kludge);

        if (!prevUserName.equals(kludge)) etUserName.setText(prevUserName);
        if (!prevUserEmail.equals(kludge)) etUserEmail.setText(prevUserEmail);

        if (!prevUserName.equals("") && !(prevUserEmail.indexOf('@') <= 0)) {
            finishWelcomeScreen(prevUserName);
        }

    }

    /**
     * literally in the name of not duplicating code!
     */
    private void finishWelcomeScreen(String userName) {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("username", userName);
        startActivity(intent);
    }

}