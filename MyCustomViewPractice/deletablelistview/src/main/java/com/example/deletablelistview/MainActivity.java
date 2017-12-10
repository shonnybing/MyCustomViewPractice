package com.example.deletablelistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usePopupWindow();
    }

    private void usePopupWindow() {
        DeletableListView listView = (DeletableListView) findViewById(R.id.listView);
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList("java", "android", "html", "css", "python"));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnDeleteClickListener(new DeletableListView.OnDeleteClickListener() {
            @Override
            public void clickDelete(int position) {
                adapter.remove(adapter.getItem(position));
            }
        });
    }
}
