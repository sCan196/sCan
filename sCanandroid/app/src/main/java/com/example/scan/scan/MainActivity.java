package com.example.scan.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    public final static String Username = "kjndf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.signinbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, homepage.class);
                    EditText editText = (EditText) findViewById(R.id.username);
                    String message = editText.getText().toString();
                    intent.putExtra(Username, message);
                    intent.setClass(MainActivity.this, homepage.class);
                    startActivity(intent);
                }

        });
    }

}