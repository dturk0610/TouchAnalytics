package com.example.touchanalytics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.*;
import java.lang.Math;


public class ImageSelect extends AppCompatActivity {

    private static int numOfImagesEach = 21;

    public static Drawable RandomImage(Context context) {

        char animalChar;
        int index = (int)(Math.random()*numOfImagesEach) + 1;
        int animalInt = (int) Math.round(Math.random());
        //String animalStr;

        if (animalInt == 0)
        {
            animalChar = 'c';
            //animalStr = "cats";
        }
        else
        {
            animalChar = 'd';
            //animalStr = "dogs";
        }

        String pathName = animalChar + "" + index;
        Log.d("", pathName);
        Resources resources = context.getResources();
        int id = resources.getIdentifier(pathName,"drawable", context.getPackageName());
        return resources.getDrawable(id, context.getTheme());

    }
}
