package com.example.touchanalytics;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestSwipe  extends AppCompatActivity {

    AnalyticDataManager manager;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.manager = bundle.getParcelable("manager");
        setContentView(R.layout.calibration);

        imageView = findViewById(R.id.calibrationImgView);
        imageView.setImageDrawable(ImageSelect.RandomImage(this));
    }
}
