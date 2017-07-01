package com.example.titan.titan1;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;


public class HotspotActivity extends AppCompatActivity {

    String ssid = "pancham1";
    String pwd = "9820900119";


    TextView ip_textview, request_message_textview, hotspot_status_textview;
    Button hotspotbutton, tcp_connect_button;

    Button play_pause_button, previous_button, next_button, volume_up, volume_down;
    AudioManager audioManager;
    Intent intentDown;
    Intent intentUp;
    KeyEvent keyEventUp,keyEventDown;

    String request_message = "";
    String host_ip_address = "";

    boolean hotspot_status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hotspot_status_textview = (TextView)findViewById(R.id.hotspot_status_textview);
        ip_textview = (TextView)findViewById(R.id.ip_textview);
        request_message_textview = (TextView)findViewById(R.id.request_message_textview);
        request_message_textview.canScrollVertically(1);

        /*
        if(AccessPointManager.isApOn(this)) {
            WifiManager wifimanager = (WifiManager) HotspotActivity.this.getSystemService(this.WIFI_SERVICE);
            Log.d("Titan1", "Titan1 - wifi Accesspoint enabled");
        }
        */



        if(AccessPointManager.isApOn(getApplicationContext())) {
            hotspot_status = true;
        } else {
            hotspot_status = false;
        }



