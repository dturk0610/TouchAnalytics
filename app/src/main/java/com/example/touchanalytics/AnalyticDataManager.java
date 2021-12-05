package com.example.touchanalytics;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

public class AnalyticDataManager implements Parcelable {
    public int selectedUserIndex;
    public int[] usersCSVs;
    public File[] usersCSVFiles;
    public String userID;
    public Float currUserMedDist;
    public ArrayList<AnalyticDataFeatureSet> featuresOfCurrentUser;
    AnalyticDataFeatureSet runningUserUpAverage; int numUpSwipes = 0;
    AnalyticDataFeatureSet runningUserDownAverage; int numDownSwipes = 0;
    AnalyticDataFeatureSet runningUserLeftAverage; int numLeftSwipes = 0;
    AnalyticDataFeatureSet runningUserRightAverage; int numRightSwipes = 0;
    float maxDistanceFromAverage = Float.MIN_VALUE;
    Thread CSVParserThread;
    private Context context;

    private List<AnalyticDataEntry> AllPointsOfData;

    public AnalyticDataManager(Context currentContext, int[] allUserCSVIds){
        this.context = currentContext;
        usersCSVs = allUserCSVIds;
        selectedUserIndex = 0;
        CSVParserThread = new Thread(this::parseCSVFromRaw);
        CSVParserThread.run();
    }

    public AnalyticDataManager(Context currentContext, File[] allUserCSVFiles){
        this.context = currentContext;
        usersCSVFiles = allUserCSVFiles;
        selectedUserIndex = 0;
        CSVParserThread = new Thread(this::parseCSVFromDCIMFiles);
        CSVParserThread.run();
    }


