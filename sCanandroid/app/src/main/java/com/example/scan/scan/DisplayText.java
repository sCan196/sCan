package com.example.scan.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class DisplayText extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_text);
        TextView printText = (TextView) findViewById(R.id.textDisplay);
        Intent intent = this.getIntent();
        String answer = intent.getExtras().getString("text");
        printText.setText(answer);
        printText.setMovementMethod(new ScrollingMovementMethod());
    }
}
