package com.example.touchanalytics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import java.io.File;
import java.security.AllPermission;

public class MainActivity extends AppCompatActivity{

    AnalyticDataManager dataManager;
    int[] CSVIds;
    public static int currentNumRegUsers = 0;
    static File registeredUserSaveDir;
    File[] allRegisteredUserFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenSaveCSV.verifyStoragePermissions(this);
        File DCIMDir = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File dataDCIMDir = new File(DCIMDir, "data");
        if (!dataDCIMDir.exists()){
            boolean worked = dataDCIMDir.mkdirs();
            if (!worked){ Log.d("", "somehow didn't work"); }
            Log.d("", "Making DCIM Data path");
        }
        registeredUserSaveDir = dataDCIMDir;
        allRegisteredUserFiles = CheckForRegisteredUsers();
        Log.d("", "allRegisteredUserFiles length: " + allRegisteredUserFiles.length);
        setContentView(R.layout.activity_main);


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
        //dataManager = new AnalyticDataManager(this, CSVIds);
        dataManager = new AnalyticDataManager(this, allRegisteredUserFiles);
        Log.d("", "test 2");


        Button calibrateUSRBtn = findViewById(R.id.calibrateUSRBtn);



        calibrateUSRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent swipeCollect = new Intent(view.getContext(), CollectSwipe.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("manager", dataManager);
                swipeCollect.putExtras(bundle);
                startActivity(swipeCollect);
            }
        });


        int size = 150;
        GridLayout gridLayout = findViewById(R.id.idGrid);

       for(int i=0; i<5; i++) {
           Button btn = new Button(this);
           GridLayout.LayoutParams layoutParams=new GridLayout.LayoutParams();
           layoutParams.setMargins(size*20/100,size*20/100,size*20/100,size*20/100);
           layoutParams.width=size;
           layoutParams.height=size;
           btn.setTag(""+i);
           btn.setBackground(getResources().getDrawable(R.drawable.round_button));
           btn.setLayoutParams(new GridLayout.LayoutParams(calibrateUSRBtn.getLayoutParams()));

           gridLayout.addView(btn);
       }
    }

    public static File[] CheckForRegisteredUsers(){
        File[] filesDCIM = registeredUserSaveDir.listFiles();
        int count = (filesDCIM != null) ? filesDCIM.length : -1;
        if (count == -1){
            Log.d("", "other uh oh");
            return null;
        }else{
            Log.d("", "count: " + count);
            currentNumRegUsers = count;
            return filesDCIM;
        }
    }
}