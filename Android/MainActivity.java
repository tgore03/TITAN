package com.example.titan.titan1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    String topic = "/0001/esp";
    String willtopic = "/0001/esp1";
    String username = "bnbqhbne";
    String password = "Q64GD7kAtlDv";
    String url = "m10.cloudmqtt.com";

    String device_id = "123456";
    String client_id = "0001";

    long port = 16583;

    int publishqos = 1;
    int subscribeqos = 1;
    int will_qos = 1;

    int connect_attempt = 3;

    String received_message;

    String will_message = "/" + client_id + "/" +device_id + " disconnect";
    byte[] willpayload = will_message.getBytes();

    String message = "/" + client_id + "/" + device_id;
    byte[] msgpayload = new byte[0]; // used to formulate and store mqttmessage to publish.


    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    Button mqtt_connect_button;

    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;
    IMqttToken iMqttToken;


    Switch relay1_switch, relay2_switch;

    boolean internet_available;
    Button connect_button, refresh_button, subscribe_button;

    AlertDialog.Builder alertDialogBuilder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // listener implemented later on


        // *****************Abhilasha code **************//
        new Database3Connectivity(MainActivity.this).execute();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Titan1", "Titan1-run");
                x();
            }
        }, 2000);

        // ******************** Abhilasha code end *****************//

        /*
        relay1_switch = (Switch) findViewById(R.id.relay1_switch);
        relay1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Titan1", "Titan1 - onCheckedChanged of Relay1 started");
                //statusButtonUpdate(buttonView);
                if(isChecked)
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
            }
        });


        relay2_switch = (Switch) findViewById(R.id.relay2_switch);
        relay2_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Titan1", "Titan1 - onCheckedChanged of Relay2 started");

                // isChecked tells that button should be set to the value in isChecked
                if(isChecked)
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
            }
        });
     */








        refresh_button = (Button)findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.rl);
                Log.d("Titan1", "Titan1 - Remove all Relative layout views");
                relativeLayout.removeAllViews();
                Log.d("Titan1", "Titan1 - Relative layout views removed");

                new Database3Connectivity(MainActivity.this).execute();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Titan1", "Titan1-run");
                        x();
                    }
                }, 2000);

            }
        });

        subscribe_button = (Button)findViewById(R.id.mqtt_subscribe_button);
        subscribe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subscribe();
            }
        });









        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });



    }
    void statusButtonUpdate(CompoundButton buttonView) {
        Log.d("Titan1", "Titan1 - statusButtonUpdate started");
        // determine the present state of the button and call onCheckedChange method above to complete the response action
        // buttonView.setChecked call the onCheckedChanged method of the corresponding button.
        if (buttonView.isChecked()) {
            Log.d("Titan1","Titan1 - isChecked = " + buttonView.isChecked());
            buttonView.setChecked(false);
            Log.d("Titan1", "Titan1 - statusButtonUpdate : set to off");
        } else {
            buttonView.setChecked(true);
            Log.d("Titan1", "Titan1 - statusButtonUpdate : set to on");
        }


        // ************************** my functions *********************************//
    }















    // ******************* abhilasha function ********************//



    // *********** abhilasha variables ****************//

    RelativeLayout rl;
    Switch[] switch_controller;
    TextView[] tv;

    String did;
    static  int ps;


    // ******** abhilsha variables end ******************//





    // after list populated, dynamically create spinners and show them.
    void x()
    {
        Database3Connectivity db = new Database3Connectivity(this);

        int rows=db.r;
        int xmargin=100;
        int ymargin=350;
        Log.d("Titan14", "SS" + rows);

        tv= new TextView[rows];
        switch_controller = new Switch[rows];

        for(int i=0;i<rows;i++)
        {
            rl = (RelativeLayout) findViewById(R.id.rl);
            Log.d("Titan14", "rl" );

            switch_controller[i]= new Switch(MainActivity.this);

            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            switch_controller[i].setLayoutParams(params);

            params.leftMargin=500;
            params.topMargin=ymargin;
            rl.addView(switch_controller[i]);
            Log.d("Titan1", "Titan1 - swtich view added to rl");


            if (db.status[i].compareTo("off")==0) {
                switch_controller[i].setChecked(false);
                Log.d("Titan1", "Titan16 - statusButtonUpdate : set to off");
            } else {
                switch_controller[i].setChecked(true);
                Log.d("Titan1", "Titan1 - statusButtonUpdate : set to on");
            }




            tv[i] = new TextView(MainActivity.this);
            Log.d("Titan14", "array");
            Log.d("Titan14", "array" + db.dname[i]);
            tv[i].setText(db.dname[i]);
            tv[i].setTextColor(Color.parseColor("#009688"));
            tv[i].setTextSize(16);

            RelativeLayout.LayoutParams params2=new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT,(int) RelativeLayout.LayoutParams.WRAP_CONTENT);

            Log.d("Titan14", "rlparams");
            params2.leftMargin=xmargin;
            params2.topMargin=ymargin;
            Log.d("Titan14", "margin");
            tv[i].setLayoutParams(params2);
            Log.d("Titan14", "viewadd");
            rl.addView(tv[i]);
            ymargin+=80;
        }

    }


    void y()
    {

        Log.d("Titan1", "Titan1- y entered");
        Database3Connectivity db = new Database3Connectivity(this);
        Log.d("Titan1", "Titan1- db created");

        int rows=db.r;


        for(int i=0;i<rows;i++)
        {
            Log.d("Titan1", "Titan1- for entered. did = "+ db.did[i] + "did = " + did);

            int result = db.did[i].compareTo( did );
            Log.d("Titan1", "Titan1- result = " + result);

            if(result==0) {
                Log.d("Titan1", "Titan1- result 0 if entered");
                ps = i;
            }
            Log.d("Titan16","ps"+ps);
        }
        Log.d("Titan16", "psout" + ps);
        if(db.status[ps].compareTo("off")==0)
            switch_controller[ps].setChecked(false);
        if(db.status[ps].compareTo("on")==0)
        {
            Log.d("Titan16", "psout setcheck" + ps);
            switch_controller[ps].setChecked(true);

        }


    }





    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Titan1", "Titan1 - Start preference");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getBoolean("firstTime", false)) {
            // run your one time code here
            Log.d("Titan1", "Titan1 - App start for the first time ");
            Log.d("Titan1", "Titan1 - prefs = " + prefs.getBoolean("firstTime", false));

            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            Log.d("Titan1", "Titan1 - first time boolean set to true");
            editor.commit();
        } else {
            checkConnectivity();

            mqtt_connect_button = (Button) findViewById(R.id.mqtt_connect_button);
            mqtt_connect_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkConnectivity();
                  //  if (internet_available)
                        //connect();
                   // else
                      //  Toast.makeText(getApplicationContext(), "Connection Failed. Please check internet connectivity and press connect button", Toast.LENGTH_SHORT).show();
                }
            });

           // if (internet_available)
              //  connect();
           // else
            //    Toast.makeText(getApplicationContext(), "Connection Failed. Please check internet connectivity and press connect button", Toast.LENGTH_SHORT).show();

        }
    }



    void checkConnectivity(){
        String answer = "";
        try {
            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null) {

                /*
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    answer = "Connected to WiFi Network";
                    internet_available = true;
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    answer = "Connected to Mobile Network";
                    internet_available = true;
                }

                */

                if(networkInfo.isAvailable()) {
                    answer = "Connected to Internet";
                    internet_available = true;
                } else {
                    answer = "Internet Unavailable. Check the connectivity.";
                    internet_available = false;
                }

            } else {
                answer = "Internet Unavailable. Check the connectivity.";
                internet_available = false;
            }

            Toast.makeText(this, answer, Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.d("Titan1", "Titan1 - exception =" + e);
        }
        finally {
            networkInfo = null;
            connectivityManager = null;
            answer = null;
        }


    }

    public MqttAndroidClient getMqttClient() {
        if(mqttAndroidClient != null)
            return mqttAndroidClient;
        else
            return null;
    }

    public void mqttDisconnect() {
        Log.d("Titan1","Titan1 - mqttDisconnect started");
        mqttAndroidClient.close();
    }

    boolean connect_status;

    public boolean connect()
    {

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), "tcp://" + url + ":" + port,
                client_id);

        mqttAndroidClient.setCallback(new mainCallback());

        try {
            // Set connection parameters
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            mqttConnectOptions.setWill(willtopic, willpayload, will_qos, false);
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            Log.d("Titan1", "Titan1 - mqtt parameters set");

            // Establish connection

            iMqttToken = mqttAndroidClient.connect(mqttConnectOptions);
            Log.d("Titan1","Titan1 - mqtt connected; iMqttToken = "+iMqttToken);

            iMqttToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Titan1", " Titan1 - onSuccess start: mqtt connect success");
                    Toast.makeText(getApplicationContext(),"Connected to Server",Toast.LENGTH_SHORT).show();
                    //connect_status = true;
                    //connect_attempt = 3;
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Titan1", "Titan1 - onFailure start: Connect Failed");
                    connect_status = false;
                    Toast.makeText(getApplicationContext(),"Connection to Server failed",Toast.LENGTH_SHORT).show();

                    //connect_attempt--;
                    //Log.d("Titan1", "Titan1 - connect_attempt = " + connect_attempt);
                    /*
                    if (connect_attempt >= 0) {
                        mqttDisconnect();
                        connect();
                    }
                    else {
                        Log.d("Titan1", "Titan1 - onFailure start: connect attempt < 0 ");
                        Toast.makeText(getApplicationContext(), "Connection Failed. Please check internet connectivity and press connect button", Toast.LENGTH_SHORT).show();
                        mqttDisconnect();
                    }
                    */
                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("Titan1", "Titan1 - exception in mqtt connect: " + e);
            connect_status = false;
        }
        Log.d("Titan1", "Titan1 - connect_status = " + connect_status);
        return connect_status;
    }


    boolean subscribe_status;
    public boolean subscribe() {
        try {

            IMqttToken subToken = mqttAndroidClient.subscribe(topic, subscribeqos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Titan1", "subscribed");
                    //Toast.makeText(getApplicationContext(), "successfully subscribed",Toast.LENGTH_LONG).show();
                    subscribe_status = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("Titan1", "Subscribe Failure");
                    Toast.makeText(getApplicationContext(),"Subscribe failed", Toast.LENGTH_SHORT).show();
                    subscribe_status = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            subscribe_status = false;
        }
        return subscribe_status;
    }


    private class mainCallback implements MqttCallback {
        public void connectionLost(Throwable cause)
        {
            Log.d("Titan1", "MQTT Server connection lost, cause = "+ cause);
            connect_status = false;

            Toast.makeText(getApplicationContext(), "Connection Lost. Tap connect button.", Toast.LENGTH_SHORT).show();
            /*
            if(connect_attempt >= 0) {
                Log.d("Titan1", "Titan1 - connect_attempt = " + connect_attempt);
                mqttDisconnect();
                connect();
            }
            else {
                Toast.makeText(getApplicationContext(), "Connection Failed. Please check internet connectivity and press connect button", Toast.LENGTH_SHORT).show();
                mqttDisconnect();
            }

            */

        }
        public void messageArrived(String topic, MqttMessage message)
        {
            Log.d("Titan1", "Message arrived:" + topic + ":" + message.toString());
            CompoundButton tempbutton;



            /*

            if(message.toString().contains("relay1")){
                tempbutton = (CompoundButton)findViewById(R.id.relay1_switch);
                statusButtonUpdate(tempbutton);
            } else if (message.toString().contains("relay2")) {
                tempbutton = (CompoundButton)findViewById(R.id.relay2_switch);
                statusButtonUpdate(tempbutton);
            }

            */



            // ********** abhilasha code ****************///

            String msg=message.toString();
            Log.d("Titan1", "Titan1 - run" + msg.substring(0, 2));
            String a= "/a";

            did=msg.substring(3,9);
            Log.d("Titan15", "run" + did);

            int result = msg.substring(0,2).compareTo(a);

            if(result==0)
            {
                Log.d("Titan15", "run1");
                new Database3Connectivity(MainActivity.this).execute();
                Log.d("Titan15", "run");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Titan1", "Titan1 - run");
                        y();
                    }
                }, 3000);
            }


            // abhilasha code end *************************//
        }
        public void deliveryComplete(IMqttDeliveryToken token)
        {
            Log.d("Titan1", "Delivery complete");
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.sticker_connect) {
            intent = new Intent(this, HotspotActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.configuration) {
            intent = new Intent(this, ConfigurationActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.help) {
            intent = new Intent(this, HelpActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.settings) {

        } else if (id == R.id.login){
            intent = new Intent(this, RegistrationActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.mapping) {
            intent = new Intent(this, Mapping2Activity.class);
            this.startActivity(intent);
        } else if (id == R.id.about_us) {
            intent = new Intent(this, AboutUsActivity.class);
            this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Context getContext(){
        return MainActivity.this;
    }
}



// Database Connectivity and data retrieval
class Database3Connectivity extends AsyncTask<String, Void, String> {
    public String client_id="0001";
    private Context context;

    static  String[] dname;
    static  String[] status;
    static  String[] did;
    static  int r;

    public Database3Connectivity(Context context) {
        this.context = context;
    }

    ProgressDialog progressDialog;

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Loading..", null, true, true);
    }

    @Override
    protected String doInBackground(String... arg0) {

        String link;
        String data ="";
        BufferedReader bufferedReader;
        String result;

        try {
            Log.d("Titan15","db again");

            data += "cid=" + client_id ;
            link = "http://titan1994.96.lt/get_devices.php?" + data;
            Log.d("titan", link);
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            JSONObject jsonRootObject = new JSONObject(result);
            JSONArray jsonArray = jsonRootObject.optJSONArray("devices");

            dname=new String[jsonArray.length()];
            status=new String[jsonArray.length()];
            did=new String[jsonArray.length()];
            r=jsonArray.length();

            Log.d("Titan14","DDD"+r);

            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                status[i] = jsonObject.optString("status").toString();
                dname[i] = jsonObject.optString("device_name").toString();
                did[i] = jsonObject.optString("device_id").toString();
                Log.d("Titan14",status[i]+dname[i]);
            }

            return result;
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        progressDialog.dismiss();
    }
}




