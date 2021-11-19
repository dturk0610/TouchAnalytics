package com.example.touchanalytics;

import android.app.Activity;
import android.view.MotionEvent;

import java.util.List;

public class AnalyticDataManager {
    /*
    [userId,eventTime,action,phoneOrientation,xCoord,yCoord,pressure,size]
    */
    public class AnalyticDataEntry implements java.io.Serializable {
        /*
         * 'phone ID', - won't be used

         * anonymous user - possibly registered user
         * 'user ID', - can be used (registered user)

         * 'document ID', - can be used for the pictures

         * absolute time of recorded action (ms since 1970).
         * 'time[ms]', - will be used

         * can take three values
         *   0: touch down,
         *   1: touch up,
         *   2: move finger on screen.
         * In our paper, a stroke is defined as all actions between a 0 and a 1 if there is a xy-displacement between these actions.
         * Clicks are actions between 0 and 1 without displacement.
         * 'action', - will be used, same as specified above

         * returned from android api during current action
         * 'phone orientation', - not sure what the interpretation of this is, maybe 0-3 for which direction the phone is being held. 0 for home button at bottom and phone being held vertically?

         * returned from android api during current action
         * 'x-coordinate', - self explanatory, x coord. on the screen

         * returned from android api during current action
         * 'y-coordinate', - self explanatory, y coord. on the screen

         * returned from android api during current action
         * 'pressure', - may not be able to be recorded from our devices, but we can try to implement it

         * returned from android api during current action
         * 'area covered', - The amount of area the finger is covering

         * returned from android api during current action
         * 'finger orientation'. - I don't understand this value, I don't believe that we will use it.

         * */
        private long userId;

        //Get the below values using the MotionEvent.obtain method
        private long eventTime;
        private int action;
        private int phoneOrientation;
        private float xCoord;
        private float yCoord;
        private float pressure;
        private float size; //the area of which the finger takes up on the screen

        public AnalyticDataEntry(long userId, long eventTime, int action, Activity activity, float xCoord, float yCoord, float pressure, float size) {
            this.userId = userId;
            this.eventTime = eventTime;
            this.action = action;
            this.phoneOrientation = activity.getRequestedOrientation();
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.pressure = pressure;
            this.size = size;
        }

        public AnalyticDataEntry(long userId, Activity activity, MotionEvent moEv) {
            this.userId = userId;
            this.eventTime = moEv.getEventTime();
            this.action = moEv.getAction();
            this.phoneOrientation = activity.getRequestedOrientation();
            this.xCoord = moEv.getX();
            this.yCoord = moEv.getY();
            this.pressure = moEv.getPressure();
            this.size = moEv.getSize();
        }

        public String ToString() {
            String ret = "";
            ret += Long.toString(userId) + ",";
            ret += Long.toString(eventTime) + ",";
            ret += Integer.toString(action) + ",";
            ret += Integer.toString(phoneOrientation) + ",";
            ret += Float.toString(xCoord) + ",";
            ret += Float.toString(yCoord) + ",";
            ret += Float.toString(pressure) + ",";
            ret += Float.toString(size);
            return ret;
        }
    }

    public class AnalyticDataFeatureSet implements java.io.Serializable{
        /*
        * 20.58%    mid-stroke area covered
        * 19.63%    20%-perc. pairwise velocity
        * 17.28%    mid-stroke pressure
        * 11.06%    direction of end-to-end line
        * 10.32%    stop x
        * 10.15%    start x
        * 9.45%     average direction
        * 9.43%     start y
        * 8.84%     average velocity
        * 8.61%     stop y
        * 8.5%      stroke duration
        * 8.27%     direct end-to-end distance
        * 8.16%     length of trajectory
        * 7.85%     80%-perc. pairwise velocity
        * 7.24%     median velocity at last 3 pts
        * 7.22%     50%-perc. pairwise velocity
        * 7.07%     20%-perc. pairwise acc
        * 6.29%     ratio end-to-end dist and length of trajectory
        * 6.08%     largest deviation from end-to-end line
        * 5.96%     80%-perc. pairwise acc
        * 5.82%     mean resultant length
        * 5.42%     median acceleration at first 5 points
        * 5.39%     50%-perc. dev. from end-to-end line
        * 5.3%      inter-stroke time
        * 5.14%     80%-perc. dev. from end-to-end line
        * 5.04%     20%-perc. dev. from end-to-end line
        * 5.04%     50%-perc. pairwise acc
        * 3.44%     phone orientation
        * 3.08%     mid-stroke finger orientation
        * 0.97%     up/down/left/right flag
        * 0%        change of finger orientation
        * */
        long userId;                                                    /*done*/
        float midStrokeArea;                                            /*done*/
        float midStrokePressure;                                        /*done*/
        float avgVel;                                                   /*done*/
        float directEtoEDist;                                           /*done*/
        float lengthOfTrajectory;                                       /*done*/
        float ratiodirectEtoEDistandlengthOfTrajectory;                 /*done*/
        float largestDeviationFromEtoELine;                             /*done*/
        float meanResultantLength;
        float medianVelocityAtLast3pts, medianAccelAtFirst5Points;

