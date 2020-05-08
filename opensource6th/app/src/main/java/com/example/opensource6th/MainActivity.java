package com.example.opensource6th;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void onButton1Click(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://qicqock.github.io/"));
        startActivity(i);
    }
}



