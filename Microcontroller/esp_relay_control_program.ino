
// ************************** Relay program **********************************//

#include <ESP8266WiFi.h>
#include <PubSubClient.h>


const char* wifi_ssid = "pancham";
const char* wifi_pwd = "9820900119";


//const char* esp_ssid = "";
//const char* esp_pwd = "";

//const char* a_wifi_ssid = "";
//const char* a_wifi_pwd = "";


const uint32_t device_id = 501204;
const char client_id[] = "0001";

const char* mqtt_server = "m10.cloudmqtt.com";
const long mqtt_port = 16583;
const char* mqtt_user_id = "bnbqhbne";
const char* mqtt_user_pwd = "Q64GD7kAtlDv";
const bool retain = false;

String will_msg_string;
char will_msg[50] = ""; 
const int will_qos = 1;

char topic[20];
String topic_string = "";
String subtopic = "esp1"; 
const int sub_qos = 1;

char msg[50];
String msg_string = ""; // contains message to compare received message
 // received message for server
//String receive_message_string;


String connect_message_string = "";
char connect_message[50] = "";

String ack_message_string = "";
char ack_message[50] = "";


WiFiClient espClient;
PubSubClient client(espClient);

boolean relay_state = true; // relay is active low & false -> HIGH, true -> LOW
int pin = 12;
int led_pin = 13;


// ********************************  MQTT Functions *************************//

// callback function for receiving messages
void callback(char* rtopic, byte* payload, unsigned int length) {

  String temp_message = "";
  char receive_message[9];
  
  Serial.print("Message arrived [");
  Serial.print(rtopic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    receive_message[i] = (char)payload[i];
    Serial.print((char)payload[i]);
  }

  temp_message = String(receive_message);
  Serial.println(); 
  
  if(temp_message == msg_string) {
    
     if(relay_state == true){
        digitalWrite(pin, HIGH); // relay inactive when pin HIGH
        digitalWrite(led_pin, LOW);
        relay_state = false;
        Serial.println("Relay off");
        client.publish(topic, ack_message, retain);
      } else {
        digitalWrite(pin, LOW); // relay active when pin LOW
        digitalWrite(led_pin, HIGH);
        relay_state = true;
        Serial.println("Relay on");
        client.publish(topic, ack_message, retain);
     }
     
  } else if(temp_message == ack_message_string){
     //Serial.println("Received acknowledgement");
  
  } else {
    Serial.println("string not match");
  }   
}




//************************* Connection Function **********************************//

// Connect to WiFi
void setup_wifi() {
  int retry = 20;
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(wifi_ssid);

  WiFi.begin(wifi_ssid, wifi_pwd);
  Serial.print("WiFi Connecting..");
  delay(500);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
    if(retry-- == 0)
      while(1);
  }

  Serial.println("Connected");
  Serial.println("");

}



// Connect or Reconnect to MQTT Broker
void reconnect() {
  int retries = 10;

  // clean up before reconnect()
  client.unsubscribe(topic);
  client.disconnect();
  
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
   
    // Attempt to connect
    if (client.connect(client_id, mqtt_user_id, mqtt_user_pwd, topic, will_qos, retain, will_msg)) {
      Serial.println("connected");
      
      // subscribe after connect
      if(client.subscribe(topic, sub_qos)) {
        // Once connected, publish an announcement...
        client.publish(topic, connect_message, retain);
      } else {
        Serial.println("Subscribe Failed. Retrying..");
        client.unsubscribe(topic);
        client.disconnect();
      } 
    } else {
      Serial.println(" Connection Failed");
      Serial.println(" Trying Again...");
      client.unsubscribe(topic);
      client.disconnect();
      delay(500);
      
      if(retries--==0){
        Serial.println(" Waiting to die...");
        while(1);
      }
      
    }
  }
}




//****************************** Initialization Functions ************************//

// Setup the pin connected to relay
void pin_initialize(){
  digitalWrite(pin, HIGH);
  digitalWrite(led_pin, LOW);
  pinMode(led_pin, OUTPUT);
  pinMode(pin, OUTPUT);
}


// Setup messages to send at various stages of connection
void string_initialize() {
 
  // message to compare
  msg_string += "/t/" + String(device_id); 
  msg_string.toCharArray(msg, msg_string.length()+1);
  Serial.print("message to send = ");
  Serial.println(msg_string);


  // generate topic to publish, subscribe and send will msg
  topic_string += "/" + String(client_id) + "/" + subtopic;
  topic_string.toCharArray(topic, topic_string.length()+1);

  
  // message to send on connection successful
  connect_message_string += "/c/" + String(device_id);
  connect_message_string.toCharArray(connect_message, connect_message_string.length()+1);


  // generate message for will
  will_msg_string = "/w/" + String(device_id); 
  will_msg_string.toCharArray(will_msg, will_msg_string.length()+1);


  //generate acknowledgement message
  ack_message_string = "/a/" + String(device_id);
  ack_message_string.toCharArray(ack_message, ack_message_string.length() + 1);
  
}


//*************************** Arduino Functions *******************************//


void setup() {
  Serial.begin(74880);
  delay(10);

  pin_initialize();
  setup_wifi();
  
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
  
  string_initialize();
}


void loop() {

  if (!client.connected()) {
    reconnect();
  }
  client.loop();
}

//************************************ END ***************************************//
