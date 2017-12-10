package com.example.deletablelistview2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DeletableListView listView = (DeletableListView) findViewById(R.id.list);
        ArrayList<String> lists = new ArrayList<>();
        initList(lists);

        final MyArrayAdapter adapter = new MyArrayAdapter(this, 0, lists);
        listView.setOnDeleteListener(new DeletableListView.onDeleteListener() {
            @Override
            public void deleteItem(int position) {
                adapter.remove(adapter.getItem(position));
            }
        });
        listView.setAdapter(adapter);
    }

    private void initList(List<String> list) {
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");
        list.add("item5");
        list.add("item6");
        list.add("item7");
    }
}
