#include <ESP8266WiFi.h>

const uint32_t device_id = 501203;
const char client_id[] = "0001";

const char* a_wifi_ssid = "pancham1";
const char* a_wifi_pwd = "9820900119";

const char* host = "192.168.43.1";
const int port = 7788;

const int n = 5; // No. of Buttons
int button[n][2];

int queue[15];
int start = 0;
int current = -1;

String msg_string;
char* msg_s;
char msg[50];




WiFiClient client;

volatile long last_debounce_time = 0;
long debounce_delay = 200;
long start_time = 0;
long end_time = 20000;



void finish(){
  Serial.println(" finish start ");
  Serial.println(millis());
  ESP.deepSleep(10000*10, WAKE_RFCAL);
}


void enqueue(int button_id){
  if(current < 14) {
    current++;
    //Serial.print("Enqueue; current = ");
    //Serial.println(current);
    queue[current] = button_id;
  }
  else {
    //Serial.println("queue full to enqueue");
  }
}


int dequeue(){
  int button_id;
  if(start <= current) {
    //Serial.print("dequeue: start = ");
    //Serial.println(start);
    button_id = queue[start++];
    if( start > current ) {
      start = 0;
      current = -1;
    }
    return button_id;
  } else {
    //Serial.print("queue empty");
    return -1;
  }
}

void tcpconnect() {
  int retries = 10;
  
  while (!client.connected()) {
    Serial.print("Attempting TCP connection...");
    if (client.connect(host, port)) {
      Serial.println("connected");
    } 
    else {      
      Serial.println(" trying again...");
      client.stop();
      delay(100);
      if(retries--==0){
        Serial.println(" waiting to die...");
        finish();
        //while(1);
      }
    }
  }
}


void send_request(){
  int button_id;
  int byte_sent;
  String temp;
  
  while(start <= current) {
    tcpconnect();
    
    button_id = dequeue();
    Serial.print("button id = ");
    Serial.println(button_id);
    
    temp = msg_string;
    temp += String(button_id);
    Serial.print("Sent msg  = ");
    Serial.println(temp);

    byte_sent = client.println(temp);
    delay(100);
    
    if(byte_sent < temp.length()){
      Serial.println("Sending failed. Retrying");
      start--;
    } else {
      Serial.println("Send successful");
    }
    client.stop();
  }

  end_time = millis() + 10000;
}



void setup_wifi() {
  int retry = 20;
  delay(10);
  
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(a_wifi_ssid);

  WiFi.begin(a_wifi_ssid, a_wifi_pwd);
  Serial.print("connecting..");
  delay(500);
  
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
    if(retry-- == 0) {
      Serial.println("Not connected");
      finish();
      //while(1);
    }
  }

  Serial.println("");
  Serial.println("WiFi connected");
  //Serial.println("IP address: ");
  //Serial.println(WiFi.localIP());
}







void buttonAction1(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    //Serial.println("1");
    //end_time = millis() + 10000;
    enqueue(1);
  }
}


void buttonAction2(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    //Serial.println("2");
    //end_time = millis() + 10000;
    enqueue(2);
  }
}

void buttonAction3(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    //Serial.println("3");
    //end_time = millis() + 10000;
    enqueue(3);
  }
}

void buttonAction4(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    //Serial.println("4");
    //end_time = millis() + 10000;
    enqueue(4);
  }
}

void buttonAction5(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    //Serial.println("5");
    //end_time = millis() + 10000;
    enqueue(5);
  }
}


void buttonInitialize(){
  
  button[0][0] = 1;
  button[0][1] = 4;
  pinMode(button[0][1], INPUT_PULLUP);
  attachInterrupt(button[0][1], buttonAction1, FALLING);

  button[1][0] = 2;
  button[1][1] = 5;
  pinMode(button[1][1], INPUT_PULLUP);
  attachInterrupt(button[1][1], buttonAction2, FALLING);
  
  button[2][0] = 3;
  button[2][1] = 12;
  pinMode(button[2][1], INPUT_PULLUP);
  attachInterrupt(button[2][1], buttonAction3, FALLING);
  
  button[3][0] = 4;
  button[3][1] = 13;
  pinMode(button[3][1], INPUT_PULLUP);
  attachInterrupt(button[3][1], buttonAction4, FALLING);
  
  button[4][0] = 5;
  button[4][1] = 14;
  pinMode(button[4][1], INPUT_PULLUP);
  attachInterrupt(button[4][1], buttonAction5, FALLING);
 
}


void string_initialize() {
  msg_string =  "/";
  msg_string += String(device_id);
  msg_string += "/"; 
}


void setup() {
  start_time = 0;
  Serial.begin(74880);  
  delay(10);
  
  buttonInitialize();
  string_initialize();
  setup_wifi();
}

void loop() {
  if(current != -1) {
    // .. Serial.println("loop: current>-1");
    send_request();
  }

  if((millis() - start_time) >= end_time) {
    finish();
  }
}
