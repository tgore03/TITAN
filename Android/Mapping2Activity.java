package com.example.titan.titan1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class Mapping2Activity extends AppCompatActivity {
    Spinner spinner_sticker;
    Spinner spinner_buttonid;
    Spinner spinner_thing;
    Spinner spinner_function;
    String sticker_id;
    String output_id;
    String button2;
    Button b1,b2,b3;
    TextView label;
    String device_input , device_output;


    Spinnerx mb=new Spinnerx();
//static int count=0;
    int slength;
    int tlength;
    int flength;

    private static String client_id = "0001";
    String uri = "http://titan1994.96.lt/mapping.php?cid="+client_id;
    int i;
    static int x=0;



    ArrayAdapter<String> adapter_state, adapter_state2, adapter_state3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mb.connect_to_database(uri);
        Toast.makeText(getApplicationContext(), "Loading...Wait for few seconds",
                Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                x();
            }
        }, 2000);
        Log.d("Titan1", "Titan1 - onCreate started");


    }
    public void x() {

        slength=mb.sticker_length;
        tlength=mb.thing_length;
        flength=mb.function_length;
        String [] sticker_info1 = new String[slength];
        String [] function_info1 = new String[flength];
        String [] thing_info1 = new String[tlength];

        for(i=0;i<slength;i++)
        {   sticker_info1[i]=mb.sticker_info[i][1];

        }
        for(i=0;i<flength;i++)
        {
            function_info1[i]=mb.function_info[i][1];
            Log.d("Titan1", "Titan1 -function_info1 array"+ function_info1[i]);
        }

        for(i=0;i<tlength;i++)
        {
            thing_info1[i]=mb.thing_info[i][1];
            Log.d("Titan1", "Titan1-thing_info1 array"+ thing_info1[i]);
        }



        List<String> Stickerlist= Arrays.asList(sticker_info1);
        Log.d("Titan1", "Titan1 - List sticker");

        List<String> Thinglist= Arrays.asList(thing_info1);
        Log.d("Titan1", "Titan1 - thing sticker");
        List<String> Functionlist= Arrays.asList(function_info1);
        Log.d("Titan1", "Titan1 - function sticker");


        Log.d("Titan1", "ADDED LIST");

        spinner_sticker = (Spinner) findViewById(R.id.sticker);
        spinner_buttonid = (Spinner) findViewById(R.id.button_id);
        spinner_thing = (Spinner) findViewById(R.id.thing);
        spinner_function = (Spinner) findViewById(R.id.function);
        spinner_thing.setVisibility(View.INVISIBLE);
        spinner_function.setVisibility(View.INVISIBLE);
        spinner_buttonid.setEnabled(false);
        spinner_thing.setEnabled(false);
        spinner_function.setEnabled(false);

        adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Stickerlist);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sticker.setAdapter(adapter_state);



        adapter_state2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Thinglist);
        adapter_state2
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_thing.setAdapter(adapter_state2);


        adapter_state3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Functionlist);
        adapter_state3
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_function.setAdapter(adapter_state3);

        spinner_sticker.setOnItemSelectedListener(sticker_listener);
        spinner_function.setOnItemSelectedListener(function_listener);
        spinner_thing.setOnItemSelectedListener(thing_listener);
        /*********************buttons**********************/
        b1=(Button)findViewById(R.id.button1);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        label=(TextView)findViewById(R.id.label);

        b3.setEnabled(false);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.INVISIBLE);
                spinner_thing.setVisibility(View.VISIBLE);
                b3.setEnabled(true);
               // count=2;
               // Log.d("Titan1", "Titan1 -Count value in thing"+count);
                label.setText("Select Thing");
                spinner_thing.setEnabled(true);
                spinner_function.setEnabled(false);
               spinner_thing.setAdapter(adapter_state2);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.INVISIBLE);
                spinner_function.setVisibility(View.VISIBLE);
                b3.setEnabled(true);
               // count=1;
               // Log.d("Titan1", "Titan1 -Count value in function"+count);
                label.setText("Select Function");
                spinner_function.setEnabled(true);
                spinner_thing.setEnabled(false);
                spinner_function.setAdapter(adapter_state3);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                device_input="/s/" ;
                device_input+=sticker_id + "/" +button2;
                Log.d("Titan1", "Titan1 -MAPPING INFO SENT"+device_input+device_output);

                new DatabaseConnectivity(Mapping2Activity.this).execute(device_input, device_output);
            }
        });
    }


    AdapterView.OnItemSelectedListener sticker_listener =
            new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    sticker_id=mb.sticker_info[position][0];
                    spinner_buttonid.setEnabled(true);
                    Log.d("Titan1", sticker_id);
                    Log.d("Titan1", "Titan1 - spinnerbutton enabled");
                    spinner_buttonid.setOnItemSelectedListener(button_listener);

                    int nob= Integer.parseInt(mb.sticker_info[position][2]);
                    Log.d("Titan1", "Titan1"+nob);
                    String [] button_info1 = new String[nob];
                    Log.d("Titan1", "Titan1 - buttonarray created");
                    int f=1;
                    for(i=0;i<nob;i++)
                    {
                        button_info1[i]= String.valueOf(f);
                        f++;
                    }
                    Log.d("Titan1", "Titan1 - loop executed");
                    List<String> Buttonlist= Arrays.asList(button_info1);
                    Log.d("Titan1", "Titan1 - list created");

                    ArrayAdapter<String> adapter_state1 = new ArrayAdapter<String>(Mapping2Activity.this,
                            android.R.layout.simple_spinner_item,Buttonlist);
                    Log.d("Titan1", "Titan1 - arrayadapter object");
                    adapter_state1
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_buttonid.setAdapter(adapter_state1);
                    Log.d("Titan1", "Titan1 - arrayadapter done");

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            };
    AdapterView.OnItemSelectedListener button_listener =
            new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {

                    button2= (String)parent.getItemAtPosition(position);
                    Log.d("Titan1", button2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            };
    AdapterView.OnItemSelectedListener thing_listener =
            new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String thing_sticker= (String)parent.getItemAtPosition(position);
                   // Log.d("Titan1", "Titan1 -Count value in thing listener"+count);

                    output_id = mb.getThingInfo(position);
                    Log.d("Titan1","thing selected"+thing_sticker+output_id);

                    Log.d("Titan1","get thing: "+mb.getThingInfo(position));

                    device_output="/t/";
                    device_output+=output_id;


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            };
    AdapterView.OnItemSelectedListener function_listener =
            new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    String  function_sticker= (String)parent.getItemAtPosition(position);
                    //Log.d("Titan1", "Titan1 -Count value in function listener"+count);

                        output_id = mb.function_info[position][0];
                        device_output="/t/";
                        device_output+=output_id;


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            };

}
class DatabaseConnectivity extends AsyncTask<String, Void, String> {

    private Context context;

    public DatabaseConnectivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... arg0) {

        String did_input = arg0[0];
        String did_output = arg0[1];
        Log.d("titan",did_input+did_output);

        String link;
        String data ="";
        BufferedReader bufferedReader;
        String result;

        try {

            data += "did_input=" + did_input +"&did_output="+ did_output;
            link = "http://titan1994.96.lt/second_mapping.php?" + data;
            Log.d("titan", link);
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        String jsonStr = result;
        Log.d("titan", result);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String cid = jsonObj.getString("query_result");
                Toast.makeText(context, "Devices " +
                        cid, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
        }
    }
}





