package com.example.touchanalytics;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.*;
import java.lang.Math;


public class ImageSelect extends AppCompatActivity {

    private static int numOfImagesEach = 21;

    public Drawable RandomImage() {

        char animalChar;
        int index = ((int) Math.round(Math.random()) ) * (numOfImagesEach-1) + 1;
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

        String pathName= "res/drawables/" + animalChar + index + ".jpg";

        return Drawable.createFromPath(pathName);

    }
}
