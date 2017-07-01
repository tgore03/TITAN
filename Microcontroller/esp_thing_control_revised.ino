
//********* ESP program for sticker connecting to MQTT Server **********//


#include <ESP8266WiFi.h>
#include <PubSubClient.h>


const char* wifi_ssid = "pancham";
const char* wifi_pwd = "9820900119";

//const char* esp_ssid = "";
//const char* esp_pwd = "";

//const char* a_wifi_ssid = "";
//const char* a_wifi_pwd = "";

const uint32_t device_id = 501202;
const char client_id[] = "0001";



const char* mqtt_server = "m10.cloudmqtt.com";
const long mqtt_port = 16583;
const char* mqtt_user_id = "bnbqhbne";
const char* mqtt_user_pwd = "Q64GD7kAtlDv";
const bool retain = false;

String will_msg_string;
char will_msg[50] = ""; 
const int will_qos = 1;

String connect_msg_string = "";
char connect_msg[50] = "";

char topic[20];
String topic_string="";
String subtopic = "esp1";
const int sub_qos = 1;

char msg[50];
String msg_string ="";



WiFiClient espClient;
PubSubClient client(espClient);



const int n = 5; // No. of Buttons
int button[n][2];

volatile long last_debounce_time = 0;
long debounce_delay = 200;
long systime = 10000;

int queue[10];
int start = 0;
int current = -1;



long start_time = 0;
long end_time = 20000;


// ********************************  MQTT Functions *************************//


void finish(){
  Serial.println(" finish start ");
  
  client.publish(topic, will_msg, retain);
  client.unsubscribe(topic);
  client.disconnect();

  Serial.println(millis());
  
  ESP.deepSleep(10000*10, WAKE_RFCAL);
}



// Add button Ids to queue to send on connection establishment.
void enqueue(int button_id){
  if(current < 14) {
    current++;
    Serial.print("Enqueue; current = ");
    Serial.println(current);
    queue[current] = button_id;
  }
  else {
    Serial.println("queue full to enqueue");
  }
}

// Remove button Ids to send the messages.
int dequeue(){
  int button_id;
  if(start <= current) {
    Serial.print("dequeue; start = ");
    Serial.println(start);
    button_id = queue[start++];
    if(start > current) {
      start = 0;
      current = -1;
    }
    return button_id;
  } else {
    Serial.println("No element to dequeue");
    return -1;
  }
}



/*
void publish_msg1(int add, int button_id){
  int i, j;
  Serial.print("publish count = ");
  Serial.println(publish_count);
  Serial.print("add = ");
  Serial.println(add);

  String temp="";
  temp = msg_string;
  temp += button_id; // later on use array to store button id of pending publish msg due to non connectivity.
  temp.toCharArray(msg, temp.length()+1);
  Serial.print("Message = ");
  Serial.println(msg);
  for(i=0;i<(publish_count + add);i++) {
    Serial.print("topic = ");
    Serial.println(topic);
    if(!client.connected()){
      Serial.println("lost connection before publish");
      //reconnect();
    } 
    Serial.print("publish status = ");
    j = client.publish(topic, msg, retain);
    Serial.println(j);
    if(!client.connected()) {
      Serial.println("lost connection after publish");
      i--;
    }
    delay(1000);
  }
  publish_count = 0;
  Serial.print("millis = ");
  Serial.println(millis());
  systime = millis();


  
  Serial.println(" publish_msg end ");
}

*/


void publish_msg() {

  int j, wait = 3;
  int button_id;
  String temp;
  temp = msg_string;

  while(start <= current) {
    
    button_id = dequeue();

    temp = msg_string;
    temp += String(button_id);
    temp.toCharArray(msg, temp.length()+1);
    
    if(!client.publish(topic, msg, retain)) {
      Serial.println(" publish failed. Retrying..");
      break;
    }
    
    delay(200);
  }
  end_time = millis() + 10000;
}



