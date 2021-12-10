package com.example.touchanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestSwipe  extends AppCompatActivity {

    AnalyticDataManager dataManager;
    ConcurrentLinkedQueue<AnalyticDataEntry> swipe;
    ConcurrentLinkedQueue<AnalyticDataEntry> fullCollect;
    int numOfSwipes = 0;
    public int requiredSwipeLimit = 13;
    float percentCanFail = .5f;
    int numFail = 0;


    ImageView imageView;
    TextView txtView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.dataManager = bundle.getParcelable("dataManager");
        dataManager.rerunDCIMThread();
        Log.d("", "parecelable usrIDs Length: " + dataManager.usersCSVFiles.length);
        setContentView(R.layout.calibration);
        swipe = new ConcurrentLinkedQueue<>();
        fullCollect = new ConcurrentLinkedQueue<>();

        imageView = findViewById(R.id.calibrationImgView);
        imageView.setImageDrawable(ImageSelect.RandomImage(this));


        txtView = findViewById(R.id.swipe_amount);
        txtView.setText(String.format("%d/%d", 0, requiredSwipeLimit));


        Button backBtn = findViewById(R.id.cancelCaliBtn);
        backBtn.setText("End test");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullCollect.clear();
                swipe.clear();
                Intent backToMain = new Intent(view.getContext(), MainActivity.class);
                startActivity(backToMain);
            }
        });
    }

    public boolean dispatchTouchEvent( MotionEvent event ) {
        // Log.w( MA, "Inside onTouchEvent" );
        View v = getCurrentFocus();
        //long userID = dataManager.userID;
        String userID = "test";
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Log.d("", "Event down");
                swipe = new ConcurrentLinkedQueue<>();
                AnalyticDataEntry downData = new AnalyticDataEntry(userID, this, event);
                swipe.add(downData);
                fullCollect.add(downData);
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d("", "Event move");
                AnalyticDataEntry moveData = new AnalyticDataEntry(userID, this, event);
                swipe.add(moveData);
                fullCollect.add(moveData);
                break;
            case MotionEvent.ACTION_UP:
                //Log.d("", "Event up");
                AnalyticDataEntry upData = new AnalyticDataEntry(userID, this, event);
                if (swipe.size() + 1 >= 6) {
                    swipe.add(upData);
                    fullCollect.add(upData);
                    numOfSwipes += 1;
                    txtView.setText(String.format("%d/%d", numOfSwipes, requiredSwipeLimit));
                    AnalyticDataEntry[] swipeArr = new AnalyticDataEntry[swipe.size()];
                    swipe.toArray(swipeArr);
                    AnalyticDataFeatureSet feature = new AnalyticDataFeatureSet(swipeArr);
                    Log.d("", "feature:" + feature.toDebugString());
                    boolean didPass = dataManager.compareAgainstCurrent(swipeArr);
                    if (!didPass)
                        numFail ++;
                    if (numFail >= requiredSwipeLimit*percentCanFail){
                        String toastText = "Intruder detected!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(this, toastText, duration);
                        toast.show();

                        fullCollect.clear();
                        swipe.clear();
                        Intent backToMain = new Intent(this, MainActivity.class);
                        startActivity(backToMain);
                    } else if (numOfSwipes >= requiredSwipeLimit) {

                        String toastText = "Determined to be same USR!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(this, toastText, duration);
                        toast.show();

                        numOfSwipes = 0;
                        numFail = 0;
                        fullCollect.clear();
                        fullCollect = new ConcurrentLinkedQueue<AnalyticDataEntry>();
                    }
                    imageView.setImageDrawable(ImageSelect.RandomImage(this));
                }
                break;
        }
        super.dispatchTouchEvent(event);
        return true;
    }

}