        // Connect button iplementation - Start / Stop hotspot & start TCP connection
        hotspotbutton = (Button)findViewById(R.id.hotspotbutton);
        hotspotbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AccessPointManager.isApOn(getApplicationContext())) {
                    if(AccessPointManager.configApState(HotspotActivity.this, false)) {
                        Log.d("Titan1", "Titan1 - onClick Hotspot started");
                        hotspotbutton.setText("Disconnect");
                        hotspot_status_textview.setText("Connected");

                        Log.d("Titan1", "Titan1 - countdowntimer start");
                        new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                Log.d("Titan1", "Titan1 - countdown finish");
                                if (AccessPointManager.isApOn(HotspotActivity.this)) {
                                    Log.d("Titan1", "Titan1 - AP started");
                                    //onStartConnection();
                                    startTCPConnection();

                                } else {
                                    Log.d("Titan1", "Titan1 - AP stopped");
                                }
                            }
                        }.start();
                    }
                }
                else{
                    if (AccessPointManager.configApState(HotspotActivity.this, true)) {
                        Log.d("Titan1", "Titan1 - onClick Hotspot stopped");

                        hotspot_status_textview.setText("Disconnected");
                        hotspotbutton.setText("Connect");
                        ip_textview.setText("");
                        request_message_textview.setText("");

                    }
                }
            }
        });


        tcp_connect_button = (Button)findViewById(R.id.tcp_connect_button);
        tcp_connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTCPConnection();
            }
        });


        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        play_pause_button = (Button)findViewById(R.id.play_pause_button);
        play_pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Titan1", "Titan1 - play pause onClick");
                play_pause();
                //sendMusicCommand("play_pause");
            }
        });

        previous_button = (Button)findViewById(R.id.previous_button);
        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Titan1", "Titan1 - prev onClick");
                //sendMusicCommand("prev");
                prev();
            }
        });

        next_button = (Button)findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Titan1","Titan1 - next onClick");
              //  sendMusicCommand("next");
                next();
            }
        });

        volume_up = (Button)findViewById(R.id.volume_up_button);
        volume_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                volume_up();
            }
        });

        volume_down = (Button)findViewById(R.id.volume_down_button);
        volume_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume_down();

            }
        });
    }

    // ************************************ TCP (new) Connection Start *******************************************//



    //     Get IP Address of device
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            System.out.println(" Network Interfaces = " + enumNetworkInterfaces);

            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
            Log.e("Titan1", "Titan1 - " + e);
        }

        return ip;
    }

    ServerSocket serverSocket;
    Socket socket;
    static final int SERVER_PORT = 7788;


    void startTCPConnection(){

        host_ip_address = getIpAddress();
        Log.d("Titan1", "Titan1 - startTCPConnection host_ip = " + host_ip_address);
        ip_textview.setText(host_ip_address + ":" + SERVER_PORT);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Log.d("Titan1", "Titan1 - Listener Thread run() started");

                    //Create a server socket object and bind it to a port
                    serverSocket = new ServerSocket(SERVER_PORT);
                    //Create server side client socket reference
                    socket = null;

                    //Infinite loop will listen for client requests to connect
                    while (true) {

                        //Accept the client connection and hand over communication to server side client socket
                        socket = serverSocket.accept();
                        Log.d("Titan1", "Titan1 - connection accepted. temporary socket created");

                        //For each client new instance of AsyncTask will be created
                        ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                        //Start the AsyncTask execution
                        //Accepted client socket object will pass as the parameter
                        serverAsyncTask.execute(new Socket[] {socket});
                        Log.d("Titan1", "Titan1 - serverAsyncTask executed. socket passed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Titan1", "Titan1 - exception in listener Thread e=" + e);
                }
            }
        }).start();
    }

    /**
     * AsyncTask which handles the communication with clients
     */
    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        //Background task which serve for the client
        @Override
        protected String doInBackground(Socket... params) {
            Log.d("Titan1", "Titan1 - doInBackground of Async Task started");

            String result = null;
            //Get the accepted socket object
            Socket clientSocket = params[0];


            try {

                //Get the data input stream comming from the client
                InputStream inputStream = clientSocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true);
                //Write data to the data output stream
                out.println("Hello from server");
                //Buffer the data input stream
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));
                //Read the contents of the data buffer
                result = bufferedReader.readLine();
                Log.d("Titan1", "Titan1 - request = "+ result);

                //Close the client connection
                clientSocket.close();
                Log.d("Titan1", "Titan1 - async client closed.");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Titan1", "Titan1 - exception in  Async Task e = " + e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            //After finishing the execution of background task data will be write the text view
            request_message = s + "\n" + request_message;
            Log.d("Titan1", "Titan1 - onPostExecute started. s =  " + s);
            request_message_textview.setText(request_message);
            if(s.contains("/501203/1")) {
                Log.d("Titan1","Titan1 - request matched");
                volume_down();
            } else if(s.contains("/501203/2")) {
                Log.d("Titan1","Titan1 - request matched");
                volume_up();
            } else if(s.contains("/501203/3")) {
                Log.d("Titan1","Titan1 - request matched");
                play_pause();
            } else if(s.contains("/501203/4")) {
                Log.d("Titan1","Titan1 - request matched");
                prev();
            } else if(s.contains("/501203/5")) {
                Log.d("Titan1","Titan1 - request matched");
                next();
            }
            else {
                Log.d("Titan1", "Titan1 - request not matched");
                next();
            }

        }
    }




    // *************************************TCP (new) Connection End ***********************************************//




    // ************************************ Functions ***************************************//

    void next(){

        intentDown = new Intent(Intent.ACTION_MEDIA_BUTTON,null);
        intentUp = new Intent(Intent.ACTION_MEDIA_BUTTON,null);

        keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_NEXT);
        intentDown.putExtra(Intent.EXTRA_KEY_EVENT,keyEventDown);
        keyEventUp = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_NEXT);
        intentUp.putExtra(Intent.EXTRA_KEY_EVENT,keyEventUp);

        Log.d("Titan1", "Titan1 - next keyevent initialized");
        sendOrderedBroadcast(intentDown,null);
        sendOrderedBroadcast(intentUp, null);
        Log.d("Titan1", "Titan1 - Broadcast sent");
    }


    void prev(){

        intentDown = new Intent(Intent.ACTION_MEDIA_BUTTON,null);
        intentUp = new Intent(Intent.ACTION_MEDIA_BUTTON,null);

        keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        intentDown.putExtra(Intent.EXTRA_KEY_EVENT, keyEventDown);
        keyEventUp = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        intentUp.putExtra(Intent.EXTRA_KEY_EVENT, keyEventUp);

        Log.d("Titan1", "Titan1 - prev keyevent initialized");
        sendOrderedBroadcast(intentDown, null);
        sendOrderedBroadcast(intentUp, null);
        Log.d("Titan1", "Titan1 - Broadcast sent");
    }

    void play_pause(){

        intentDown = new Intent(Intent.ACTION_MEDIA_BUTTON,null);
        intentUp = new Intent(Intent.ACTION_MEDIA_BUTTON,null);

        keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        intentDown.putExtra(Intent.EXTRA_KEY_EVENT,keyEventDown);
        keyEventUp = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        intentUp.putExtra(Intent.EXTRA_KEY_EVENT, keyEventUp);

        Log.d("Titan1", "Titan1 - play pause keyevent initialized");
        sendOrderedBroadcast(intentDown, null);
        sendOrderedBroadcast(intentUp, null);
        Log.d("Titan1", "Titan1 - Broadcast sent");
    }

    void volume_up(){

        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        if(audioManager.isMusicActive()) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            Log.d("Titan1","Titan1 - Music volume up");
        } else {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            Log.d("Titan1", "Titan1 - Ring volume up");
        }
        Log.d("Titan1", "Titan1 - volume up end");
    }

    void volume_down(){

        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        if(audioManager.isMusicActive()) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            Log.d("Titan1","Titan1 - Music volume down");
        } else {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            Log.d("Titan1", "Titan1 - Ring volume down");
        }
        Log.d("Titan1", "Titan1 - volume down end");
    }

    // *************************************** Functions End **************************************************//




    // ************************************ TCP(old) Connection Start *********************************************//

    /*
    public void onStartConnection(){
        Log.d("Titan1", "Titan1 - onStartConnection started");

        // Create object of below thread class and start the thread for server.
        Thread socketServerThread = new Thread(new SocketServerThread());
        Log.d("Titan1", "Titan1 - sockerServerThread created");

        socketServerThread.start();
        Log.d("Titan1", "Titan1 - ");
    }


    private class SocketServerThread extends Thread {

        @Override
        public void run() {

            Log.d("Titan1", "Titan1 - run() of socketServerthread started");
            System.setProperty("http.keepAlive","false");

            try {
                serverSocket = new ServerSocket(socketserverport);
                Log.d("Titan1", "Titan1 - Created serverSocket object");
                HotspotActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Titan1", "Titan1 - run() of socketServer runOnUiThread started : print ip");
                        ip_textview.setText("I'm waiting here: " + getIpAddress() + ":" + serverSocket.getLocalPort());
                        hotspot_status_textview.setText("Connected");
                    }
                });

                while (true) {

                    // Read Request from client
                    Log.d("Titan1", "Titan1 - socketServerThread initialize objects");
                    Socket socket = serverSocket.accept();
                    Log.d("Titan1", "Titan1 - socket accept done; socket = "+socket);
                    inputStreamReader = new InputStreamReader(socket.getInputStream());
                    Log.d("Titan1","Titan1 - inputStreamReader object created; inputStreamReader = "+ inputStreamReader);
                    bufferedReader = new BufferedReader(inputStreamReader); //get the client message
                    Log.d("Titan1","Titan1 - Buffered reader object created; bufferedReader = "+ bufferedReader);
                    request_message += "\n request: " + bufferedReader.readLine();
                    Log.d("Titan1", "Titan1 - arrived request message = " + request_message);

                    HotspotActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Titan1", "Titan1 - run() of socketServerThread runOnUiThread started: print request message");
                            request_message_textview.setText(request_message);
                        }
                    });

                    // Create object of SocketServerReplyThread Class defined below and start the thread for reply.
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket);
                    Log.d("Titan1", "Titan1 - created socketServerResponsethread");
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Titan1", "Titan1 - " + e);
                Log.v("Titan1", "Titan1 - catch block of socketServerThread");
            }


        }

    }

    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
        }

        @Override
        public void run() {
            Log.d("Titan1", "Titan1 - run() of socketServerReplyThread");

            OutputStream outputStream;
            String msgReply = "Hello from Android";

            try {
                // Send response to client
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                Log.d("Titan1", "Titan1 - socketServerReplyThread PrintStream initialized");

                request_message += "\n replayed: " + msgReply;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                request_message += "Something wrong! " + e.toString() + "\n";
                Log.e("Titan1", "Titan1 - " + e);
                Log.d("Titan1", "Titan1 - catch block of socketServerReplyThread \n request_message = " + request_message);

            }

            HotspotActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.d("Titan1", "Titan1 - run() of socketServerReplyThread: print response or error msg");

                    request_message_textview.setText(request_message);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("Titan1", "Titan1 - onDestroy() started");
        if (serverSocket != null) {
            try {
                serverSocket.close();
                Log.v("Titan1", "Titan1 - serverSocket closed");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Titan1", "Titan1 - " + e);
            }
        }

    }
    */

    // *********************************** TCP (old) Connection End *****************************************//

}


