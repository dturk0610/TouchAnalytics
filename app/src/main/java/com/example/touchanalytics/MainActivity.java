package com.example.touchanalytics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity{

    AnalyticDataManager dataManager;
    int[] CSVIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenSaveCSV.verifyStoragePermissions(this);
        //Intent openCSV = new Intent(this, OpenSaveCSV.class);
        //startActivity(openCSV);
        CSVIds = new int[]{
                R.raw.user0,
                R.raw.user1,
                R.raw.user2,
                R.raw.user3,
                R.raw.user4,
                R.raw.user5
        };
        Log.d("", "test 1");
        dataManager = new AnalyticDataManager(this, CSVIds);
        Log.d("", "test 2");

        Intent swipeCollect = new Intent(this, CollectSwipe.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("manager", dataManager);
        swipeCollect.putExtras(bundle);
        startActivity(swipeCollect);
    }
}