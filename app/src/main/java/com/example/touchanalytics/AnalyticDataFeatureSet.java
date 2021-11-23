package com.example.touchanalytics;

import android.util.Log;

import com.example.touchanalytics.AnalyticDataEntry;

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
    float medianVelocityAtLast3pts, medianAccelAtFirst5Points;      /*done*/

    float vel20per,vel50per,vel80per;                               /*done*/
    float accel20per, accel50per, accel80per;                       /*done*/
    float deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine; /*done*/

    float[] dirEtoELine;                                            /*done*/
    float[] avgDir;                                                 /*done*/
    float startx, stopx, starty, stopy;                             /*done*/
    long strokeDuration;                                            /*[ms] done*/
    int phoneOrientation;                                           /*done*/
    int udlrFlag; //up, down, left, right flag 0 - 3 respectively   /*done*/

    //float meanResultantLength;
    //long interStrokeTime; wont be used?
    //change of finger orientation ?? how is this even calculated
    //mid-stroke finger orientation ?? how is this one even gotten

    float[] u = {0, -1}, d = {0, 1}, l = {-1, 0}, r = {1, 0};

    public AnalyticDataFeatureSet (long userId, float midStrokeArea, float midStrokePressure,
            float avgVel, float directEtoEDist, float lengthOfTrajectory, float ratiodirectEtoEDistandlengthOfTrajectory,
            float largestDeviationFromEtoELine, float medianVelocityAtLast3pts, float medianAccelAtFirst5Points,
            float vel20per, float vel50per, float vel80per, float accel20per, float accel50per, float accel80per,
            float deviation20PercFromEtoELine, float deviation50PercFromEtoELine, float deviation80PercFromEtoELine,
            float[] dirEtoELine, float[] avgDir, float startx, float stopx, float starty, float stopy,
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
            starty = swipe[0].xCoord;
            stopy = swipe[arrSize - 1].yCoord;
            float[] startPos = {startx, starty};
            float[] stopPos = {stopx, stopy};

            directEtoEDist = magVec(subVec(stopPos, startPos));
            dirEtoELine = scaleVec(subVec(stopPos, startPos), 1/directEtoEDist);
            float uDotDirEtoE = dotVec(dirEtoELine, u), dDotDirEtoE = dotVec(dirEtoELine, d);
            float lDotDirEtoE = dotVec(dirEtoELine, l), rDotDirEtoE = dotVec(dirEtoELine, r);
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
            accel20per = (float)(vel20per*invPer20TimeDiff);
            accel50per = (float)(vel50per*invPer50TimeDiff);
            accel80per = (float)(vel80per*invPer80TimeDiff);

            float[] posAlongLineAt20 = addVec(scaleVec(dirEtoELine, dotVec(subVec(pos20,startPos), dirEtoELine)), startPos);
            float[] posAlongLineAt50 = addVec(scaleVec(dirEtoELine, dotVec(subVec(pos50,startPos), dirEtoELine)), startPos);
            float[] posAlongLineAt80 = addVec(scaleVec(dirEtoELine, dotVec(subVec(pos80,startPos), dirEtoELine)), startPos);

            deviation20PercFromEtoELine = magVec(subVec(pos20, posAlongLineAt20));
            deviation50PercFromEtoELine = magVec(subVec(pos50, posAlongLineAt50));
            deviation80PercFromEtoELine = magVec(subVec(pos80, posAlongLineAt80));

            avgVel = 0;
            lengthOfTrajectory = 0;
            avgDir = new float[]{0, 0};
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
            int lastInd = arrSize - 2 - 0;
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
                float[] currDir;
                if (magDisp != 0)
                    currDir = scaleVec(displacement, 1f/magDisp);
                else
                    currDir = new float[] {0, 0};
                lengthOfTrajectory +=magDisp;
                avgDir = addVec(avgDir, currDir);

                long currTime = currData.eventTime;
                long lastTime = prevData.eventTime;
                double invTimeStep = 1/((currTime - lastTime)*.001);

                float currVel = (float)(magDisp*invTimeStep);
                avgVel += currVel*invArrSize;

                float[] posAlongLine = addVec(scaleVec(dirEtoELine, dotVec(subVec(currPos,startPos), dirEtoELine)), startPos);

                float currDeviation = magVec(subVec(currPos, posAlongLine));
                if (currDeviation > largestDeviationFromEtoELine)
                    largestDeviationFromEtoELine = currDeviation;
            }
            avgDir = scaleVec(avgDir, 1/magVec(avgDir));
            ratiodirectEtoEDistandlengthOfTrajectory = directEtoEDist/lengthOfTrajectory;

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String toDebugString(){
        String res = "";
        res += "userId: " + Long.toString(userId) + "\n";
        res += "midStrokeArea: " + Float.toString(midStrokeArea) + "\n";
        res += "midStrokePressure: " + Float.toString(midStrokePressure) + "\n";
        res += "avgVel: " + Float.toString(avgVel) + "\n";
        res += "directEtoEDist: " + Float.toString(directEtoEDist) + "\n";
        res += "lengthOfTrajectory: " + Float.toString(lengthOfTrajectory) + "\n";
        res += "ratiodirectEtoEDistandlengthOfTrajectory: " + Float.toString(ratiodirectEtoEDistandlengthOfTrajectory) + "\n";
        res += "largestDeviationFromEtoELine: " + Float.toString(largestDeviationFromEtoELine) + "\n";
        //res += "meanResultantLength: " + Float.toString(meanResultantLength) + "\n";
        res += "medianVelocityAtLast3pts: " + Float.toString(medianVelocityAtLast3pts) + "\n";
        res += "medianAccelAtFirst5Points: " + Float.toString(medianAccelAtFirst5Points) + "\n";
        res += "vel20per: " + Float.toString(vel20per) + "\n";
        res += "vel50per: " + Float.toString(vel50per) + "\n";
        res += "vel80per: " + Float.toString(vel80per) + "\n";
        res += "accel20per: " + Float.toString(accel20per) + "\n";
        res += "accel50per: " + Float.toString(accel50per) + "\n";
        res += "accel80per: " + Float.toString(accel80per) + "\n";
        res += "deviation20PercFromEtoELine: " + Float.toString(deviation20PercFromEtoELine) + "\n";
        res += "deviation50PercFromEtoELine: " + Float.toString(deviation50PercFromEtoELine) + "\n";
        res += "deviation80PercFromEtoELine: " + Float.toString(deviation80PercFromEtoELine) + "\n";

        res+= "dirEtoELine: [" + Float.toString(dirEtoELine[0]) + ", " + Float.toString(dirEtoELine[1]) + "]\n";
        res+= "avgDir: [" + Float.toString(avgDir[0]) + ", " + Float.toString(avgDir[1]) + "]\n";

        res += "startx: " + Float.toString(startx) + "\n";
        res += "stopx: " + Float.toString(stopx) + "\n";
        res += "starty: " + Float.toString(starty) + "\n";
        res += "stopy: " + Float.toString(stopy) + "\n";

        res+= "strokeDuration: " + Long.toString(strokeDuration) + "\n";

        res += "phoneOrientation: " + Integer.toString(phoneOrientation) + "\n";
        res += "udlrFlag: " + Integer.toString(udlrFlag);

        return res;
    }

    public String toString(){
        String res = "";
        res += Long.toString(userId) +',';
        res += Float.toString(midStrokeArea) +',';
        res += Float.toString(midStrokePressure) +',';
        res += Float.toString(avgVel) +',';
        res += Float.toString(directEtoEDist) +',';
        res += Float.toString(lengthOfTrajectory) +',';
        res += Float.toString(ratiodirectEtoEDistandlengthOfTrajectory) +',';
        res += Float.toString(largestDeviationFromEtoELine) +',';
        //res += Float.toString(meanResultantLength) +',';
        res += Float.toString(medianVelocityAtLast3pts) +',';
        res += Float.toString(medianAccelAtFirst5Points) +',';
        res += Float.toString(vel20per) +',';
        res += Float.toString(vel50per) +',';
        res += Float.toString(vel80per) +',';
        res += Float.toString(accel20per) +',';
        res += Float.toString(accel50per) +',';
        res += Float.toString(accel80per) +',';
        res += Float.toString(deviation20PercFromEtoELine) +',';
        res += Float.toString(deviation50PercFromEtoELine) +',';
        res += Float.toString(deviation80PercFromEtoELine) +',';

        res += Float.toString(dirEtoELine[0]) + " " + Float.toString(dirEtoELine[1]) + ',';
        res += Float.toString(avgDir[0]) + " " + Float.toString(avgDir[1]) + ',';

        res += Float.toString(startx) +',';
        res += Float.toString(stopx) +',';
        res += Float.toString(starty) +',';
        res += Float.toString(stopy) +',';

        res += Long.toString(strokeDuration) +',';

        res += Integer.toString(phoneOrientation) +',';
        res += Integer.toString(udlrFlag);

        return res;
    }

    public AnalyticDataFeatureSet add(AnalyticDataFeatureSet sec){
        AnalyticDataFeatureSet newFeatureSet;
        if (this.userId != sec.userId){
            return null;
        }else {
            /*userId,midStrokeArea,midStrokePressure,avgVel,directEtoEDist,lengthOfTrajectory,
            ratiodirectEtoEDistandlengthOfTrajectory,largestDeviationFromEtoELine,medianVelocityAtLast3pts,
            medianAccelAtFirst5Points,vel20per,vel50per,vel80per,accel20per,accel50per,accel80per,
            deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine,
            dirEtoELine,avgDir,startx,stopx,starty,stopy,strokeDuration,phoneOrientation,udlrFlag*/
            newFeatureSet = new AnalyticDataFeatureSet(userId, midStrokeArea + sec.midStrokeArea,
                    midStrokePressure + sec.midStrokePressure, avgVel + sec.avgVel, directEtoEDist + sec.directEtoEDist,
                    lengthOfTrajectory + sec.lengthOfTrajectory,
                    ratiodirectEtoEDistandlengthOfTrajectory + sec.ratiodirectEtoEDistandlengthOfTrajectory,
                    largestDeviationFromEtoELine + sec.largestDeviationFromEtoELine, medianVelocityAtLast3pts + sec.medianVelocityAtLast3pts,
                    medianAccelAtFirst5Points + sec.medianAccelAtFirst5Points, vel20per + sec.vel20per,
                    vel50per + sec.vel50per, vel80per + sec.vel80per, accel20per + sec.accel20per, accel50per + sec.accel50per,
                    accel80per + sec.accel80per, deviation20PercFromEtoELine + sec.deviation20PercFromEtoELine,
                    deviation50PercFromEtoELine + sec.deviation50PercFromEtoELine, deviation80PercFromEtoELine + sec.deviation80PercFromEtoELine,
                    addVec(dirEtoELine, sec.dirEtoELine), addVec(avgDir, sec.avgDir), startx + sec.startx,
                    stopx + sec.stopx, starty + sec.starty, stopy + sec.stopy, strokeDuration + sec.strokeDuration,
                    phoneOrientation + sec.phoneOrientation, udlrFlag + sec.udlrFlag);

            return newFeatureSet;
        }
    }

    public AnalyticDataFeatureSet subForkNN(AnalyticDataFeatureSet sec){
        AnalyticDataFeatureSet newFeatureSet;
            /*userId,midStrokeArea,midStrokePressure,avgVel,directEtoEDist,lengthOfTrajectory,
            ratiodirectEtoEDistandlengthOfTrajectory,largestDeviationFromEtoELine,medianVelocityAtLast3pts,
            medianAccelAtFirst5Points,vel20per,vel50per,vel80per,accel20per,accel50per,accel80per,
            deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine,
            dirEtoELine,avgDir,startx,stopx,starty,stopy,strokeDuration,phoneOrientation,udlrFlag*/
        newFeatureSet = new AnalyticDataFeatureSet(-1, midStrokeArea - sec.midStrokeArea,
                midStrokePressure - sec.midStrokePressure, avgVel - sec.avgVel, directEtoEDist - sec.directEtoEDist,
                lengthOfTrajectory - sec.lengthOfTrajectory,
                ratiodirectEtoEDistandlengthOfTrajectory - sec.ratiodirectEtoEDistandlengthOfTrajectory,
                largestDeviationFromEtoELine - sec.largestDeviationFromEtoELine, medianVelocityAtLast3pts - sec.medianVelocityAtLast3pts,
                medianAccelAtFirst5Points - sec.medianAccelAtFirst5Points, vel20per - sec.vel20per,
                vel50per - sec.vel50per, vel80per - sec.vel80per, accel20per - sec.accel20per, accel50per - sec.accel50per,
                accel80per - sec.accel80per, deviation20PercFromEtoELine - sec.deviation20PercFromEtoELine,
                deviation50PercFromEtoELine - sec.deviation50PercFromEtoELine, deviation80PercFromEtoELine - sec.deviation80PercFromEtoELine,
                subVec(dirEtoELine, sec.dirEtoELine), subVec(avgDir, sec.avgDir), startx - sec.startx,
                stopx - sec.stopx, starty - sec.starty, stopy - sec.stopy, strokeDuration - sec.strokeDuration,
                phoneOrientation - sec.phoneOrientation, udlrFlag - sec.udlrFlag);
        return newFeatureSet;
    }

    public AnalyticDataFeatureSet scale(float scaler){
        AnalyticDataFeatureSet newFeatureSet;
        newFeatureSet = new AnalyticDataFeatureSet(userId, midStrokeArea*scaler,
                midStrokePressure*scaler, avgVel*scaler, directEtoEDist *scaler,
                lengthOfTrajectory *scaler, ratiodirectEtoEDistandlengthOfTrajectory*scaler,
                largestDeviationFromEtoELine *scaler, medianVelocityAtLast3pts *scaler,
                medianAccelAtFirst5Points *scaler, vel20per *scaler,
                vel50per *scaler, vel80per *scaler, accel20per *scaler, accel50per *scaler,
                accel80per *scaler, deviation20PercFromEtoELine *scaler,
                deviation50PercFromEtoELine *scaler, deviation80PercFromEtoELine *scaler,
                scaleVec(dirEtoELine, 1/magVec(dirEtoELine)), scaleVec(avgDir, 1/magVec(avgDir)), startx *scaler,
                stopx *scaler, starty *scaler, stopy *scaler, (int)(strokeDuration*scaler),
                (int)(phoneOrientation*scaler), (int)(udlrFlag*scaler));
        return newFeatureSet;
    }

    private float dotVec(float[] a, float[] b)         { return a[0]*b[0] + a[1]*b[1]; }
    private float[] addVec(float[] a, float[] b)       { return new float[]{a[0] + b[0], a[1] + b[1]}; }
    private float[] subVec(float[] a, float[] b)       { return new float[]{a[0] - b[0], a[1] - b[1]}; }
    private float[] scaleVec(float[] pos, float scale)  { return new float[]{pos[0] * scale, pos[1] * scale}; }
    private float magVec(float[] v)                    { return (float)Math.sqrt(v[0]*v[0]+v[1]*v[1]); }
}