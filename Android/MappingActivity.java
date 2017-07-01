package com.example.titan.titan1;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MappingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    Spinner sticker_spinner, button_spinner, thing_spinner, function_spinner;
    List<String> sticker_list, button_list, thing_list, function_list;
    ArrayAdapter<String> sticker_adapter, button_adapter, thing_adapter, function_adapter;


    Button http_connect_button;

    String uri = "http://titan1994.96.lt/mapping.php?cid="+client_id;
    HttpURLConnection httpURLConnection;
    BufferedReader bufferedReader;
    URL url;

    StringBuilder stringBuilder;
    String json_message, number_of_button;
    private static String client_id = "0001";


    JSONArray json_array_for_sticker,json_array_for_button, json_array_for_thing, json_array_for_function;
    JSONObject json_object_for_sticker, json_object_for_thing, json_object_for_function, jsonRootObject;

    String [][] sticker_info = new String[100][3];
    String [][] thing_info = new String[100][2];
    String [][] function_info = new String[100][2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Titan1", "Titan1 - onCreate started");


        sticker_list = new ArrayList<String>();
        button_list = new ArrayList<String>();
        thing_list = new ArrayList<String>();
        function_list = new ArrayList<String>();

        Log.d("Titan1", "Titan1 - Lists initialized");


        // initialize for sticker spinner
        sticker_spinner = (Spinner)findViewById(R.id.sticker_spinner);
        sticker_spinner.setOnItemSelectedListener(this);

        //sticker_list.add("home sticker");

        Log.d("Titan1", "Titan1 - sticker spinner initialized");

        // initialize for button spinner
        button_spinner = (Spinner)findViewById(R.id.button_spinner);
        button_spinner.setOnItemSelectedListener(this);


        Log.d("Titan1", "Titan1 - button spinner initialized");

        // initialize for thing spinner
        thing_spinner = (Spinner)findViewById(R.id.thing_spinner);
        thing_spinner.setOnItemSelectedListener(this);


        Log.d("Titan1", "Titan1 - thing spinner initialized");

        // initialize for function
        function_spinner = (Spinner)findViewById(R.id.function_spinner);
        function_spinner.setOnItemSelectedListener(this);


        Log.d("Titan1", "Titan1 - function spinner initialized");

        connect_to_database(uri);

        Log.d("Titan1", "Titan1 - database data retrieval finished");

        http_connect_button = (Button)findViewById(R.id.http_connect_button);
        http_connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // connect_to_database(uri);

                Log.d("Titan1", "Titan1 - onCreate end");

            }
        });



        /*
        // create listener for all the spinners


        sticker_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "clicked: " + item, Toast.LENGTH_SHORT).show();
                Log.d("Titan1", "Titan1 - OnClick of sticker_spinner ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.d("Titan1", "Titan1 - onclick of sticker_spinner complete");

        thing_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "clicked: " + item, Toast.LENGTH_SHORT).show();
                Log.d("Titan1", "Titan1 - onItemselected thing_spinner");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "onNothing Selected called", Toast.LENGTH_SHORT).show();
                Log.d("Titan1", "Titan1 - onNothingSelected of thing_spinner");
            }
        });

        Log.d("Titan1", "Titan1 - thing_spinner listener complete ");

        function_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "clicked: " + item, Toast.LENGTH_SHORT).show();
                Log.d("Titan1", "Titan1 - OnItemSelected of function_spinner");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "onNothing Selected called", Toast.LENGTH_SHORT).show();
                Log.d("Titan1", "Titan1 - OnNothingSelected of function_spinner");
            }
        });

        Log.d("Titan1", "Titan1 - function_spinner listener complete");
        */
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Titan1", "Titan1 - onStart started");

        sticker_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sticker_list);
        sticker_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sticker_spinner.setAdapter(sticker_adapter);

        button_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, button_list);
        button_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        button_spinner.setAdapter(button_adapter);

        thing_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, thing_list);
        thing_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thing_spinner.setAdapter(thing_adapter);

        function_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, function_list);
        function_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        function_spinner.setAdapter(function_adapter);


    }




    void connect_to_database(String url_string) {

        class DatabaseConnection extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MappingActivity.this, "Loading..", null, true, true);
            }

            @Override
            protected String doInBackground(String... params) {
                Log.d("Titan1", "Titan1 - doInBackground started");
                //String uri = params[0];
                try {
                    url = new URL(uri);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    stringBuilder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    json_message = bufferedReader.readLine();
                    Log.d("Titan1", "Titan1 - message stored in json_message = " + json_message);


                    try {
                        jsonRootObject = new JSONObject(json_message);

                        // Retrieve Data for sticker
                        json_array_for_sticker = jsonRootObject.optJSONArray("sticker");
                        json_array_for_thing = jsonRootObject.optJSONArray("thing");
                        json_array_for_function = jsonRootObject.optJSONArray("function");


                        Log.d("Titan1","Titan1 - jsonArray created for sticker");
                        for(int i=0; i < json_array_for_sticker.length(); i++){

                            json_object_for_sticker = json_array_for_sticker.getJSONObject(i);

                            sticker_info[i][0]= json_object_for_sticker.optString("device_id").toString();
                            Log.d("Titan1", "Titan1 - device id = " + sticker_info[i][0]);

                            sticker_info[i][1]= json_object_for_sticker.optString("device_name").toString();
                            Log.d("Titan1","Titan1 - device_name = "+ sticker_info[i][1]);

                            sticker_info[i][2] = json_object_for_sticker.optString("button").toString();
                            Log.d("Titan1","Titan1 - number of buttons = " + sticker_info[i][2]);

                            sticker_list.add(sticker_info[i][1]);
                            Log.d("Titan1", "Titan1 - device added to sticker_list");
                        }


                        Log.d("Titan1","Titan1 - json_array_for_function created for function");
                        //Iterate the jsonArray and print the info of JSONObjects
                        for(int i=0; i < json_array_for_function.length(); i++){
                            json_object_for_function = json_array_for_function.getJSONObject(i);
                            Log.d("Titan1", "Titan1 - jsonObject created for individual rows");

                            function_info[i][0]= json_object_for_function.optString("device_id").toString();
                            Log.d("Titan1", "Titan1 - device id = " + function_info[i][0]);

                            function_info[i][1] = json_object_for_function.optString("device_name").toString();
                            Log.d("Titan1","Titan1 - device_name = "+ function_info[i][1]);

                            function_list.add(function_info[i][1]);
                        }


                        Log.d("Titan1","Titan1 - json_array_for_thing created for thing");
                        //Iterate the jsonArray and print the info of JSONObjects
                        for(int i=0; i < json_array_for_thing.length(); i++){
                            json_object_for_thing = json_array_for_thing.getJSONObject(i);
                            Log.d("Titan1", "Titan1 - jsonObject created for individual rows");

                            thing_info[i][0] = json_object_for_thing.optString("device_id").toString();
                            Log.d("Titan1", "Titan1 - device id = " + thing_info[i][0]);

                            thing_info[i][1] = json_object_for_thing.optString("device_name").toString();
                            Log.d("Titan1","Titan1 - device_name = "+ thing_info[i][1]);

                            thing_list.add(thing_info[i][1]);
                        }

                    }catch (JSONException e) {
                        Log.d("Titan1", "Titan1 - Exception e = "+ e);
                    }

                    Log.d("Titan1", "Titan1 - return stringBuilder string");
                    return stringBuilder.toString().trim();

                } catch (Exception e) {
                    Log.d("Titan1", "Titan1 - exception occured : " + e);
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Log.d("Titan1", "Titan1 - onPostExecute started : s = " + s);
                //Toast.makeText(getApplicationContext(), " response = " + s, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        Log.d("Titan1", "Titan1 - connect_to_database started ");
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.execute(url_string);
        Log.d("Titan1", "Titan1 - DatabaseConnection executed");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Titan1", "Titan1 - onItemSelected parent = " + parent.toString() + " position = " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("Titan1", "Titan1 - onNothingSelected called");
    }
}