        float vel20per,vel50per,vel80per;                               /*done*/
        float accel20per, accel50per, accel80per;                       /*done*/
        float deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine; /*done*/

        float[] dirEtoELine;                                            /*done*/
        float[] avgDir;                                                 /*done*/
        float startx, stopx, starty, stopy;                             /*done*/
        long strokeDuration                                             /*[ms] done*/;
        int phoneOrientation;                                           /*done*/
        int udlrFlag; //up, down, left, right flag 0 - 3 respectively   /*done*/

        //long interStrokeTime; wont be used?
        //change of finger orientation ?? how is this even calculated
        //mid-stroke finger orientation ?? how is this one even gotten

        float[] u = {0, -1}, d = {0, 1}, l = {-1, 0}, r = {1, 0};

        public AnalyticDataFeatureSet(AnalyticDataEntry[] swipe){
            try {
                if (swipe.length < 6) { //6 is not arbitrary, one of the functions requires the last 5 points
                    throw new Exception("not enough point to build dataset");
                }
                userId = swipe[0].userId;
                int arrSize = swipe.length;
                float invArrSize = 1/((float)arrSize);
                midStrokeArea = swipe[arrSize / 2].size;
                midStrokePressure = swipe[arrSize / 2].pressure;
                startx = swipe[0].xCoord;
                stopx = swipe[arrSize - 1].xCoord;
                starty = swipe[0].xCoord;
                stopy = swipe[arrSize - 1].yCoord;
                float[] startPos = {startx, starty};
                float[] stopPos = {startx, starty};

                float lastXmFirst = sub(stopPos, startPos)[0];
                float lastYmFirst = sub(stopPos, startPos)[1];
                directEtoEDist = (float) Math.sqrt(lastXmFirst * lastXmFirst + lastYmFirst * lastYmFirst);
                dirEtoELine = new float[]{lastXmFirst / directEtoEDist, lastYmFirst / directEtoEDist};
                float uDotDirEtoE = dot(dirEtoELine, u), dDotDirEtoE = dot(dirEtoELine, d);
                float lDotDirEtoE = dot(dirEtoELine, l), rDotDirEtoE = dot(dirEtoELine, r);
                if ((uDotDirEtoE > dDotDirEtoE) && (uDotDirEtoE > lDotDirEtoE) && (uDotDirEtoE > rDotDirEtoE))
                    udlrFlag = 0;
                if ((dDotDirEtoE > uDotDirEtoE) && (dDotDirEtoE > lDotDirEtoE) && (dDotDirEtoE > rDotDirEtoE))
                    udlrFlag = 1;
                if ((lDotDirEtoE > uDotDirEtoE) && (lDotDirEtoE > dDotDirEtoE) && (lDotDirEtoE > rDotDirEtoE))
                    udlrFlag = 2;
                if ((rDotDirEtoE > uDotDirEtoE) && (rDotDirEtoE > lDotDirEtoE) && (rDotDirEtoE > dDotDirEtoE))
                    udlrFlag = 3;

                phoneOrientation = swipe[0].phoneOrientation;
                long startTime = swipe[0].eventTime;
                long endTime = swipe[arrSize - 1].eventTime;
                strokeDuration = endTime - startTime;

                int per20Size = (int)(arrSize*.2f);
                int per50Size = (int)(arrSize*.5f);
                int per80Size = (int)(arrSize*.8f);

                double per20TimeDiff = (swipe[per20Size].eventTime - swipe[per20Size - 1].eventTime)*.001;
                double invPer20TimeDiff = 1/per20TimeDiff;
                double per50TimeDiff = (swipe[per50Size].eventTime - swipe[per50Size - 1].eventTime)*.001;
                double invPer50TimeDiff = 1/per50TimeDiff;
                double per80TimeDiff = (swipe[per80Size].eventTime - swipe[per80Size - 1].eventTime)*.001;
                double invPer80TimeDiff = 1/per80TimeDiff;

                float pos20x = swipe[per20Size].xCoord;
                float pos50x = swipe[per50Size].xCoord;
                float pos80x = swipe[per80Size].xCoord;
                float pos20y = swipe[per20Size].yCoord;
                float pos50y = swipe[per50Size].yCoord;
                float pos80y = swipe[per80Size].yCoord;
                float[] pos20 = {pos20x, pos20y};
                float[] pos50 = {pos50x, pos50y};
                float[] pos80 = {pos80x, pos80y};

                float vel20x = (float)((pos20x - swipe[per20Size - 1].xCoord)*invPer20TimeDiff);
                float vel50x = (float)((pos50x - swipe[per50Size - 1].xCoord)*invPer50TimeDiff);
                float vel80x = (float)((pos80x - swipe[per80Size - 1].xCoord)*invPer80TimeDiff);
                float vel20y = (float)((pos20y - swipe[per20Size - 1].yCoord)*invPer20TimeDiff);
                float vel50y = (float)((pos50y - swipe[per50Size - 1].yCoord)*invPer50TimeDiff);
                float vel80y = (float)((pos80y - swipe[per80Size - 1].yCoord)*invPer80TimeDiff);

                vel20per = (float)Math.sqrt(vel20x*vel20x + vel20y*vel20y);
                vel50per = (float)Math.sqrt(vel50x*vel50x + vel50y*vel50y);
                vel80per = (float)Math.sqrt(vel80x*vel80x + vel80y*vel80y);
                accel20per =(float)(vel20per*invPer20TimeDiff);
                accel50per =(float)(vel50per*invPer50TimeDiff);
                accel80per =(float)(vel80per*invPer80TimeDiff);

                float[] posAlongLineAt20 = add(mult(dirEtoELine, dot(sub(pos20,startPos), dirEtoELine)), startPos);
                float[] posAlongLineAt50 = add(mult(dirEtoELine, dot(sub(pos50,startPos), dirEtoELine)), startPos);
                float[] posAlongLineAt80 = add(mult(dirEtoELine, dot(sub(pos80,startPos), dirEtoELine)), startPos);

                deviation20PercFromEtoELine = mag(sub(pos20, posAlongLineAt20));
                deviation50PercFromEtoELine = mag(sub(pos50, posAlongLineAt50));
                deviation80PercFromEtoELine = mag(sub(pos80, posAlongLineAt80));

                avgVel = 0;
                lengthOfTrajectory = 0;
                avgDir = new float[]{0, 0};
                largestDeviationFromEtoELine = 0;

                for (int i = 1; i < arrSize; i++){
                    AnalyticDataEntry currData = swipe[i];
                    AnalyticDataEntry prevData = swipe[i-1];

                    float[] currPos = new float[]{currData.xCoord, currData.yCoord};
                    float[] lastPos = new float[]{prevData.xCoord, prevData.yCoord};
                    float[] displacement = sub(currPos, lastPos);
                    float magDisp = mag(displacement);
                    float[] currDir = mult(displacement, 1/magDisp);
                    lengthOfTrajectory +=magDisp;
                    avgDir = add(avgDir, mult(currDir, invArrSize));

                    long currTime = currData.eventTime;
                    long lastTime = prevData.eventTime;
                    double invTimeStep = 1/((lastTime - currTime)*.001);

                    float currVel = (float)(magDisp*invTimeStep);
                    avgVel += currVel*invArrSize;

                    float[] posAlongLine = add(mult(dirEtoELine, dot(sub(currPos,startPos), dirEtoELine)), startPos);

                    float currDeviation = mag(sub(currPos, posAlongLine));
                    if (currDeviation > largestDeviationFromEtoELine)
                        largestDeviationFromEtoELine = currDeviation;
                }

                ratiodirectEtoEDistandlengthOfTrajectory = directEtoEDist/lengthOfTrajectory;

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }



        private float dot(float[] a, float[] b){ return a[0]*b[0] + a[1]*b[1]; }
        private float[] add(float[] a, float[] b){ return new float[]{a[0] + b[0], a[1] + b[1]}; }
        private float[] sub(float[] a, float[] b){ return new float[]{a[0] - b[0], a[1] - b[1]}; }
        private float[] mult(float[] pos, float scale){ return new float[]{pos[0] * scale, pos[1] * scale}; }
        private float mag(float[] v){ return (float)Math.sqrt(v[0]*v[0]+v[1]*v[1]); }
    }

    private List<AnalyticDataEntry> AllPointsOfData;

    //ideally this will be the entirety of one user's calibration test
    public boolean AddEntries(List<AnalyticDataEntry> usersEntries){
        long currUserID = usersEntries.get(0).userId;
        for (AnalyticDataEntry dataEntry : usersEntries){
            if (dataEntry.userId != currUserID){
                return false;
            }
        }
        for (AnalyticDataEntry dataEntry : usersEntries){
            if (dataEntry.userId != currUserID){
                return false;
            }
        }
        return true;
    }
}