class AccessPointManager {
    //check whether wifi hotspot on or off

   /*Variables used
    *
    *HotspotActivity hotspotActivity;
    *WifiManager wifiManager;
    *Method method;
    *WifiConfiguration wifiConfiguration; */

    static HotspotActivity hotspotActivity = new HotspotActivity();
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        Method method;
        try {
            method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {
            return false;
        }
        finally {
            wifimanager = null;
            method = null;
        }
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context, boolean hotspot_status) {
        Log.v("Titan1", "Titan1 - configApState start");

        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        Method method;

        // WifiConfiguration wificonfiguration = null;
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = hotspotActivity.ssid;
        wifiConfiguration.preSharedKey = hotspotActivity.pwd;
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);


        if(!hotspot_status) {
            Log.d("Titan1","Titan1 - hotspot status = " + hotspot_status);
            if(wifimanager.isWifiEnabled()) {
                wifimanager.setWifiEnabled(false);
                Log.d("Titan1","Titan1 - hotspot status = false: wifi disabled");
            }
            else {
                Log.d("Titan1", "Titan1 - hotspot status = false: WiFi already off");
            }

            try {
                Log.d("Titan1","Titan1 - start hotspot");

                method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifimanager, wifiConfiguration, true);

                Log.d("Titan1", "Titan1 - hotspot status set to true");
                return true;
                /*
                if(!isApOn(context)) {
                    method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    method.invoke(wifimanager, wifiConfiguration, true);
                }
                else {
                    Log.d("Titan1", "Titan1 - hotspot already on ");
                    method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    method.invoke(wifimanager, wifiConfiguration, false);
                    method.invoke(wifimanager, wifiConfiguration, true);
                }
                */
            } catch (NoSuchMethodException e) {
                Log.d("Titan1", "Titan1 - ");
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                Log.d("Titan1","Titan1 - ");
                e.printStackTrace();
                return false;
            } catch (IllegalAccessException e) {
                Log.d("Titan1","Titan1 - ");
                e.printStackTrace();
                return false;
            }

        } else {
            Log.d("Titan1","Titan1 - hotspot status = " + hotspot_status);
            try {
                method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifimanager, wifiConfiguration, false);

                wifimanager.setWifiEnabled(true);
                return true;
            } catch (NoSuchMethodException e) {
                Log.d("Titan1","Titan1 - ");
                e.printStackTrace();
                return false;
            } catch (InvocationTargetException e) {
                Log.d("Titan1","Titan1 - ");
                e.printStackTrace();
                return false;
            } catch (IllegalAccessException e) {
                Log.d("Titan1","Titan1 - ");
                e.printStackTrace();
                return false;
            }


        }
        /*
        try {
            method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);

            // if WiFi is on, turn it off
            if(wifimanager.isWifiEnabled()) {
                wifimanager.setWifiEnabled(false);
                Log.v("Titan1", "Titan1 - WiFi turned off");
                method.invoke(wifimanager, wifiConfiguration, true);
                Log.v("Titan1", "Titan1 - Hotspot started");
            }
            else{
                method.invoke(wifimanager, wifiConfiguration, false);
                Log.v("Titan1", "Titan1 - Hotspot stoped");

                wifimanager.setWifiEnabled(true);
                Log.v("Titan1", "Titan1 - WiFi turned on");
            }


            return true;
        }

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            wifimanager = null;
            wifiConfiguration = null;
            method = null;
        }
        */
    }

}