    protected AnalyticDataManager(Parcel in) {
        selectedUserIndex = in.readInt();
        usersCSVs = in.createIntArray();
        userID = in.readString();
        if (in.readByte() == 0) {
            currUserMedDist = null;
        } else {
            currUserMedDist = in.readFloat();
        }
        featuresOfCurrentUser = in.createTypedArrayList(AnalyticDataFeatureSet.CREATOR);
        runningUserUpAverage = in.readParcelable(AnalyticDataFeatureSet.class.getClassLoader());
        numUpSwipes = in.readInt();
        runningUserDownAverage = in.readParcelable(AnalyticDataFeatureSet.class.getClassLoader());
        numDownSwipes = in.readInt();
        runningUserLeftAverage = in.readParcelable(AnalyticDataFeatureSet.class.getClassLoader());
        numLeftSwipes = in.readInt();
        runningUserRightAverage = in.readParcelable(AnalyticDataFeatureSet.class.getClassLoader());
        numRightSwipes = in.readInt();
        maxDistanceFromAverage = in.readFloat();
        usersCSVFiles = (File[])in.readSerializable();
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

    public void rerunDCIMThread(){
        Log.d("", "reRunning parser thread");
        runningUserUpAverage = null; numUpSwipes = 0;
        runningUserDownAverage = null; numDownSwipes = 0;
        runningUserLeftAverage = null; numLeftSwipes = 0;
        runningUserRightAverage = null; numRightSwipes = 0;
        CSVParserThread = new Thread(this::parseCSVFromDCIMFiles);
        CSVParserThread.run();
    }


    public void parseCSVFromRaw(){
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
                String userId = dataRowVals[1];
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

                        if (runningUserUpAverage == null && featureSet.udlrFlag == 0) {
                            runningUserUpAverage = featureSet; numUpSwipes++;
                        }else if (runningUserDownAverage == null && featureSet.udlrFlag == 1) {
                            runningUserDownAverage = featureSet; numDownSwipes++;
                        }else if (runningUserLeftAverage == null && featureSet.udlrFlag == 2) {
                            runningUserLeftAverage = featureSet; numLeftSwipes++;
                        }else if (runningUserRightAverage == null && featureSet.udlrFlag == 3) {
                            runningUserRightAverage = featureSet; numRightSwipes++;
                        }else{
                            switch (featureSet.udlrFlag){
                                case 0: runningUserUpAverage = runningUserUpAverage.add(featureSet); numUpSwipes++; break;
                                case 1: runningUserDownAverage = runningUserDownAverage.add(featureSet); numDownSwipes++; break;
                                case 2: runningUserLeftAverage = runningUserLeftAverage.add(featureSet); numLeftSwipes++; break;
                                case 3: runningUserRightAverage = runningUserRightAverage.add(featureSet); numRightSwipes++; break;
                            }
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
            runningUserUpAverage = runningUserUpAverage.scale(1f/((float)numUpSwipes));
            runningUserDownAverage = runningUserDownAverage.scale(1f/((float)numDownSwipes));
            runningUserLeftAverage = runningUserLeftAverage.scale(1f/((float)numLeftSwipes));
            runningUserRightAverage = runningUserRightAverage.scale(1f/((float)numRightSwipes));

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
                String userId = dataRowVals[1];
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
                        float distFromAvg = 0;
                        switch (featureSet.udlrFlag){
                            case 0: distFromAvg = kNN.weightedDist(runningUserUpAverage, featureSet); break;
                            case 1: distFromAvg = kNN.weightedDist(runningUserDownAverage, featureSet); break;
                            case 2: distFromAvg = kNN.weightedDist(runningUserLeftAverage, featureSet); break;
                            case 3: distFromAvg = kNN.weightedDist(runningUserRightAverage, featureSet); break;
                        }
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
            Log.d("", "largeDist: " + largestDist);
            Log.d("", "smallDist: " + smallDist);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void parseCSVFromDCIMFiles(){
        try {
            File selectedFile = usersCSVFiles[selectedUserIndex];
            InputStream inStream = new FileInputStream(selectedFile);

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
                String userId = dataRowVals[0];
                long eventTime = Long.parseLong(dataRowVals[1]);
                int action = Integer.parseInt(dataRowVals[2]);
                int phoneOrientation = Integer.parseInt(dataRowVals[3]);
                float xCoord = Float.parseFloat(dataRowVals[4]);
                float yCoord = Float.parseFloat(dataRowVals[5]);
                float pressure = Float.parseFloat(dataRowVals[6]);
                float size = Float.parseFloat(dataRowVals[7]);
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
                        swipe.clear();
                        AnalyticDataFeatureSet featureSet = new AnalyticDataFeatureSet(swipeArray);

                        //Log.d("", "feature:" + featureSet.toDebugString());

                        if (runningUserUpAverage == null && featureSet.udlrFlag == 0) {
                            runningUserUpAverage = featureSet; numUpSwipes++;
                        }else if (runningUserDownAverage == null && featureSet.udlrFlag == 1) {
                            runningUserDownAverage = featureSet; numDownSwipes++;
                        }else if (runningUserLeftAverage == null && featureSet.udlrFlag == 2) {
                            runningUserLeftAverage = featureSet; numLeftSwipes++;
                        }else if (runningUserRightAverage == null && featureSet.udlrFlag == 3) {
                            runningUserRightAverage = featureSet; numRightSwipes++;
                        }else{
                            switch (featureSet.udlrFlag){
                                case 0: runningUserUpAverage = runningUserUpAverage.add(featureSet); numUpSwipes++; break;
                                case 1: runningUserDownAverage = runningUserDownAverage.add(featureSet); numDownSwipes++; break;
                                case 2: runningUserLeftAverage = runningUserLeftAverage.add(featureSet); numLeftSwipes++; break;
                                case 3: runningUserRightAverage = runningUserRightAverage.add(featureSet); numRightSwipes++; break;
                            }
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
            if (numUpSwipes > 0)
                runningUserUpAverage = runningUserUpAverage.scale(1f/((float)numUpSwipes));
            if (numDownSwipes > 0)
                runningUserDownAverage = runningUserDownAverage.scale(1f/((float)numDownSwipes));
            if (numLeftSwipes > 0)
                runningUserLeftAverage = runningUserLeftAverage.scale(1f/((float)numLeftSwipes));
            if (numRightSwipes > 0)
                runningUserRightAverage = runningUserRightAverage.scale(1f/((float)numRightSwipes));

            ArrayList<Float> dists = new ArrayList<Float>();
            float largestDist = Float.MIN_VALUE;
            float smallDist = Float.MAX_VALUE;
            br.close();
            inStream = new FileInputStream(usersCSVFiles[selectedUserIndex]);
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
                String userId = dataRowVals[0];
                long eventTime = Long.parseLong(dataRowVals[1]);
                int action = Integer.parseInt(dataRowVals[2]);
                int phoneOrientation = Integer.parseInt(dataRowVals[3]);
                float xCoord = Float.parseFloat(dataRowVals[4]);
                float yCoord = Float.parseFloat(dataRowVals[5]);
                float pressure = Float.parseFloat(dataRowVals[6]);
                float size = Float.parseFloat(dataRowVals[7]);
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
                        float distFromAvg = 0;
                        switch (featureSet.udlrFlag){
                            case 0: distFromAvg = kNN.weightedDist(runningUserUpAverage, featureSet); break;
                            case 1: distFromAvg = kNN.weightedDist(runningUserDownAverage, featureSet); break;
                            case 2: distFromAvg = kNN.weightedDist(runningUserLeftAverage, featureSet); break;
                            case 3: distFromAvg = kNN.weightedDist(runningUserRightAverage, featureSet); break;
                        }
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
            currUserMedDist = medDist;
            Log.d("", "medDist: " + medDist);
            Log.d("", "largeDist: " + largestDist);
            Log.d("", "smallDist: " + smallDist);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean compareAgainstCurrent(AnalyticDataEntry[] testSwipe){
        try {
            if (CSVParserThread != null)
                CSVParserThread.join();
            InputStream inStream = new FileInputStream(usersCSVFiles[selectedUserIndex]);
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            String currentLine;
            ArrayList<AnalyticDataEntry> swipe = new ArrayList<AnalyticDataEntry>();
            ArrayList<Float> dists = new ArrayList<Float>();
            float largestDist = Float.MIN_VALUE;
            float smallDist = Float.MAX_VALUE;
            List<AnalyticDataFeatureSet> allFeatures = new ArrayList<>();
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
                String userId = dataRowVals[0];
                long eventTime = Long.parseLong(dataRowVals[1]);
                int action = Integer.parseInt(dataRowVals[2]);
                int phoneOrientation = Integer.parseInt(dataRowVals[3]);
                float xCoord = Float.parseFloat(dataRowVals[4]);
                float yCoord = Float.parseFloat(dataRowVals[5]);
                float pressure = Float.parseFloat(dataRowVals[6]);
                float size = Float.parseFloat(dataRowVals[7]);
                if (action == MotionEvent.ACTION_DOWN) {
                    swipe = new ArrayList<AnalyticDataEntry>();
                }
                AnalyticDataEntry dataEntry = new AnalyticDataEntry(userId, eventTime, action,
                        phoneOrientation, xCoord, yCoord, pressure, size);
                swipe.add(dataEntry);

                if (action == MotionEvent.ACTION_UP) {
                    if (swipe.size() > 6) {
                        AnalyticDataEntry[] swipeArr = new AnalyticDataEntry[swipe.size()];
                        swipe.toArray(swipeArr);
                        AnalyticDataFeatureSet featureSet = new AnalyticDataFeatureSet(swipeArr);
                        allFeatures.add(featureSet);
                        /*
                        float distFromAvg = 0;
                        switch (featureSet.udlrFlag) {
                            case 0:
                                distFromAvg = kNN.weightedDist(runningUserUpAverage, featureSet);
                                break;
                            case 1:
                                distFromAvg = kNN.weightedDist(runningUserDownAverage, featureSet);
                                break;
                            case 2:
                                distFromAvg = kNN.weightedDist(runningUserLeftAverage, featureSet);
                                break;
                            case 3:
                                distFromAvg = kNN.weightedDist(runningUserRightAverage, featureSet);
                                break;
                        }
                        dists.add(distFromAvg);
                        if (distFromAvg > largestDist) {
                            //Log.d("", "largest: " + Float.toString(distFromAvg));
                            largestDist = distFromAvg;
                        }
                        if (distFromAvg < smallDist) {
                            //Log.d("", "smallest: " + Float.toString(distFromAvg));
                            smallDist = distFromAvg;
                        }*/
                    }
                }
            }
            AnalyticDataFeatureSet testFeatureSet = new AnalyticDataFeatureSet(testSwipe);
            AnalyticDataFeatureSet[] allFeatArr = new AnalyticDataFeatureSet[allFeatures.size()];
            allFeatures.toArray(allFeatArr);
            AnalyticDataFeatureSet[] closetK = kNN.kNN(5,testFeatureSet, allFeatArr);

            /*
            Float[] distances = new Float[dists.size()];
            dists.toArray(distances);
            Arrays.sort(distances);
            float medDist = distances[distances.length/2];
            Log.d("", "smallDistFromTestedUSR: " + smallDist);
            Log.d("", "largeDistFromTestedUSR: " + largestDist);
            Log.d("", "medianDistFromTestedUSR: " + medDist);
            Log.d("", "small dist from usr med: " + (currUserMedDist - smallDist));
            Log.d("", "large dist from usr med: " + (currUserMedDist - largestDist));
            Log.d("", "med dist from usr med: " + (currUserMedDist - medDist));

             */
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;

        }
    }


    public void switchSelectedUser(int index){
        selectedUserIndex = index;
    }

    //ideally this will be the entirety of one user's calibration test
    public boolean AddEntries(List<AnalyticDataEntry> usersEntries){
        String currUserID = usersEntries.get(0).userId;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(selectedUserIndex);
        parcel.writeIntArray(usersCSVs);
        parcel.writeString(userID);
        if (currUserMedDist == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(currUserMedDist);
        }
        parcel.writeTypedList(featuresOfCurrentUser);
        parcel.writeParcelable(runningUserUpAverage, i);
        parcel.writeInt(numUpSwipes);
        parcel.writeParcelable(runningUserDownAverage, i);
        parcel.writeInt(numDownSwipes);
        parcel.writeParcelable(runningUserLeftAverage, i);
        parcel.writeInt(numLeftSwipes);
        parcel.writeParcelable(runningUserRightAverage, i);
        parcel.writeInt(numRightSwipes);
        parcel.writeFloat(maxDistanceFromAverage);
        parcel.writeSerializable(usersCSVFiles);
    }
}