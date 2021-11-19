package com.example.touchanalytics;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.*;
import java.lang.Math;


public class ImageSelect extends AppCompatActivity {

    private char animalChar;

    public static int numOfImagesEach = 21;

    public Drawable RandomImage() {

        int index = ((int) Math.round(Math.random()) ) * (numOfImagesEach-1) + 1;
        int animalInt = (int) Math.round(Math.random());
        String animalStr = "";

        if (animalInt == 0)
        {
            animalChar = 'c';
            animalStr = "cats";
        }
        else
        {
            animalChar = 'd';
            animalStr = "dogs";
        }


        String pathName= "res/drawables/dogs/d1.jpg";

        pathName= "res/drawables/" + animalStr + '/' + animalChar + index + ".jpg";



        Drawable image  = Drawable.createFromPath(pathName);


        return image;

    }
}
