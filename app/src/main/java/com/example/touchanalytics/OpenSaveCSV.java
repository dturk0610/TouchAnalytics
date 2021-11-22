package com.example.touchanalytics;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OpenSaveCSV extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InputStream inStream = getResources().openRawResource(R.raw.data);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while((line = br.readLine()) != null){


            }

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
