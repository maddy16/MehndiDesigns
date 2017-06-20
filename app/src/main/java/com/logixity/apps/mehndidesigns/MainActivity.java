package com.logixity.apps.mehndidesigns;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final Integer image_ids[] = {
            R.drawable.mehndi_1,
            R.drawable.mehndi_2,
            R.drawable.mehndi_3,
            R.drawable.mehndi_4,
            R.drawable.mehndi_5,
            R.drawable.mehndi_6,
            R.drawable.mehndi_7,
            R.drawable.mehndi_8

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Integer> createLists = prepareData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }
    public ArrayList<Integer> prepareData(){
        ArrayList<Integer> list = new ArrayList<>();
        for(int id : image_ids){
            list.add(id);
        }
        return list;
    }
}
