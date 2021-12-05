package com.example.touchanalytics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.touchanalytics.AnalyticDataEntry;

public class AnalyticDataFeatureSet implements java.io.Serializable , Parcelable {
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
    String userId;                                                  /*done*/
    float midStrokeArea;                                            /*done*/
    float midStrokePressure;                                        /*done*/
    float avgVel;                                                   /*done*/
    float directEtoEDist;                                           /*done*/
    float lengthOfTrajectory;                                       /*done*/
    float ratiodirectEtoEDistandlengthOfTrajectory;                 /*done*/
    float largestDeviationFromEtoELine;                             /*done*/
    float medianVelocityAtLast3pts, medianAccelAtFirst5Points;      /*done*/

    float vel20per,vel50per,vel80per;                               /*done*/
    float accel20per, accel50per, accel80per;                       /*done*/
    float deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine; /*done*/

    float dirEtoELine;                                            /*done*/
    float avgDir;                                                 /*done*/
    float startx, stopx, starty, stopy;                             /*done*/
    long strokeDuration;                                            /*[ms] done*/
    int phoneOrientation;                                           /*done*/
    int udlrFlag; //up, down, left, right flag 0 - 3 respectively   /*done*/

    //float meanResultantLength;
    //long interStrokeTime; wont be used?
    //change of finger orientation ?? how is this even calculated
    //mid-stroke finger orientation ?? how is this one even gotten

    float[] u = {0, -1}, d = {0, 1}, l = {-1, 0}, r = {1, 0};

    public AnalyticDataFeatureSet (String userId, float midStrokeArea, float midStrokePressure,
            float avgVel, float directEtoEDist, float lengthOfTrajectory, float ratiodirectEtoEDistandlengthOfTrajectory,
            float largestDeviationFromEtoELine, float medianVelocityAtLast3pts, float medianAccelAtFirst5Points,
            float vel20per, float vel50per, float vel80per, float accel20per, float accel50per, float accel80per,
            float deviation20PercFromEtoELine, float deviation50PercFromEtoELine, float deviation80PercFromEtoELine,
            float dirEtoELine, float avgDir, float startx, float stopx, float starty, float stopy,
            long strokeDuration, int phoneOrientation, int udlrFlag){
        this.userId = userId;
        this.midStrokeArea = midStrokeArea;
        this.midStrokePressure = midStrokePressure;
        this.avgVel = avgVel;
        this.directEtoEDist = directEtoEDist;
        this.lengthOfTrajectory = lengthOfTrajectory;
        this.ratiodirectEtoEDistandlengthOfTrajectory = ratiodirectEtoEDistandlengthOfTrajectory;
        this.largestDeviationFromEtoELine = largestDeviationFromEtoELine;
        this.medianVelocityAtLast3pts = medianVelocityAtLast3pts;
        this.medianAccelAtFirst5Points = medianAccelAtFirst5Points;
        this.vel20per = vel20per;
        this.vel50per = vel50per;
        this.vel80per = vel80per;
        this.accel20per = accel20per;
        this.accel50per = accel50per;
        this.accel80per = accel80per;
        this.deviation20PercFromEtoELine = deviation20PercFromEtoELine;
        this.deviation50PercFromEtoELine = deviation50PercFromEtoELine;
        this.deviation80PercFromEtoELine = deviation80PercFromEtoELine;
        this.dirEtoELine = dirEtoELine;
        this.avgDir = avgDir;
        this.startx = startx;
        this.stopx = stopx;
        this.starty = starty;
        this.stopy = stopy;
        this.strokeDuration = strokeDuration;
        this.phoneOrientation = phoneOrientation;
        this.udlrFlag = udlrFlag;
    }

