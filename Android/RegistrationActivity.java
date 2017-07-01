package com.example.titan.titan1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    EditText user_password_edittext, user_email_edittext;
    Button signup_button;
    public String user_email, user_password, failure_msg;
    private String user_id, client_id;

    CheckBox show_pwd_checkbox;

    String uri = "http://titan1994.96.lt/indextrue.php?";

    Boolean login = false, email_validation = false, password_validation = false;
    int return_attempt = 1;
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Titan1","Titan1 - onCreate started");
        // change the id later
        user_password_edittext = (EditText) findViewById(R.id.password_edittext);
        user_email_edittext = (EditText) findViewById(R.id.email_address_edittext);

        show_pwd_checkbox = (CheckBox) findViewById(R.id.show_user_pasword_checkbox);
        show_pwd_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (show_pwd_checkbox.isChecked()) {
                    Log.d("Titan1","Titan1 - isChecked (true) = " + isChecked);
                    user_password_edittext.setTransformationMethod(null);
                } else {
                    user_password_edittext.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        Log.d("Titan1","Titan1 - edittext and check box object created");
        show_pwd_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (show_pwd_checkbox.isChecked()) {
                    Log.d("Titan1", "Titan1 - isChecked (true) = " + isChecked);
                    user_password_edittext.setTransformationMethod(null);
                } else {
                    user_password_edittext.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        Log.d("Titan1", "Titan1 - signup button object create");
        signup_button = (Button)findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_password = user_password_edittext.getText().toString();
                user_email = user_email_edittext.getText().toString();

                Log.d("Titan1", "Titan1 - onClick get the email and password entered by the user");



                if (!isValidEmail(user_email)) {
                    user_email_edittext.setError("Invalid Email");
                    email_validation = false;
                } else {
                    email_validation = true;
                }
                if (!isValidPassword(user_password)) {
                    user_password_edittext.setError("Invalid Password. Min 7 chars required");
                    password_validation = false;
                } else {
                    password_validation = true;
                }


                if (email_validation && password_validation) {
                    Toast.makeText(getApplicationContext(), "Signing up...", Toast.LENGTH_SHORT).show();
                    new Signup(getApplicationContext()).execute(user_password, user_email);
                }
                Log.d("Titan1", "Titan1 - SignUpActivity executed ; onClick end");

            }
        });


        alertDialogBuilder=new AlertDialog.Builder(this);
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




    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }




    @Override
    protected void onStart() {
        super.onStart();

        /*

        Log.d("Titan1", "Titan1 - Start of web view");
        WebView myWebView = (WebView) findViewById(R.id.webview);
        Log.d("Titan1","Titan1 - Web view object created");
        WebSettings webSettings = myWebView.getSettings();
        Log.d("Titan1", "Titan1 - Web Settings created");
        webSettings.setJavaScriptEnabled(true);
        Log.d("Titan1", "Titan1 - java script activated");
        myWebView.loadUrl(uri);
        Log.d("Titan1", "Titan1 - Web view url loaded");

        */

        //connect_to_database(uri);
    }


    @Override
    public void onBackPressed() {
        if(login){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            if(return_attempt == 1) {
                Toast.makeText(getApplicationContext(), "Please Login to Continue.", Toast.LENGTH_LONG).show();
                return_attempt--;
            } else {

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        }


    }

    class Signup extends AsyncTask<String, Void, String> {

        private Context context;
        public Signup(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg0) {
            Log.d("Titan1","Titan1 -  doInBackground started  ");

            String link;
            String data ="";
            BufferedReader bufferedReader;
            String result;

            try {
                Log.d("Titan1","Titan1 - start url encoding");
                data += "emailaddress=" + URLEncoder.encode(user_email, "UTF-8");
                data += "&password=" + URLEncoder.encode(user_password, "UTF-8");


                Log.d("Titan1","Titan1 - start httpURLConnection");
                link = "http://titan1994.96.lt/indextrue.php?" + data;
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.d("Titan1","Titan1 - create buffered reader object");

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = bufferedReader.readLine();
                Log.d("Titan1","Titan1 - result = " + result);

                return result;
            } catch (Exception e) {
                Log.d("Titan1","Titan1 - exception occured = " + e);
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;

            Log.d("Titan1","Titan1 - onPostExecute started");

            if (jsonStr != null) {
                Log.d("Titan1","Titan1 - json string not empty");
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Log.d("Titan1","Titan1 - json object created");
                    client_id = jsonObj.getString("cid");
                    user_id = jsonObj.getString("uid");

                    if(client_id.contains("Email")) {
                        failure_msg = client_id;
                        Toast.makeText(getApplicationContext(), failure_msg, Toast.LENGTH_SHORT).show();
                        login = false;
                    }
                    else
                    {

                        Toast.makeText(getApplicationContext(), "client id = " + client_id + " user id = " + user_id, Toast.LENGTH_SHORT).show();

                        Log.d("Titan1","Titan1 - client id obtained from registration Activity");


                        // To be removed later on. The ids should be kept private.

                        login = true;

                    }
                       // Toast.makeText(getApplicationContext(), "client id = " + client_id + " user id = " + user_id, Toast.LENGTH_SHORT).show();

                  //  Log.d("Titan1","Titan1 - client id obtained from registration Activity");


                    // To be removed later on. The ids should be kept private.

                  //  login = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Titan1", "Titan1 - exception in onPostExecution e = " + e);
                    //Toast.makeText(getApplicationContext(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("Titan1", "Titan1 -  json data empty");
               // Toast.makeText(getApplicationContext(), "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
