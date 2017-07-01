package com.example.titan.titan1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    private TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        text2 = (TextView) findViewById(R.id.text2);
        text2.setText("STEPS:\n" +
                "1)First sign up from login page that opens up when you install the application for the first time.\n" +
                "2)After signing up,go to Configuration Activity and configure your sticker to connect it to your android phone .Now you will be able to control your phone's music player with the sticker\n" +
                "3)Go to Mapping Activity in order to be able to control home appliances. Map the appliances you want to control and the corresponding buttons. \n" +
                "4)Once the mapping is successful, you will be able to control the devices from the sticker.\n" +
                "5)You will be able to see the current status of the devices on the home page .In order to get the real time status , just hit the refresh button.");

    }

}
