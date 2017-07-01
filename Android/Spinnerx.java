package com.example.titan.titan1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tanmay on 02-03-2016.
 */
public class Spinnerx extends AppCompatActivity {
    private static String client_id = "0001";
    String uri = "http://titan1994.96.lt/mapping.php?cid="+client_id;
    HttpURLConnection httpURLConnection;
    BufferedReader bufferedReader;
    URL url;

    StringBuilder stringBuilder;
    String json_message;



    JSONArray json_array_for_sticker, json_array_for_thing, json_array_for_function;
    JSONObject json_object_for_sticker, json_object_for_thing, json_object_for_function, jsonRootObject;
    public int sticker_length;
    public int function_length;
    public int thing_length;
    public static String [][] sticker_info = new String[100][3];
    public static String [][] thing_info = new String[100][2];
    public static String [][] function_info = new String[100][2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);


    }

    String getStickerInfo(int position) {
        Log.d("TItan1", "Titan1 - spinner x sticker info: " + sticker_info[position][0]);
        return sticker_info[position][0];
    }

    String getThingInfo(int position){
        Log.d("TItan1", "Titan1 - spinner x thing info: " + thing_info[position][0]);
        return thing_info[position][0];
    }

    String getFunctionInfo(int position){
        Log.d("TItan1", "Titan1 - spinner x function info: " + function_info[position][0]);
        return function_info[position][0];
    }


    void connect_to_database(String url_string) {



        class DatabaseConnection extends AsyncTask<String, Void, String> {



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("Titan1", "Titan1 - connect_to_database started3 ");

            }

            @Override
            protected String doInBackground(String... params) {
                Log.d("Titan1", "Titan1 - doInBackground started");

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

                        sticker_length=json_array_for_sticker.length();
                        thing_length=json_array_for_thing.length();
                        function_length=json_array_for_function.length();
                        Log.d("Titan1","Titan1 - stickerlength"+sticker_length);
                        for(int i=0; i < json_array_for_sticker.length(); i++){

                            json_object_for_sticker = json_array_for_sticker.getJSONObject(i);

                            sticker_info[i][0]= json_object_for_sticker.optString("device_id").toString();
                            Log.d("Titan1", "Titan1 - device id = " + sticker_info[i][0]);

                            sticker_info[i][1]= json_object_for_sticker.optString("device_name").toString();
                            Log.d("Titan1","Titan1 - device_name = "+ sticker_info[i][1]);

                            sticker_info[i][2] = json_object_for_sticker.optString("button").toString();
                            Log.d("Titan1","Titan1 - number of buttons = " + sticker_info[i][2]);

                        }


                        Log.d("Titan1","Titan1 - json_array_for_function created for function");
                        //Iterate the jsonArray and print the info of JSONObjects
                        for(int i=0; i < json_array_for_function.length(); i++){
                            json_object_for_function = json_array_for_function.getJSONObject(i);
                            Log.d("Titan1", "Titan1 - jsonObject created for individual rows");

                            function_info[i][0]= json_object_for_function.optString("device_id").toString();
                            Log.d("Titan1", "Titan1 - device id = " + function_info[i][0]);

                            function_info[i][1] = json_object_for_function.optString("device_name").toString();
                            Log.d("Titan1", "Titan1 - device_name = " + function_info[i][1]);


                        }


                        Log.d("Titan1","Titan1 - json_array_for_thing created for thing");
                        //Iterate the jsonArray and print the info of JSONObjects
                        for(int i=0; i < json_array_for_thing.length(); i++){
                            json_object_for_thing = json_array_for_thing.getJSONObject(i);
                            Log.d("Titan1", "Titan1 - jsonObject created for individual rows");

                            thing_info[i][0] = json_object_for_thing.optString("device_id").toString();
                            Log.d("Titan1", "Titan1 - device id = " + thing_info[i][0]);

                            thing_info[i][1] = json_object_for_thing.optString("device_name").toString();
                            Log.d("Titan1", "Titan1 - device_name = " + thing_info[i][1]);

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
                Log.d("Titan1", "Titan1 - onPostExecute started  ");
            }
        }


        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.execute(url_string);
        Log.d("Titan1", "Titan1 - DatabaseConnection executed");

    }

}