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
    private static int numOfTypes = 4;

    public static Drawable RandomImage(Context context) {

        char animalChar;
        int index = (int)(Math.random()*numOfImagesEach) + 1;
        int animalInt = (int) Math.round(Math.random()*numOfTypes);
        //String animalStr;

        if (animalInt == 0)
        {
            animalChar = 'c';
            //animalStr = "cats";
        }
        else if (animalInt == 1)
        {
            animalChar = 'd';
            //animalStr = "dogs";
        }
        else if (animalInt == 2)
        {
            animalChar = 'b';
        }
        else
        {
            animalChar = 'h';
        }




        String pathName = animalChar + "" + index;
        Log.d("", pathName);
        Resources resources = context.getResources();
        int id = resources.getIdentifier(pathName,"drawable", context.getPackageName());


        return resources.getDrawable(id, context.getTheme());

    }
}