void publish_msg1(int add, int button_id) {

  int j;
  String temp;
  temp = msg_string;

  Serial.print("current= ");
  Serial.println(current);
  Serial.print("add = ");
  Serial.println(add);

  if(add != 0) {
    temp = msg_string;
    temp += button_id; 
    temp.toCharArray(msg, temp.length()+1);
    Serial.print("add: Message = ");
    Serial.println(msg);

    Serial.print("publish status = ");
    j = client.publish(topic, msg, retain);
    Serial.println(j);

    delay(500);

    if(!client.connected()) {
      Serial.println("lost connection after publish");
    }
    
  }

  while(start <= current) {   
     
    button_id = dequeue();
    temp = msg_string;
    temp += String(button_id); // later on use array to store button id of pending publish msg due to non connectivity.
    temp.toCharArray(msg, temp.length()+1);
    Serial.print("queue: Message = ");
    Serial.println(msg);
    
    Serial.print("publish status = ");
    j = client.publish(topic, msg, retain);
    Serial.println(j);

    delay(500);

    if(!client.connected()) {
      Serial.println("lost connection after publish");
    }
  }
  
  Serial.print("millis = ");
  Serial.println(millis());
  
  end_time = millis() + 10000;
}



void callback(char* rtopic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(rtopic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
}









void buttonAction1(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    Serial.println("1");
    
    enqueue(1);
  }
}


void buttonAction2(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    Serial.println("2");

    enqueue(2);
  }
}

void buttonAction3(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    Serial.println("3");

    enqueue(3);
  }
}

void buttonAction4(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    Serial.println("4");

    enqueue(4);
  }
}

void buttonAction5(){
  if((millis() - last_debounce_time) > debounce_delay) {
    last_debounce_time = millis();
    Serial.println("5");

    enqueue(5);
  }
}


//************************* Connection Function **********************************//



void setup_wifi() {
  int retry = 20;
  delay(10);
  
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(wifi_ssid);

  WiFi.begin(wifi_ssid, wifi_pwd);
  Serial.print("connecting..");
  
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
    if(retry-- == 0) {
      Serial.println("Not Connected");
      //finish();
      while(1);
    }
  }

  Serial.println("");
  Serial.println("WiFi connected");
}








void reconnect() {
  int retries = 3;

  // clean up before reconnect()
  client.unsubscribe(topic);
  client.disconnect();
  
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");

    if (client.connect(client_id, mqtt_user_id, mqtt_user_pwd, topic, will_qos, retain, will_msg)) {
      Serial.println("connected");
      
      if(client.subscribe(topic, sub_qos)) {
        client.publish(topic, connect_msg, retain);
      } else{
        retries--;
        Serial.println("Subscribe failed");
        reconnect();
      }
      
    } else {
      client.unsubscribe(topic);
      client.disconnect();
      
      Serial.println(" trying again in 1/2 seconds");
      delay(500);
      if(retries--==0){
        //finish();
        while(1);
      }
    }
  }
}




//****************************** Initialization Functions ************************//





void buttonInitialize(){
  Serial.println(" buttonInitialize start ");
  
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

  // print the button allocation table
  for(int i=0;i<n;i++){
    for(int j=0;j<2;j++){
      Serial.print(button[i][j]);
      Serial.print("  ");
    }
    Serial.println();
  }
}






void string_initialize() {

  // generate message to send
  msg_string += "/s";
  msg_string +=  "/";
  msg_string += String(device_id);
  msg_string += "/";  // Added Button id later in publish function

  // generate topic to publish, subscribe and send will msg
  topic_string += "/" + String(client_id) + "/" +subtopic;
  topic_string.toCharArray(topic, topic_string.length()+1);

  // generate message for will
  will_msg_string = "/w";
  will_msg_string += "/";
  will_msg_string += String(device_id);
  will_msg_string.toCharArray(will_msg, will_msg_string.length()+1);

  connect_msg_string += "/c";
  connect_msg_string += "/";
  connect_msg_string += String(device_id);
  connect_msg_string.toCharArray(connect_msg, connect_msg_string.length()+1);
}





//*************************** Arduino Functions *******************************//




void setup() {
  start_time = millis();
  Serial.begin(74880);
  delay(10);
  
  buttonInitialize();
  setup_wifi();
  
  client.setServer(mqtt_server, mqtt_port);
  //client.setCallback(callback);
  
  string_initialize();
}


void loop() {

  if (!client.connected()) {
    reconnect();
  }

  if(current != -1) {
    publish_msg();
  }
      
  client.loop();
  if((millis() - start_time) >= end_time)
    finish();
}

//************************************ END ***************************************//
