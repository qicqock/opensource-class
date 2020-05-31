package com.example.weatherforecastapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String data = getIntent().getStringExtra("data");
        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(data);
    }

}
