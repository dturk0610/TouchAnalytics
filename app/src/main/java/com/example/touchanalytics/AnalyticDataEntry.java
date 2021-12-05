package com.example.touchanalytics;

import android.app.Activity;
import android.view.MotionEvent;

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

    /* [userId,eventTime,action,phoneOrientation,xCoord,yCoord,pressure,size] */

    public String userId;

    //Get the below values using the MotionEvent.obtain method
    public long eventTime;
    public int action;
    public int phoneOrientation;
    public float xCoord;
    public float yCoord;
    public float pressure;
    public float size; //the area of which the finger takes up on the screen

    public AnalyticDataEntry(String userId, long eventTime, int action, int phoneOrientation, float xCoord, float yCoord, float pressure, float size) {
        this.userId = userId;
        this.eventTime = eventTime;
        this.action = action;
        this.phoneOrientation = phoneOrientation;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.pressure = pressure;
        this.size = size;
    }

    public AnalyticDataEntry(String userId, long eventTime, int action, Activity activity, float xCoord, float yCoord, float pressure, float size) {
        this.userId = userId;
        this.eventTime = eventTime;
        this.action = action;
        this.phoneOrientation = activity.getRequestedOrientation();
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.pressure = pressure;
        this.size = size;
    }

    public AnalyticDataEntry(String userId, Activity activity, MotionEvent moEv) {
        this.userId = userId;
        this.eventTime = moEv.getEventTime();
        this.action = moEv.getAction();
        this.phoneOrientation = activity.getRequestedOrientation();
        this.xCoord = moEv.getX();
        this.yCoord = moEv.getY();
        this.pressure = moEv.getPressure();
        this.size = moEv.getSize();
    }

    @Override
    public String toString() {
        String ret = "";
        ret += userId + ",";
        ret += eventTime + ",";
        ret += action + ",";
        ret += phoneOrientation + ",";
        ret += xCoord + ",";
        ret += yCoord + ",";
        ret += pressure + ",";
        ret += size;
        return ret;
    }
}