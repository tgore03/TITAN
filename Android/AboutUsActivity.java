package com.example.titan.titan1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {
    private TextView text2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        text2 = (TextView) findViewById(R.id.text2);
        text2.setText("We are a bunch of techsavvy engineering students working on our final year project . Hope you like our system-TITAN!");

    }

}
