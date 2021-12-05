package com.example.touchanalytics;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class OpenSaveCSV extends AppCompatActivity {

    //static File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    //static String dataPath = "data/";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        InputStream inStream = getResources().openRawResource(R.raw.data);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while((line = br.readLine()) != null){


            }

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        */
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static boolean WriteToCSV(Context context, ConcurrentLinkedQueue<AnalyticDataEntry> collectionOfSwipes){
        Log.d("", "Attempting to save swipe collect");
        File DCIMDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File dataDCIMDir = new File(DCIMDir, "data");
        if (!dataDCIMDir.exists()){
            boolean worked = dataDCIMDir.mkdirs();
            if (!worked){
                Log.d("uhohh", "uh oh");
                return false;
            }
            Log.d("", "Making DCIM Data path");
        }

        File[] filesDCIM = dataDCIMDir.listFiles();
        int count = (filesDCIM != null) ? filesDCIM.length : -1;
        if (count == -1){
            Log.d("", "other uh oh");
        }
        Log.d("", "count: " + count);
        String userFileName = "user" + count + ".csv";
        File newUserFile = new  File(dataDCIMDir, userFileName);
        AnalyticDataEntry dataEntry;

        try (FileOutputStream fOS = new FileOutputStream(newUserFile.toString())){
            while ((dataEntry = collectionOfSwipes.poll()) != null){
                dataEntry.userId = "" + count;
                String line = dataEntry + "\n";
                fOS.write(line.getBytes());
            }
            return true;
        }catch (Exception e){ e.printStackTrace();  return false; }
    }
}
