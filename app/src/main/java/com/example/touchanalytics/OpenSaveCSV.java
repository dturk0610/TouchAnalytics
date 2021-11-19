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

    ArrayList<AnalyticDataManager.AnalyticDataEntry> allDataEntriesFromCSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("", "Hello");
        Log.d("", "Hello");
        Log.d("", "Hello");
        Log.d("", "Hello");
        Log.d("", "Hello");
        Log.d("", "Hello");
        Log.d("", "Hello");
        ArrayList<AnalyticDataManager.AnalyticDataEntry> swipe =
                new ArrayList<AnalyticDataManager.AnalyticDataEntry>();
        InputStream inStream = getResources().openRawResource(R.raw.data);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while((line = br.readLine()) != null){
                //From CSV:
                //{0}'phone ID',{1}'user ID',{2}'document ID',{3}'time[ms]',{4}'action'
                //{5}'phone orientation',{6}'x-coordinate',{7}'y-coordinate',{8}'pressure'
                //{9}'area covered',{10}'finger orientation'.
                //What we are using:
                //L{0}'userID',L{1}'eventTime[ms]',I{2}'action',I{3}'phoneOrientation'
                //F{4}'xcoord',F{5}'ycoord',F{6}'pressure',F{7}'area covered'
                String[] dataRowVals = line.split(",");

                //the index passed in here will be from the first list above
                long userId = Long.parseLong(dataRowVals[1]);
                long eventTime = Long.parseLong(dataRowVals[3]);
                int action = Integer.parseInt(dataRowVals[4]);
                int phoneOrientation = Integer.parseInt(dataRowVals[5]);
                float xCoord = Float.parseFloat(dataRowVals[6]);
                float yCoord = Float.parseFloat(dataRowVals[7]);
                float pressure = Float.parseFloat(dataRowVals[8]);
                float size = Float.parseFloat(dataRowVals[9]);
                if (action == 0) {
                    swipe = new ArrayList<AnalyticDataManager.AnalyticDataEntry>();
                }
                AnalyticDataManager.AnalyticDataEntry dataEntry =
                        new AnalyticDataManager.AnalyticDataEntry(userId,
                        eventTime, action, phoneOrientation, xCoord, yCoord, pressure, size);
                swipe.add(dataEntry);

                if (action == 1){
                    if (swipe.size() > 6){
                        AnalyticDataManager.AnalyticDataEntry[] swipeArray =
                                new AnalyticDataManager.AnalyticDataEntry[swipe.size()];
                        swipe.toArray(swipeArray);
                        AnalyticDataManager.AnalyticDataFeatureSet featureSet =
                                new AnalyticDataManager.AnalyticDataFeatureSet(swipeArray);
                        Log.d("", featureSet.toString());
                        Log.d("", featureSet.toDebugString());
                        return;
                    }
                }

            }

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }


    /*
    try
    {
        //parsing a CSV file into BufferedReader class constructor
        BufferedReader br = new BufferedReader(new FileReader("CSVDemo.csv"));
        while ((line = br.readLine()) != null)   //returns a Boolean value
        {
            String[] employee = line.split(splitBy);    // use comma as separator
            System.out.println("Employee [First Name=" + employee[0] + ", Last Name=" + employee[1] + ", Designation=" + employee[2] + ", Contact=" + employee[3] + ", Salary= " + employee[4] + ", City= " + employee[5] +"]");
        }
    }
    catch (IOException e)
    {
        e.printStackTrace();
    } */


}
