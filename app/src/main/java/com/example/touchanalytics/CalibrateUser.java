package com.example.touchanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CalibrateUser extends AppCompatActivity {

    public static ConcurrentLinkedQueue<AnalyticDataEntry> fullCollect;
    public static boolean canSave = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.confirm_registration);
        Button yesBtn = findViewById(R.id.yes_confirmRegistration);
        Button noBtn = findViewById(R.id.no_confirmRegistration);
        EditText usrNameEditTxt = findViewById(R.id.userEditTxt);
        usrNameEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    String text = editable.toString();
                    if (OpenSaveCSV.DoesFileAlreadyExistInDCIM(text, getBaseContext())){

                        String toastText = "User already Exists, try again";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getBaseContext(), toastText, duration);
                        toast.show();
                    }
                    else{
                        canSave = true;
                    }
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canSave) {
                    boolean worked = OpenSaveCSV.WriteToCSV(view.getContext(), fullCollect, usrNameEditTxt.getText().toString()); //------ if clicked yes on calibration page, run this
                    if (worked) {
                        Intent backToMain = new Intent(view.getContext(), MainActivity.class);
                        startActivity(backToMain);
                    }
                }else{

                    String toastText = "Give valid username, or hit no";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(view.getContext(), toastText, duration);
                    toast.show();
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