    public AnalyticDataFeatureSet(AnalyticDataEntry[] swipe){
        try {
            if (swipe.length < 6) { //6 is not arbitrary, one of the functions requires the last 5 points
                throw new Exception("not enough point to build dataset");
            }
            userId = swipe[0].userId;
            int arrSize = swipe.length;
            float invArrSize = 1f/(arrSize-1);

            midStrokeArea = swipe[arrSize / 2].size;
            midStrokePressure = swipe[arrSize / 2].pressure;
            startx = swipe[0].xCoord;
            stopx = swipe[arrSize - 1].xCoord;
            starty = swipe[0].yCoord;
            stopy = swipe[arrSize - 1].yCoord;
            float[] startPos = {startx, starty};
            float[] stopPos = {stopx, stopy};
            float[] diffVec = subVec(stopPos, startPos);

            directEtoEDist = magVec(diffVec);
            dirEtoELine = (float)Math.atan(diffVec[1]/diffVec[0]);
            float uDotDirEtoE = dotVec(diffVec, u), dDotDirEtoE = dotVec(diffVec, d);
            float lDotDirEtoE = dotVec(diffVec, l), rDotDirEtoE = dotVec(diffVec, r);
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
            double invPer20TimeDiff = (per20TimeDiff != 0) ? 1/per20TimeDiff : 0;
            double per50TimeDiff = (swipe[per50Size].eventTime - swipe[per50Size - 1].eventTime)*.001;
            double invPer50TimeDiff = (per50TimeDiff !=0 ) ?  1/per50TimeDiff : 0;
            double per80TimeDiff = (swipe[per80Size].eventTime - swipe[per80Size - 1].eventTime)*.001;
            double invPer80TimeDiff = (per80TimeDiff !=0 ) ?  1/per80TimeDiff : 0;

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
            accel20per = (float)(vel20per*invPer20TimeDiff);
            accel50per = (float)(vel50per*invPer50TimeDiff);
            accel80per = (float)(vel80per*invPer80TimeDiff);

            float[] normDiffVec = normVec(diffVec);
            float[] posAlongLineAt20 = addVec(scaleVec(normDiffVec, dotVec(subVec(pos20,startPos), normDiffVec)), startPos);
            float[] posAlongLineAt50 = addVec(scaleVec(normDiffVec, dotVec(subVec(pos50,startPos), normDiffVec)), startPos);
            float[] posAlongLineAt80 = addVec(scaleVec(normDiffVec, dotVec(subVec(pos80,startPos), normDiffVec)), startPos);

            deviation20PercFromEtoELine = magVec(subVec(pos20, posAlongLineAt20));
            deviation50PercFromEtoELine = magVec(subVec(pos50, posAlongLineAt50));
            deviation80PercFromEtoELine = magVec(subVec(pos80, posAlongLineAt80));

            avgVel = 0;
            lengthOfTrajectory = 0;
            avgDir = 0;
            largestDeviationFromEtoELine = 0;

            float[] pos0 = {swipe[0].xCoord, swipe[0].yCoord};
            float[] pos1 = {swipe[1].xCoord, swipe[1].yCoord};
            float[] pos2 = {swipe[2].xCoord, swipe[2].yCoord};
            float[] pos3 = {swipe[3].xCoord, swipe[3].yCoord};
            float[] pos4 = {swipe[4].xCoord, swipe[4].yCoord};
            float[] pos5 = {swipe[5].xCoord, swipe[5].yCoord};
            double invTimeStep1 = 1/((swipe[1].eventTime - swipe[0].eventTime)*.001);
            double invTimeStep2 = 1/((swipe[2].eventTime - swipe[1].eventTime)*.001);
            double invTimeStep3 = 1/((swipe[3].eventTime - swipe[2].eventTime)*.001);
            double invTimeStep4 = 1/((swipe[4].eventTime - swipe[3].eventTime)*.001);
            double invTimeStep5 = 1/((swipe[5].eventTime - swipe[4].eventTime)*.001);
            float accel1 = (float)(magVec(subVec(pos1, pos0))*invTimeStep1*invTimeStep1);
            float accel2 = (float)(magVec(subVec(pos2, pos1))*invTimeStep2*invTimeStep2);
            float accel3 = (float)(magVec(subVec(pos3, pos2))*invTimeStep3*invTimeStep3);
            float accel4 = (float)(magVec(subVec(pos4, pos3))*invTimeStep4*invTimeStep4);
            float accel5 = (float)(magVec(subVec(pos5, pos4))*invTimeStep5*invTimeStep5);

            float[] accels = {accel1, accel2, accel3, accel4, accel5};
            java.util.Arrays.sort(accels);
            medianAccelAtFirst5Points = accels[2];  //should be sorted now and the middle index
            //will be the median

            //                                  -2 to get us to actually last point since the
            //                                  last and second to last are the same, -[0-3] for
            //                                  obvious reasons
            int lastInd = arrSize - 2;
            int secToLasInd = lastInd - 1;
            int thrToLasInd = secToLasInd - 1;
            int fourToLasInd = thrToLasInd - 1;
            float[] posLast         =   {swipe[lastInd].xCoord, swipe[lastInd].yCoord};
            float[] posSecondToLast =   {swipe[secToLasInd].xCoord, swipe[secToLasInd].yCoord};
            float[] posThirdToLast  =   {swipe[thrToLasInd].xCoord, swipe[thrToLasInd].yCoord};
            float[] posFourthToLast =   {swipe[fourToLasInd].xCoord, swipe[fourToLasInd].yCoord};
            double invTimeStepLast = 1/((swipe[lastInd].eventTime - swipe[secToLasInd].eventTime)*.001);
            double invTimeStep2ToLast = 1/((swipe[secToLasInd].eventTime - swipe[thrToLasInd].eventTime)*.001);
            double invTimeStep3ToLast = 1/((swipe[thrToLasInd].eventTime - swipe[fourToLasInd].eventTime)*.001);
            float lastVel = (float)(magVec(subVec(posLast, posSecondToLast))*invTimeStepLast);
            float secToLastVel = (float)(magVec(subVec(posSecondToLast, posThirdToLast))*invTimeStep2ToLast);
            float thrToLastVel = (float)(magVec(subVec(posThirdToLast, posFourthToLast))*invTimeStep3ToLast);

            float[] lastVels = {lastVel, secToLastVel, thrToLastVel};
            java.util.Arrays.sort(lastVels);
            medianVelocityAtLast3pts = lastVels[1]; //1 here because the array size is 3 and is
            //now sorted making the index 1 the median

            for (int i = 1; i < arrSize - 1; i++){
                AnalyticDataEntry currData = swipe[i];
                AnalyticDataEntry prevData = swipe[i-1];

                float[] currPos = new float[]{currData.xCoord, currData.yCoord};
                float[] lastPos = new float[]{prevData.xCoord, prevData.yCoord};
                float[] displacement = subVec(currPos, lastPos);
                float magDisp = magVec(displacement);
                float currDir = 0;
                if (displacement[0] != 0){
                    currDir = (float)Math.atan(displacement[1]/displacement[0]);
                }
                else{
                    float negator = 1f;
                    if (displacement[1] < 0)
                        negator = -1f;
                    currDir = negator*90*(float)Math.PI/180f;
                }
                lengthOfTrajectory +=magDisp;
                avgDir += currDir*invArrSize;

                long currTime = currData.eventTime;
                long lastTime = prevData.eventTime;
                if (currTime != lastTime) {
                    double invTimeStep = 1 / ((currTime - lastTime) * .001);
                    float currVel = (float) (magDisp * invTimeStep);
                    avgVel += currVel * invArrSize;
                }
                float[] posAlongLine = addVec(scaleVec(normDiffVec, dotVec(subVec(currPos,startPos), normDiffVec)), startPos);

                float currDeviation = magVec(subVec(currPos, posAlongLine));
                if (currDeviation > largestDeviationFromEtoELine)
                    largestDeviationFromEtoELine = currDeviation;
            }
            ratiodirectEtoEDistandlengthOfTrajectory = directEtoEDist/lengthOfTrajectory;

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    protected AnalyticDataFeatureSet(Parcel in) {
        userId = in.readString();
        midStrokeArea = in.readFloat();
        midStrokePressure = in.readFloat();
        avgVel = in.readFloat();
        directEtoEDist = in.readFloat();
        lengthOfTrajectory = in.readFloat();
        ratiodirectEtoEDistandlengthOfTrajectory = in.readFloat();
        largestDeviationFromEtoELine = in.readFloat();
        medianVelocityAtLast3pts = in.readFloat();
        medianAccelAtFirst5Points = in.readFloat();
        vel20per = in.readFloat();
        vel50per = in.readFloat();
        vel80per = in.readFloat();
        accel20per = in.readFloat();
        accel50per = in.readFloat();
        accel80per = in.readFloat();
        deviation20PercFromEtoELine = in.readFloat();
        deviation50PercFromEtoELine = in.readFloat();
        deviation80PercFromEtoELine = in.readFloat();
        dirEtoELine = in.readFloat();
        avgDir = in.readFloat();
        startx = in.readFloat();
        stopx = in.readFloat();
        starty = in.readFloat();
        stopy = in.readFloat();
        strokeDuration = in.readLong();
        phoneOrientation = in.readInt();
        udlrFlag = in.readInt();
        u = in.createFloatArray();
        d = in.createFloatArray();
        l = in.createFloatArray();
        r = in.createFloatArray();
    }

    public static final Creator<AnalyticDataFeatureSet> CREATOR = new Creator<AnalyticDataFeatureSet>() {
        @Override
        public AnalyticDataFeatureSet createFromParcel(Parcel in) {
            return new AnalyticDataFeatureSet(in);
        }

        @Override
        public AnalyticDataFeatureSet[] newArray(int size) {
            return new AnalyticDataFeatureSet[size];
        }
    };

    public String toDebugString(){
        String res = "";
        res += "userId: " + userId + "\n";
        res += "midStrokeArea: " + midStrokeArea + "\n";
        res += "midStrokePressure: " + midStrokePressure + "\n";
        res += "avgVel: " + avgVel + "\n";
        res += "directEtoEDist: " + directEtoEDist + "\n";
        res += "lengthOfTrajectory: " + lengthOfTrajectory + "\n";
        res += "ratiodirectEtoEDistandlengthOfTrajectory: " + ratiodirectEtoEDistandlengthOfTrajectory + "\n";
        res += "largestDeviationFromEtoELine: " + largestDeviationFromEtoELine + "\n";
        //res += "meanResultantLength: " + meanResultantLength + "\n";
        res += "medianVelocityAtLast3pts: " + medianVelocityAtLast3pts + "\n";
        res += "medianAccelAtFirst5Points: " + medianAccelAtFirst5Points + "\n";
        res += "vel20per: " + vel20per + "\n";
        res += "vel50per: " + vel50per + "\n";
        res += "vel80per: " + vel80per + "\n";
        res += "accel20per: " + accel20per + "\n";
        res += "accel50per: " + accel50per + "\n";
        res += "accel80per: " + accel80per + "\n";
        res += "deviation20PercFromEtoELine: " + deviation20PercFromEtoELine + "\n";
        res += "deviation50PercFromEtoELine: " + deviation50PercFromEtoELine + "\n";
        res += "deviation80PercFromEtoELine: " + deviation80PercFromEtoELine + "\n";

        res+= "dirEtoELine: " + dirEtoELine + "\n";
        res+= "avgDir: " + avgDir +"\n";

        res += "startx: " + startx + "\n";
        res += "stopx: " + stopx + "\n";
        res += "starty: " + starty + "\n";
        res += "stopy: " + stopy + "\n";

        res+= "strokeDuration: " + strokeDuration + "\n";

        res += "phoneOrientation: " + phoneOrientation + "\n";
        res += "udlrFlag: " + udlrFlag;

        return res;
    }

    @Override
    public String toString(){
        String res = "";
        res += userId + ",";
        res += midStrokeArea +",";
        res += midStrokePressure +",";
        res += avgVel +",";
        res += directEtoEDist +",";
        res += lengthOfTrajectory +",";
        res += ratiodirectEtoEDistandlengthOfTrajectory +",";
        res += largestDeviationFromEtoELine +',';
        //res += Float.toString(meanResultantLength) +",;
        res += medianVelocityAtLast3pts +",";
        res += medianAccelAtFirst5Points +",";
        res += vel20per +",";
        res += vel50per +",";
        res += vel80per +",";
        res += accel20per +",";
        res += accel50per +",";
        res += accel80per +",";
        res += deviation20PercFromEtoELine +",";
        res += deviation50PercFromEtoELine +",";
        res += deviation80PercFromEtoELine +",";

        res += dirEtoELine + ",";
        res += avgDir + ",";

        res += startx + ",";
        res += stopx + ",";
        res += starty + ",";
        res += stopy + ",";

        res += strokeDuration + ",";

        res += phoneOrientation +",";
        res += udlrFlag;

        return res;
    }

    public AnalyticDataFeatureSet add(AnalyticDataFeatureSet sec){
        AnalyticDataFeatureSet newFeatureSet;
        String addedID;
        if (this.userId != sec.userId)
            addedID = userId + "+" + sec.userId;
        else
            addedID = userId;


            /*userId,midStrokeArea,midStrokePressure,avgVel,directEtoEDist,lengthOfTrajectory,
            ratiodirectEtoEDistandlengthOfTrajectory,largestDeviationFromEtoELine,medianVelocityAtLast3pts,
            medianAccelAtFirst5Points,vel20per,vel50per,vel80per,accel20per,accel50per,accel80per,
            deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine,
            dirEtoELine,avgDir,startx,stopx,starty,stopy,strokeDuration,phoneOrientation,udlrFlag*/
            newFeatureSet = new AnalyticDataFeatureSet(addedID, midStrokeArea + sec.midStrokeArea,
                    midStrokePressure + sec.midStrokePressure, avgVel + sec.avgVel, directEtoEDist + sec.directEtoEDist,
                    lengthOfTrajectory + sec.lengthOfTrajectory,
                    ratiodirectEtoEDistandlengthOfTrajectory + sec.ratiodirectEtoEDistandlengthOfTrajectory,
                    largestDeviationFromEtoELine + sec.largestDeviationFromEtoELine, medianVelocityAtLast3pts + sec.medianVelocityAtLast3pts,
                    medianAccelAtFirst5Points + sec.medianAccelAtFirst5Points, vel20per + sec.vel20per,
                    vel50per + sec.vel50per, vel80per + sec.vel80per, accel20per + sec.accel20per, accel50per + sec.accel50per,
                    accel80per + sec.accel80per, deviation20PercFromEtoELine + sec.deviation20PercFromEtoELine,
                    deviation50PercFromEtoELine + sec.deviation50PercFromEtoELine, deviation80PercFromEtoELine + sec.deviation80PercFromEtoELine,
                    dirEtoELine + sec.dirEtoELine, avgDir + sec.avgDir, startx + sec.startx,
                    stopx + sec.stopx, starty + sec.starty, stopy + sec.stopy, strokeDuration + sec.strokeDuration,
                    phoneOrientation + sec.phoneOrientation, udlrFlag + sec.udlrFlag);

            return newFeatureSet;
        //}
    }

    public AnalyticDataFeatureSet subForkNN(AnalyticDataFeatureSet sec){
        AnalyticDataFeatureSet newFeatureSet;
            /*userId,midStrokeArea,midStrokePressure,avgVel,directEtoEDist,lengthOfTrajectory,
            ratiodirectEtoEDistandlengthOfTrajectory,largestDeviationFromEtoELine,medianVelocityAtLast3pts,
            medianAccelAtFirst5Points,vel20per,vel50per,vel80per,accel20per,accel50per,accel80per,
            deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine,
            dirEtoELine,avgDir,startx,stopx,starty,stopy,strokeDuration,phoneOrientation,udlrFlag*/
        newFeatureSet = new AnalyticDataFeatureSet( "", midStrokeArea - sec.midStrokeArea,
                midStrokePressure - sec.midStrokePressure, avgVel - sec.avgVel, directEtoEDist - sec.directEtoEDist,
                lengthOfTrajectory - sec.lengthOfTrajectory,
                ratiodirectEtoEDistandlengthOfTrajectory - sec.ratiodirectEtoEDistandlengthOfTrajectory,
                largestDeviationFromEtoELine - sec.largestDeviationFromEtoELine, medianVelocityAtLast3pts - sec.medianVelocityAtLast3pts,
                medianAccelAtFirst5Points - sec.medianAccelAtFirst5Points, vel20per - sec.vel20per,
                vel50per - sec.vel50per, vel80per - sec.vel80per, accel20per - sec.accel20per, accel50per - sec.accel50per,
                accel80per - sec.accel80per, deviation20PercFromEtoELine - sec.deviation20PercFromEtoELine,
                deviation50PercFromEtoELine - sec.deviation50PercFromEtoELine, deviation80PercFromEtoELine - sec.deviation80PercFromEtoELine,
                dirEtoELine - sec.dirEtoELine,avgDir - sec.avgDir, startx - sec.startx,
                stopx - sec.stopx, starty - sec.starty, stopy - sec.stopy, strokeDuration - sec.strokeDuration,
                phoneOrientation - sec.phoneOrientation, udlrFlag - sec.udlrFlag);
        return newFeatureSet;
    }

    public AnalyticDataFeatureSet scale(float scaler){
        AnalyticDataFeatureSet newFeatureSet;
        newFeatureSet = new AnalyticDataFeatureSet(userId, midStrokeArea*scaler,
                midStrokePressure*scaler, avgVel *scaler, directEtoEDist*scaler,
                lengthOfTrajectory*scaler, ratiodirectEtoEDistandlengthOfTrajectory*scaler,
                largestDeviationFromEtoELine*scaler, medianVelocityAtLast3pts*scaler,
                medianAccelAtFirst5Points*scaler, vel20per*scaler,
                vel50per*scaler, vel80per*scaler, accel20per*scaler, accel50per*scaler,
                accel80per*scaler, deviation20PercFromEtoELine *scaler,
                deviation50PercFromEtoELine *scaler, deviation80PercFromEtoELine *scaler,
                dirEtoELine*scaler, avgDir*scaler, startx*scaler,
                stopx*scaler, starty*scaler, stopy*scaler, (int)(strokeDuration*scaler),
                (int)(phoneOrientation*scaler), (int)(udlrFlag*scaler));
        return newFeatureSet;
    }

    private float dotVec(float[] a, float[] b)         { return a[0]*b[0] + a[1]*b[1]; }
    private float[] addVec(float[] a, float[] b)       { return new float[]{a[0] + b[0], a[1] + b[1]}; }
    private float[] subVec(float[] a, float[] b)       { return new float[]{a[0] - b[0], a[1] - b[1]}; }
    private float[] scaleVec(float[] pos, float scale)  { return new float[]{pos[0] * scale, pos[1] * scale}; }
    private float magVec(float[] v)                    { return (float)Math.sqrt(v[0]*v[0]+v[1]*v[1]); }
    private float[] normVec(float[] v) {
        float invmag = 1/magVec(v);
        return new float[]{v[0]*invmag, v[1]*invmag};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeFloat(midStrokeArea);
        parcel.writeFloat(midStrokePressure);
        parcel.writeFloat(avgVel);
        parcel.writeFloat(directEtoEDist);
        parcel.writeFloat(lengthOfTrajectory);
        parcel.writeFloat(ratiodirectEtoEDistandlengthOfTrajectory);
        parcel.writeFloat(largestDeviationFromEtoELine);
        parcel.writeFloat(medianVelocityAtLast3pts);
        parcel.writeFloat(medianAccelAtFirst5Points);
        parcel.writeFloat(vel20per);
        parcel.writeFloat(vel50per);
        parcel.writeFloat(vel80per);
        parcel.writeFloat(accel20per);
        parcel.writeFloat(accel50per);
        parcel.writeFloat(accel80per);
        parcel.writeFloat(deviation20PercFromEtoELine);
        parcel.writeFloat(deviation50PercFromEtoELine);
        parcel.writeFloat(deviation80PercFromEtoELine);
        parcel.writeFloat(dirEtoELine);
        parcel.writeFloat(avgDir);
        parcel.writeFloat(startx);
        parcel.writeFloat(stopx);
        parcel.writeFloat(starty);
        parcel.writeFloat(stopy);
        parcel.writeLong(strokeDuration);
        parcel.writeInt(phoneOrientation);
        parcel.writeInt(udlrFlag);
        parcel.writeFloatArray(u);
        parcel.writeFloatArray(d);
        parcel.writeFloatArray(l);
        parcel.writeFloatArray(r);
    }
}