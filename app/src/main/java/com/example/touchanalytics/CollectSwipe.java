package com.example.touchanalytics;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CollectSwipe extends AppCompatActivity{

    AnalyticDataManager manager;
    ConcurrentLinkedQueue<AnalyticDataEntry> swipe;
    ConcurrentLinkedQueue<AnalyticDataEntry> fullCollect;
    int numOfSwipes = 0;
    public int requiredSwipeLimit = 20;
    ImageView imgView;
    TextView txtView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.manager = bundle.getParcelable("manager");
        setContentView(R.layout.calibration);               ///////here we will set the view -----------
        swipe = new ConcurrentLinkedQueue<>();
        fullCollect = new ConcurrentLinkedQueue<>();
        imgView = findViewById(R.id.calibrationImgView);
        txtView = findViewById(R.id.swipe_amount);
        txtView.setText(String.format("%d/%d", 0, requiredSwipeLimit));

        Button backBtn = findViewById(R.id.cancelCaliBtn);
        backBtn.setText("Cancel Calibration");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullCollect.clear();
                swipe.clear();
                Intent backToMain = new Intent(view.getContext(), MainActivity.class);
                startActivity(backToMain);
            }
        });

        //imgView.setImageDrawable(ImageSelect.RandomImage(this));
        //Intent openCSV = new Intent(this, OpenSaveCSV.class);
        //startActivity(openCSV);
    }

    public boolean dispatchTouchEvent( MotionEvent event ) {
        // Log.w( MA, "Inside onTouchEvent" );
        View v = getCurrentFocus();
        String userID = manager.userID;
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
                    swipe.clear();
                    AnalyticDataFeatureSet feature = new AnalyticDataFeatureSet(swipeArr);
                    Log.d("", "feature:" + feature.toDebugString());
                    if (numOfSwipes >= requiredSwipeLimit) {

                        Intent confirmIntent = new Intent(this, CalibrateUser.class);
                        CalibrateUser.fullCollect = fullCollect;
                        startActivity(confirmIntent);
                        numOfSwipes = 0;
                        fullCollect = new ConcurrentLinkedQueue<>();
                        /*

                        code to setup confirmation page -make function to call to do this -----------

                         */
                    }
                    imgView.setImageDrawable(ImageSelect.RandomImage(this));
                }
                break;
        }
        super.dispatchTouchEvent(event);
        return true;
    }


}
