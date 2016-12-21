package com.cs196.scan.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

public class ListFilesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        final ListView lv = (android.widget.ListView) findViewById(R.id.listFiles);
        final ArrayAdapter<String> arrayAdapter;
        assert lv != null;

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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) (parent.getItemAtPosition(position));
                Intent intentPop = new Intent(ListFilesActivity.this, PopUp.class);
                intentPop.putExtra("FileName", name);
                startActivity(intentPop);
            }
        });

        // we should change this to a pop-up menu
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
