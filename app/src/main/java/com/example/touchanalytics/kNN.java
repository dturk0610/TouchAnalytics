package com.example.touchanalytics;

import android.util.Log;

public class kNN {

    static float mSAWeight = .2058f;
    static float mSPWeight = .1728f;
    static float vel20PerWeight = .1963f;
    static float vel50PerWeight = .0722f;
    static float vel80PerWeight = .2058f;
    static float accel20PerWeight = .0707f;
    static float accel50PerWeight = .0504f;
    static float accel80PerWeight = .0598f;
    static float dev20PerWeight = .0504f;
    static float dev50PerWeight = .0539f;
    static float dev80PerWeight = .0514f;
    static float dirEtoEWeight = .1106f;
    static float startxWeight = .1015f;
    static float startyWeight = .0943f;
    static float stopxWeight = .1032f;
    static float stopyWeight = .0861f;
    static float avgDirWeight = .0945f;
    static float avgVelWeight = .0884f;
    static float strokeDurWeight = .085f;
    static float dirEtoEDistWeight = .0827f;
    static float lnTrajWeight = .0816f;
    static float medVelLast3Weight = .0724f;
    static float medAccelFirst5Weight = .0542f;
    static float ratioWeight = .0629f;
    static float largeDevWeight = .0608f;
    static float phoneOrienWeight = .0344f;
    static float udlrFlagWeight = .0097f;



    public static float dist(AnalyticDataFeatureSet set1, AnalyticDataFeatureSet set2){
        float distance = 0;
            /*midStrokeArea,midStrokePressure,avgVel,directEtoEDist,lengthOfTrajectory,
            ratiodirectEtoEDistandlengthOfTrajectory,largestDeviationFromEtoELine,medianVelocityAtLast3pts,
            medianAccelAtFirst5Points,vel20per,vel50per,vel80per,accel20per,accel50per,accel80per,
            deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine,
            dirEtoELine,avgDir,startx,stopx,starty,stopy,strokeDuration,phoneOrientation,udlrFlag*/
        AnalyticDataFeatureSet set2mset1 = set2.subForkNN(set1);

        distance += set2mset1.midStrokeArea*set2mset1.midStrokeArea;
        distance += set2mset1.midStrokePressure*set2mset1.midStrokePressure;
        distance += set2mset1.avgVel*set2mset1.avgVel;
        distance += set2mset1.directEtoEDist*set2mset1.directEtoEDist;
        distance += set2mset1.lengthOfTrajectory*set2mset1.lengthOfTrajectory;
        distance += set2mset1.ratiodirectEtoEDistandlengthOfTrajectory*set2mset1.ratiodirectEtoEDistandlengthOfTrajectory;
        distance += set2mset1.largestDeviationFromEtoELine*set2mset1.largestDeviationFromEtoELine;
        distance += set2mset1.medianVelocityAtLast3pts*set2mset1.medianVelocityAtLast3pts;
        distance += set2mset1.medianAccelAtFirst5Points*set2mset1.medianAccelAtFirst5Points;
        distance += set2mset1.vel20per*set2mset1.vel20per;
        distance += set2mset1.vel50per*set2mset1.vel50per;
        distance += set2mset1.vel80per*set2mset1.vel80per;
        distance += set2mset1.accel20per*set2mset1.accel20per;
        distance += set2mset1.accel50per*set2mset1.accel50per;
        distance += set2mset1.accel80per*set2mset1.accel80per;
        distance += set2mset1.deviation20PercFromEtoELine*set2mset1.deviation20PercFromEtoELine;
        distance += set2mset1.deviation50PercFromEtoELine*set2mset1.deviation50PercFromEtoELine;
        distance += set2mset1.deviation80PercFromEtoELine*set2mset1.deviation80PercFromEtoELine;
        distance += set2mset1.dirEtoELine[0]*set2mset1.dirEtoELine[0];
        distance += set2mset1.dirEtoELine[1]*set2mset1.dirEtoELine[1];
        distance += set2mset1.avgDir[0]*set2mset1.avgDir[0];
        distance += set2mset1.avgDir[1]*set2mset1.avgDir[1];
        distance += set2mset1.startx*set2mset1.startx;
        distance += set2mset1.stopx*set2mset1.stopx;
        distance += set2mset1.starty*set2mset1.starty;
        distance += set2mset1.stopy*set2mset1.stopy;
        distance += set2mset1.strokeDuration*set2mset1.strokeDuration;
        distance += set2mset1.phoneOrientation*set2mset1.phoneOrientation;
        distance += set2mset1.udlrFlag*set2mset1.udlrFlag;
        return (float)Math.sqrt(distance);
    }

