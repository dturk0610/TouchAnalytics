package com.example.touchanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CalibrateUser extends AppCompatActivity {

    public static ConcurrentLinkedQueue<AnalyticDataEntry> fullCollect;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.confirm_registration);
        Button yesBtn = findViewById(R.id.yes_confirmRegistration);
        Button noBtn = findViewById(R.id.no_confirmRegistration);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean worked = OpenSaveCSV.WriteToCSV(view.getContext(), fullCollect); //------ if clicked yes on calibration page, run this
                if (worked){
                    Intent backToMain = new Intent(view.getContext(), MainActivity.class);
                    startActivity(backToMain);

                }

            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullCollect.clear();
                Intent backToMain = new Intent(view.getContext(), MainActivity.class);
                startActivity(backToMain);

            }
        });



        //startActivity(openCSV);
    }
}
