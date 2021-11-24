package com.example.touchanalytics;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

public class AnalyticDataManager implements Parcelable {
    public int selectedUserIndex;
    public int[] usersCSVs;
    public long userID;
    public ArrayList<AnalyticDataFeatureSet> featuresOfCurrentUser;
    AnalyticDataFeatureSet runningUserAverage;
    float maxDistanceFromAverage = Float.MIN_VALUE;
    Thread CSVParserThread;
    private Context context;

    private List<AnalyticDataEntry> AllPointsOfData;

    public AnalyticDataManager(Context currentContext, int[] allUserCSVIds){
        this.context = currentContext;
        usersCSVs = allUserCSVIds;
        selectedUserIndex = 0;
        CSVParserThread = new Thread(this::parseCSV);
        CSVParserThread.run();
    }

    protected AnalyticDataManager(Parcel in) {
        selectedUserIndex = in.readInt();
        usersCSVs = in.createIntArray();
        userID = in.readLong();
        maxDistanceFromAverage = in.readFloat();
    }

    public static final Creator<AnalyticDataManager> CREATOR = new Creator<AnalyticDataManager>() {
        @Override
        public AnalyticDataManager createFromParcel(Parcel in) {
            return new AnalyticDataManager(in);
        }

        @Override
        public AnalyticDataManager[] newArray(int size) {
            return new AnalyticDataManager[size];
        }
    };

    public void parseCSV(){
        try {
            InputStream inStream = context.getResources().openRawResource(usersCSVs[selectedUserIndex]);
            ArrayList<AnalyticDataEntry> swipe = new ArrayList<AnalyticDataEntry>();
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            String currentLine;
            int trueSwipeCount = 0;
            while ((currentLine = br.readLine()) != null) {
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
                if (action == MotionEvent.ACTION_DOWN) {
                    this.userID = userId;
                    swipe = new ArrayList<AnalyticDataEntry>();
                }
                AnalyticDataEntry dataEntry = new AnalyticDataEntry(userId, eventTime, action,
                        phoneOrientation, xCoord, yCoord, pressure, size);
                swipe.add(dataEntry);

                if (action == MotionEvent.ACTION_UP){
                    if (swipe.size() > 6){
                        AnalyticDataEntry[] swipeArray = new AnalyticDataEntry[swipe.size()];
                        swipe.toArray(swipeArray);
                        AnalyticDataFeatureSet featureSet = new AnalyticDataFeatureSet(swipeArray);
                        if (runningUserAverage == null) {
                            runningUserAverage = featureSet;
                        }else{
                            runningUserAverage = runningUserAverage.add(featureSet);
                        }
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

            ArrayList<Float> dists = new ArrayList<Float>();
            float largestDist = Float.MIN_VALUE;
            float smallDist = Float.MAX_VALUE;
            br.close();
            inStream = context.getResources().openRawResource(usersCSVs[selectedUserIndex]);
            br = new BufferedReader(new InputStreamReader(inStream));
            while ((currentLine = br.readLine()) != null) {
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
                if (action == MotionEvent.ACTION_DOWN) {
                    swipe = new ArrayList<AnalyticDataEntry>();
                }
                AnalyticDataEntry dataEntry = new AnalyticDataEntry(userId, eventTime, action,
                        phoneOrientation, xCoord, yCoord, pressure, size);
                swipe.add(dataEntry);

                if (action == MotionEvent.ACTION_UP){
                    if (swipe.size() > 6){
                        AnalyticDataEntry[] swipeArray = new AnalyticDataEntry[swipe.size()];
                        swipe.toArray(swipeArray);
                        AnalyticDataFeatureSet featureSet = new AnalyticDataFeatureSet(swipeArray);
                        float distFromAvg = kNN.weightedDist(runningUserAverage, featureSet);
                        dists.add(distFromAvg);
                        if (distFromAvg > largestDist){
                            //Log.d("", "largest: " + Float.toString(distFromAvg));
                            largestDist = distFromAvg;
                        }
                        if (distFromAvg < smallDist){
                            //Log.d("", "smallest: " + Float.toString(distFromAvg));
                            smallDist = distFromAvg;
                        }
                    }
                }
            }

            Float[] distances = new Float[dists.size()];
            dists.toArray(distances);
            Arrays.sort(distances);
            float medDist = distances[distances.length/2];
            Log.d("", "medDist: " + medDist);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(selectedUserIndex);
        parcel.writeArray(new int[][]{usersCSVs});
        parcel.writeLong(userID);
    }

    public void switchSelectedUser(int index){
        selectedUserIndex = index;
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