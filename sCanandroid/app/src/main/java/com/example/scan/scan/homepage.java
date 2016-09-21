package com.example.scan.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        String s = intent.getStringExtra(MainActivity.Username);
        TextView user = (TextView) findViewById(R.id.showuser);
        user.setText("Hello " + s + "!");
    }

}