    public static float weightedDist(AnalyticDataFeatureSet set1, AnalyticDataFeatureSet set2){
        double distance = 0;
            /*midStrokeArea,midStrokePressure,avgVel,directEtoEDist,lengthOfTrajectory,
            ratiodirectEtoEDistandlengthOfTrajectory,largestDeviationFromEtoELine,medianVelocityAtLast3pts,
            medianAccelAtFirst5Points,vel20per,vel50per,vel80per,accel20per,accel50per,accel80per,
            deviation20PercFromEtoELine,deviation50PercFromEtoELine,deviation80PercFromEtoELine,
            dirEtoELine,avgDir,startx,stopx,starty,stopy,strokeDuration,phoneOrientation,udlrFlag*/
        AnalyticDataFeatureSet set2mset1 = set2.subForkNN(set1);

        distance += mSAWeight*set2mset1.midStrokeArea*set2mset1.midStrokeArea;
        distance += mSPWeight*set2mset1.midStrokePressure*set2mset1.midStrokePressure;
        distance += avgVelWeight*set2mset1.avgVel*set2mset1.avgVel;
        distance += dirEtoEDistWeight*set2mset1.directEtoEDist*set2mset1.directEtoEDist;
        distance += lnTrajWeight*set2mset1.lengthOfTrajectory*set2mset1.lengthOfTrajectory;
        distance += ratioWeight*set2mset1.ratiodirectEtoEDistandlengthOfTrajectory;
        distance += largeDevWeight*set2mset1.largestDeviationFromEtoELine*set2mset1.largestDeviationFromEtoELine;
        distance += medVelLast3Weight*set2mset1.medianVelocityAtLast3pts*set2mset1.medianVelocityAtLast3pts;
        distance += medAccelFirst5Weight*set2mset1.medianAccelAtFirst5Points*set2mset1.medianAccelAtFirst5Points;
        distance += vel20PerWeight*set2mset1.vel20per*set2mset1.vel20per;
        distance += vel50PerWeight*set2mset1.vel50per*set2mset1.vel50per;
        distance += vel80PerWeight*set2mset1.vel80per*set2mset1.vel80per;
        distance += accel20PerWeight*set2mset1.accel20per*set2mset1.accel20per;
        distance += accel50PerWeight*set2mset1.accel50per*set2mset1.accel50per;
        distance += accel80PerWeight*set2mset1.accel80per*set2mset1.accel80per;
        distance += dev20PerWeight*set2mset1.deviation20PercFromEtoELine*set2mset1.deviation20PercFromEtoELine;
        distance += dev50PerWeight*set2mset1.deviation50PercFromEtoELine*set2mset1.deviation50PercFromEtoELine;
        distance += dev80PerWeight*set2mset1.deviation80PercFromEtoELine*set2mset1.deviation80PercFromEtoELine;
        distance += dirEtoEWeight*set2mset1.dirEtoELine[0]*set2mset1.dirEtoELine[0];
        distance += dirEtoEWeight*set2mset1.dirEtoELine[1]*set2mset1.dirEtoELine[1];
        distance += avgDirWeight*set2mset1.avgDir[0]*set2mset1.avgDir[0];
        distance += avgDirWeight*set2mset1.avgDir[1]*set2mset1.avgDir[1];
        distance += startxWeight*set2mset1.startx*set2mset1.startx;
        distance += stopxWeight*set2mset1.stopx*set2mset1.stopx;
        distance += startyWeight*set2mset1.starty*set2mset1.starty;
        distance += stopyWeight*set2mset1.stopy*set2mset1.stopy;
        distance += strokeDurWeight*set2mset1.strokeDuration*set2mset1.strokeDuration;
        distance += phoneOrienWeight*set2mset1.phoneOrientation*set2mset1.phoneOrientation;
        distance += udlrFlagWeight*set2mset1.udlrFlag*set2mset1.udlrFlag;


        return (float)Math.sqrt(distance);
    }

}
