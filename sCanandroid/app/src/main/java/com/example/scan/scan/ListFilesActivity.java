package com.example.scan.scan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ListFilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        final ListView lv = (android.widget.ListView)findViewById(R.id.listFIles);
        ArrayAdapter<String> arrayAdapter = null;
        if (fileList().length > 1) {
            arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    Arrays.copyOfRange(fileList(), 1, fileList().length));

        }
        lv.setAdapter(arrayAdapter);
        //TextView emptyText = (TextView)findViewById(R.id.empty);
        //lv.setEmptyView(emptyText);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {

                    InputStream in = openFileInput((String)(parent.getItemAtPosition(position)));

                    if (in != null) {

                        InputStreamReader tmp=new InputStreamReader(in);

                        BufferedReader reader=new BufferedReader(tmp);

                        String str;

                        StringBuilder buf=new StringBuilder();

                        while ((str = reader.readLine()) != null) {

                            buf.append(str+"\n");

                        }
                        Toast.makeText(ListFilesActivity.this, buf.toString(), Toast.LENGTH_LONG).show();
                        in.close();



                    }
                }

                catch (java.io.FileNotFoundException e) {

// that's OK, we probably haven't created it yet

                }

                catch (Throwable t) {

                    Toast.makeText(ListFilesActivity.this, "Exception: "+t.toString(), Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}
