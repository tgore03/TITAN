package com.example.titan.titan1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ConfigurationActivity extends AppCompatActivity {

    EditText wifi_ssid_edittext, wifi_password_edittext;
    CheckBox show_password_checkbox;
    Button save_button;
    String wifi_ssid, wifi_password, esp_hotspot_ssid, esp_hotspot_password, android_hotspot_ssid, android_hotspot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifi_ssid_edittext = (EditText)findViewById(R.id.wifi_ssid_edittext);
        wifi_password_edittext = (EditText)findViewById(R.id.wifi_password_edittext);
        show_password_checkbox = (CheckBox)findViewById(R.id.show_password_checkbox);
        save_button = (Button)findViewById(R.id.save_config_button);

        esp_hotspot_ssid = "";
        esp_hotspot_password = "";

        android_hotspot_ssid = "pancham1";
        android_hotspot_password = "9820900119";

    }

    @Override
    protected void onStart() {
        super.onStart();


        show_password_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show_password_checkbox.isChecked()) {
                    wifi_password_edittext.setTransformationMethod(null);
                } else {
                    wifi_password_edittext.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi_ssid = wifi_ssid_edittext.getText().toString();
                Log.d("Titan1","Titan1 - wifi_ssid = " + wifi_ssid);
                wifi_password = wifi_password_edittext.getText().toString();
                Log.d("Titan1","Titan1 - wifi_password = " + wifi_password);
            }
        });


    }
}
