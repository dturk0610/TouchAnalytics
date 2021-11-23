package com.example.touchanalytics;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class AnalyticDataManager {
    public int selectedUserIndex;
    public int[] usersCSVs;
    public ArrayList<AnalyticDataFeatureSet> featuresOfCurrentUser;
    AnalyticDataFeatureSet runningUserAverage;
    float maxDistanceFromAverage;
    Thread CSVParserThread;
    private Context context;

    private List<AnalyticDataEntry> AllPointsOfData;

    public AnalyticDataManager(Context current, int[] allUserCSVIds){
        this.context = current;
        usersCSVs = allUserCSVIds;
        selectedUserIndex = 0;
        CSVParserThread = new Thread();
    }

    public void parseCSV(){
        Thread thisThread = Thread.currentThread();
        String currentLine;
        try {
            InputStream inStream = context.getResources().openRawResource(usersCSVs[selectedUserIndex]);
            ArrayList<AnalyticDataEntry> swipe = new ArrayList<AnalyticDataEntry>();
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            int trueSwipeCount = 0;
            while (CSVParserThread == thisThread && (currentLine = br.readLine()) != null) {

                //From CSV:
                //{0}'phone ID',{1}'user ID',{2}'document ID',{3}'time[ms]',{4}'action'
                //{5}'phone orientation',{6}'x-coordinate',{7}'y-coordinate',{8}'pressure'
                //{9}'area covered',{10}'finger orientation'.
                //What we are using:
                //L{0}'userID',L{1}'eventTime[ms]',I{2}'action',I{3}'phoneOrientation'
                //F{4}'xcoord',F{5}'ycoord',F{6}'pressure',F{7}'area covered'
                String[] dataRowVals = currentLine.split(",");

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
                    swipe = new ArrayList<AnalyticDataEntry>();
                }
                AnalyticDataEntry dataEntry = new AnalyticDataEntry(userId, eventTime, action,
                        phoneOrientation, xCoord, yCoord, pressure, size);
                swipe.add(dataEntry);

                if (action == 1){
                    if (swipe.size() > 6){
                        AnalyticDataEntry[] swipeArray = new AnalyticDataEntry[swipe.size()];
                        swipe.toArray(swipeArray);
                        AnalyticDataFeatureSet featureSet = new AnalyticDataFeatureSet(swipeArray);
                        runningUserAverage = runningUserAverage.add(featureSet);
                        //runningUserAverage;
                        //Log.d("", featureSet.toString());
                        //Log.d("", featureSet.toDebugString());
                        trueSwipeCount += 1;
                        //return;
                    }
                }

            }
            float invSwipeCount = 1f/((float)trueSwipeCount);
            runningUserAverage = runningUserAverage.scale(invSwipeCount);

            float largestDist = Float.MIN_VALUE;

            while (CSVParserThread == thisThread && (currentLine = br.readLine()) != null) {

                //From CSV:
                //{0}'phone ID',{1}'user ID',{2}'document ID',{3}'time[ms]',{4}'action'
                //{5}'phone orientation',{6}'x-coordinate',{7}'y-coordinate',{8}'pressure'
                //{9}'area covered',{10}'finger orientation'.
                //What we are using:
                //L{0}'userID',L{1}'eventTime[ms]',I{2}'action',I{3}'phoneOrientation'
                //F{4}'xcoord',F{5}'ycoord',F{6}'pressure',F{7}'area covered'
                String[] dataRowVals = currentLine.split(",");

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
                    swipe = new ArrayList<AnalyticDataEntry>();
                }
                AnalyticDataEntry dataEntry = new AnalyticDataEntry(userId, eventTime, action,
                        phoneOrientation, xCoord, yCoord, pressure, size);
                swipe.add(dataEntry);

                if (action == 1){
                    if (swipe.size() > 6){
                        AnalyticDataEntry[] swipeArray = new AnalyticDataEntry[swipe.size()];
                        swipe.toArray(swipeArray);
                        AnalyticDataFeatureSet featureSet = new AnalyticDataFeatureSet(swipeArray);
                        float distFromAvg = kNN.dist(runningUserAverage, featureSet);
                        if (distFromAvg > maxDistanceFromAverage){
                            Log.d("", "distFromAvg: " + Float.toString(distFromAvg));
                            maxDistanceFromAverage = distFromAvg;
                        }
                    }
                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class trainUserSet implements EventListener {

    }

    public void switchSelectedUser(int index){

    }

    //ideally this will be the entirety of one user's calibration test
    public boolean AddEntries(List<AnalyticDataEntry> usersEntries){
        long currUserID = usersEntries.get(0).userId;

        //Makes sure that all entries being added are from the same user
        for (AnalyticDataEntry dataEntry : usersEntries){
            if (dataEntry.userId != currUserID){
                return false;
            }
        }
        for (AnalyticDataEntry dataEntry : usersEntries){
            AllPointsOfData.add(dataEntry);
        }
        return true;
    }
}