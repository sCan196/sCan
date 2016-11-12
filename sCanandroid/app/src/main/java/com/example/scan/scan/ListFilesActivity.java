package com.example.scan.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class ListFilesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        final ListView lv = (android.widget.ListView) findViewById(R.id.listFiles);
        final ArrayAdapter<String> arrayAdapter;
        assert lv != null;

        // get file list
        String[] list = fileList();
        ArrayList<String> resultList = new ArrayList<>();
        if (list != null) {
            for (String s : list) {
                if (s.endsWith(".txt")) // filter out possible future non-txt files, instant run, etcetera
                    resultList.add(s);
            }
        }
        Collections.sort(resultList);

        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                resultList);
        lv.setAdapter(arrayAdapter);

        // chaining stuff is fun.
        final AppCompatActivity thisActivity = this;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {
                    String name = (String) (parent.getItemAtPosition(position));
                    InputStream in = openFileInput(name);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    // pipe in text
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str);
                        buf.append("\n");
                        buf.append("\n"); // 2 line breaks between each block. you can fix that later.
                    }

                    in.close();

                    // displayText
                    Intent intent = new Intent(thisActivity, DisplayText.class);
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

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                try {
                    final String name = (String) (parent.getItemAtPosition(position));
                    boolean deleted = deleteFile(name);

                    if (deleted) {
                        Toast.makeText(getApplicationContext(), "Deleted " + name, Toast.LENGTH_SHORT).show();
                        arrayAdapter.remove(name);
                        arrayAdapter.notifyDataSetChanged();
                    } else
                        Toast.makeText(getApplicationContext(), "Couldn't delete " + name, Toast.LENGTH_LONG).show();

                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.except_other, Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }

                return true;
            }
        });
    }
}
