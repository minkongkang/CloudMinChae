#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h> // change to #include <WiFi101.h> for MKR1000

#include "arduino_secrets.h"

//#define LED_1_PIN 5
#define LED_1_PIN 3
#define LED_2_PIN 4
#define LED_3_PIN 5


#include <ArduinoJson.h>
#include "Led.h"

/////// Enter your sensitive data in arduino_secrets.h
const char ssid[]        = SECRET_SSID;
const char pass[]        = SECRET_PASS;
const char broker[]      = SECRET_BROKER;
const char* certificate  = SECRET_CERTIFICATE;

WiFiClient    wifiClient;            // Used for the TCP socket connection
BearSSLClient sslClient(wifiClient); // Used for SSL/TLS connection, integrates with ECC508
MqttClient    mqttClient(sslClient);

unsigned long lastMillis = 0;


//Led객체를 가져와서 
//Led led1(LED_1_PIN);
Led LED_G(LED_1_PIN);// 초록:1단계
Led LED_Y(LED_2_PIN);// 노랑:2단계
Led LED_R(LED_3_PIN);// 빨강:3단계



////////////////////////////////////////////////////////////////////채영
int dust_sensor_in = A0;   // 미세먼지 핀 번호
float dust_sensor_out = 100;  // 미세먼지 밖 초기값 설정 : 나쁨
int A = 8; // 선풍기 센서
int B = 9;
//int LED_G = 3; // 초록:1단계
//int LED_Y = 4; // 노랑:2단계
//int LED_R = 5; // 빨강:3단계

////상태저장
//String dust_in;
//String dust_out = "나쁨";
//
////선풍기상태
//String pan;

//상태저장
const char* dust_in;
const char* dust_out = "나쁨";

//선풍기상태
const char* pan;


//선풍기 세기 : led
const char* pan_speed_led = "0";

float dust_value_in = 0;  // 센서에서 입력 받은 미세먼지 값
float dustDensityug_in=0;  // ug/m^3 값을 계산
 
int sensor_led = 12;      // 미세먼지 센서 안에 있는 적외선 led 핀 번호
int sampling = 280;    // 적외선 led를 키고, 센서 값을 읽어 들여 미세먼지를 측정하는 샘플링 시간
int waiting = 40;    
float stop_time = 9680;   // 센서를 구동하지 않는 시간


////////////////////////////////////////////////////////////////////


void setup() {
  Serial.begin(115200);
  while (!Serial);
  
  if (!ECCX08.begin()) {
    Serial.println("No ECCX08 present!");
    while (1);
  }

  // Set a callback to get the current time
  // used to validate the servers certificate
  ArduinoBearSSL.onGetTime(getTime);

  // Set the ECCX08 slot to use for the private key
  // and the accompanying public certificate for it
  sslClient.setEccSlot(0, certificate);

  // Optional, set the client id used for MQTT,
  // each device that is connected to the broker
  // must have a unique client id. The MQTTClient will generate
  // a client id for you based on the millis() value if not set
  //
  // mqttClient.setId("clientId");

  // Set the message callback, this function is
  // called when the MQTTClient receives a message
  
  mqttClient.onMessage(onMessageReceived);



////////////////////////////////////////////////////////////////////채영
  pinMode(sensor_led,OUTPUT); // 미세먼지 적외선 led를 출력으로 설정
  pinMode(4,OUTPUT);
  
  pinMode(A,OUTPUT);
  pinMode(B,OUTPUT);
  
//  pinMode(LED_G,OUTPUT);
//  pinMode(LED_Y,OUTPUT);
//  pinMode(LED_R,OUTPUT);
////////////////////////////////////////////////////////////////////

}
///////////////////////////////////////////////////////////////////////
void loop() {
//  ///////////////////////////////////////////////

  digitalWrite(sensor_led, LOW);    // LED 켜기
  delayMicroseconds(sampling);   // 샘플링해주는 시간. 
  dust_value_in = analogRead(dust_sensor_in); // 센서 값 읽어오기
  delayMicroseconds(waiting);  // 너무 많은 데이터 입력을 피해주기 위해 잠시 멈춰주는 시간. 
  digitalWrite(sensor_led, HIGH); // LED 끄기
  delayMicroseconds(stop_time);   // LED 끄고 대기  
 
  dustDensityug_in = (0.17 * (dust_value_in * (5.0 / 1024)) - 0.1) * 1000;    // 미세먼지 값 계산
  Serial.print("Dust Density [ug/m3]: ");            // 시리얼 모니터에 미세먼지 값 출력
  Serial.print(dustDensityug_in);
  
  if(dustDensityug_in <= -96.0){ // 대기 중 미세먼지가 좋음 일 때
     dust_in = "좋음";
     Serial.print("   ");
     Serial.println("좋음");
  }else if(-96.0 < dustDensityug_in && dustDensityug_in <= -93.0){      // 대기 중 미세먼지가 보통 일 때 녹색 출력
     dust_in = "보통";
     Serial.print("   ");
     Serial.println("보통"); 
     //int b=2   
  }else if (-93.0 < dustDensityug_in && dustDensityug_in <= -91.0){    // 대기 중 미세먼지가 나쁨 일 때 노란색 출력
     dust_in = "나쁨";
     Serial.print("   ");
     Serial.println("나쁨");        
  }else{ // 대기 중 미세먼지가 매우 나쁨 일 때   
     dust_in = "매우나쁨";
     Serial.print("   ");
     Serial.println("매우나쁨");
  }

  //안 < 밖  : 뒤에서 바람나옴
  if(dust_sensor_in < dust_sensor_out){
    //팬모터가 A방향으로 회전 //뒤
    // pan="Back";
    if(dust_in == "좋음" &&dust_out == "나쁨"){ //2단계
      digitalWrite(A,LOW); //팬모터가 A방향으로 회전 //뒤
      digitalWrite(B,HIGH);
      pan="Back";
      LED_Y.on();
      LED_G.off();
      LED_R.off();
      //digitalWrite(LED_Y,HIGH);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
      pan_speed_led = "2";
      Serial.println("좋음-나쁨 2단계 : Y");
    }else if(dust_in == "좋음" &&dust_out == "매우나쁨"){// 3단계
      digitalWrite(A,LOW); //뒤
      digitalWrite(B,HIGH);
      pan="Back";
      LED_R.on();
      LED_Y.off();
      LED_G.off();
      //digitalWrite(LED_Y,LOW);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,HIGH);
      pan_speed_led = "3";
      Serial.println("좋음-매우나쁨 3단계 : R");
    }else if(dust_in == "보통" &&dust_out == "나쁨"){//1단계
      digitalWrite(A,LOW); //뒤
      digitalWrite(B,HIGH);
      pan="Back";
      LED_G.on();
      LED_Y.off();
      LED_R.off();
      //digitalWrite(LED_Y,LOW);
      //digitalWrite(LED_G,HIGH);
      //digitalWrite(LED_R,LOW);
      pan_speed_led = "1";
      Serial.println("보통-나쁨 1단계 : G");
    }else if(dust_in == "보통" &&dust_out == "매우나쁨"){//2단계
      digitalWrite(A,LOW); //뒤
      digitalWrite(B,HIGH);
      pan="Back";
      LED_Y.on();
      LED_G.off();
      LED_R.off();
      //digitalWrite(LED_Y,HIGH);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
      pan_speed_led = "2";
      Serial.println("보통-매우나쁨 2단계 : Y");
    }else{// 정지
      pan="Stop";
      pan_speed_led = "0";
      digitalWrite(A,HIGH); // 전체 정지
      digitalWrite(B,HIGH);
      LED_G.off();
      LED_Y.off();
      LED_R.off();
      //digitalWrite(LED_Y,LOW);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
    }
  }
  //안 > 밖  : 앞에서 바람나옴
  else if(dust_sensor_out < dust_sensor_in){
    //팬모터가 B방향으로 회전 //앞
    //pan="Front";
    if(dust_out == "좋음" &&dust_out == "나쁨"){ //2단계
      pan="Front";
      digitalWrite(A,HIGH);
      digitalWrite(B,LOW);
      LED_Y.on();
      LED_G.off();
      LED_R.off();
      //digitalWrite(LED_Y,HIGH);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
      pan_speed_led = "2";
      Serial.println("좋음-나쁨 2단계 : Y");
    }else if(dust_out == "좋음" &&dust_in == "매우나쁨"){// 3단계
      pan="Front";
      digitalWrite(A,HIGH);
      digitalWrite(B,LOW);
      LED_R.on();
      LED_Y.off();
      LED_G.off();
      //digitalWrite(LED_R,HIGH);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_Y,LOW);
      pan_speed_led = "3";
      Serial.println("좋음-매우나쁨 3단계 : R");
    }else if(dust_out == "보통" &&dust_in == "나쁨"){//1단계
      pan="Front";
      digitalWrite(A,HIGH);
      digitalWrite(B,LOW);
      LED_G.on();
      LED_Y.off();
      LED_R.off();
      //digitalWrite(LED_R,LOW);
      //digitalWrite(LED_G,HIGH);
      //digitalWrite(LED_Y,LOW);
      pan_speed_led = "1";
      Serial.println("보통-나쁨 1단계 : G");
    }else if(dust_out == "보통" &&dust_in == "매우나쁨"){//2단계
      pan="Front";
      digitalWrite(A,HIGH);
      digitalWrite(B,LOW);
      LED_Y.on();
      LED_G.off();
      LED_R.off();
      //digitalWrite(LED_Y,HIGH);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
      pan_speed_led = "2";
      Serial.println("보통-매우나쁨 2단계 : Y");
    }else{// 정지
      pan="Stop";
      pan_speed_led = "2";
      digitalWrite(A,HIGH); // 전체 정지
      digitalWrite(B,HIGH);
      LED_G.off();
      LED_Y.off();
      LED_R.off();
      //digitalWrite(LED_Y,LOW);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
    }
  }
  //안 == 밖 : 모터정지, led정지
  else{ 
      pan="Stop";
      pan_speed_led = "0";
      digitalWrite(A,HIGH); // 전체 정지
      digitalWrite(B,HIGH);
      LED_G.off();
      LED_Y.off();
      LED_R.off();
      //digitalWrite(LED_Y,LOW);
      //digitalWrite(LED_G,LOW);
      //digitalWrite(LED_R,LOW);
      
  }

  
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();
  }

  if (!mqttClient.connected()) {
    // MQTT client is disconnected, connect
    connectMQTT();
  }

  // poll for new MQTT messages and send keep alives
  mqttClient.poll();
  ///////////////아두이노 에서의 loop문//////////
  
//  // publish a message roughly every 5 seconds.
//  if (millis() - lastMillis > 5000) {
//    lastMillis = millis();
//    char payload[1024];
//   // sprintf(payload,"{\"state\":{\"reported\":{\"1)dust_in_s\":\"%s\",\"2)dust_out_s\":\"%s\",\"3)dust_in_f\":\"%0.2f\",\"4)dust_out_f\":\"%0.2f\",\"5)fan\":\"%s\",\"6)fan_led\":\"%s\"}}}",dust_in,dust_out,dustDensityug_in,dust_sensor_out,pan,pan_speed_led);
//    sprintf(payload,"{\"state\":{\"reported\":{\"dustsin\":\"%s\",\"dustsout\":\"%s\",\"dustfin\":\"%0.2f\",\"dustfout\":\"%0.2f\",\"fanled\":\"%s\",\"fan\":\"%s\"}}}",dust_in,dust_out,dustDensityug_in,dust_sensor_out,pan_speed_led,pan);
//    sendMessage(payload);
//    //delay(20000);
//  }
//  mqttClient.subscribe("$aws/things/MyMKRWiFi1010_2/shadow/update/delta ");
//  delay(20000);
//}
// publish a message roughly every 5 seconds.
  if (millis() - lastMillis > 5000) {
    lastMillis = millis();
    char payload[1024];
   // sprintf(payload,"{\"state\":{\"reported\":{\"1)dust_in_s\":\"%s\",\"2)dust_out_s\":\"%s\",\"3)dust_in_f\":\"%0.2f\",\"4)dust_out_f\":\"%0.2f\",\"5)fan\":\"%s\",\"6)fan_led\":\"%s\"}}}",dust_in,dust_out,dustDensityug_in,dust_sensor_out,pan,pan_speed_led);
    sprintf(payload,"{\"state\":{\"reported\":{\"dustsin\":\"%s\",\"dustsout\":\"%s\",\"dustfin\":\"%0.2f\",\"dustfout\":\"%0.2f\",\"fanled\":\"%s\",\"fan\":\"%s\"}}}",dust_in,dust_out,dustDensityug_in,dust_sensor_out,pan_speed_led,pan);
    sendMessage(payload);
    //delay(20000);
  }
  //mqttClient.subscribe("$aws/things/MyMKRWiFi1010_2/shadow/update/delta ");
  //delay(20000);
 // mqttClient.subscribe("$aws/things/MyMKRWiFi1010_2/shadow/update/accepted ");
  delay(8000);
}

/////////////////////////////////////////////////////////////////////////

unsigned long getTime() {
  // get the current time from the WiFi module  
  return WiFi.getTime();
}

void connectWiFi() {
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

void connectMQTT() {
  Serial.print("Attempting to MQTT broker: ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883)) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // subscribe to a topic
  //mqttClient.subscribe("$aws/things/MyMKRWiFi1010_2/shadow/update/accepted ");
  mqttClient.subscribe("$aws/things/MyMKRWiFi1010_2/shadow/update/delta");
}



// 센서 값들 가져와서 paylod에 넣는 함수 
void getDeviceStatus(char* payload) {
//  // Read temperature as Celsius (the default)
//  float t = dht.readTemperature();
//
//  // Read led status
//  const char* led = (led1.getState() == LED_ON)? "ON" : "OFF";

 ////////////////////////////////////////////////////////////////////
  // make payload for the device update topic ($aws/things/MyMKRWiFi1010/shadow/update)
  //sprintf(payload,"{\"state\":{\"reported\":{\"temperature1\":\"%0.2f\",\"temperature2\":\"%0.2f\",\"LED\":\"%s\"}}}",t,20.1,led);
//  sprintf(payload,"{\"state\":{\"reported\":{\"1)dust_in_s\":\"%s\",\"2)dust_out_s\":\"%s\",\"3)dust_in_f\":\"%0.2f\",\"4)dust_out_f\":\"%0.2f\",\"5)fan\":\"%s\",\"6)fan_led\":\"%d\"}}}",dust_in,dust_out,dustDensityug_in,dust_sensor_out,pan,pan_speed_led);

}

//aws에 paylod메세지 보내는 함수
void sendMessage(char* payload) {
  char TOPIC_NAME[]= "$aws/things/MyMKRWiFi1010_2/shadow/update";
  
  Serial.print("Publishing send message:");
  Serial.println(payload);
  mqttClient.beginMessage(TOPIC_NAME);
  mqttClient.print(payload);
  mqttClient.endMessage();
}


void onMessageReceived(int messageSize) {
  // we received a message, print out the topic and contents
  //위에서 delta를 구독 했기 때문에 delta로 메세지를 전송하면 받음. 
  
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // store the message received to the buffer
  //버퍼를0부터 버퍼의 크기만큼 하나 씩 저장함. mqtt에서 보낸 메세지를?
  char buffer[1024] ;
  int count=0;
  while (mqttClient.available()) {
     buffer[count++] = (char)mqttClient.read();
  }
  buffer[count]='\0'; // 버퍼의 마지막에 null 캐릭터 삽입
  Serial.println(buffer);
  Serial.println();

  // JSon 형식의 문자열인 buffer를 파싱하여 필요한 값을 얻어옴.
  // 디바이스가 구독한 토픽이 $aws/things/MyMKRWiFi1010/shadow/update/delta 이므로,
  // JSon 문자열 형식은 다음과 같다.
  // {
  //    "version":391,
  //    "timestamp":1572784097,
  //    "state":{
  //        "LED":"ON"
  //    },
  //    "metadata":{
  //        "LED":{
  //          "timestamp":15727840
  //         }
  //    }
  // }
  //


  //제이슨 형식으로 받아온 정보 중 state에서 led를 읽어옴. 
  DynamicJsonDocument doc(1024);
  deserializeJson(doc, buffer);
  JsonObject root = doc.as<JsonObject>();
  JsonObject state = root["state"];
//  if(state["fanled"]=='\0' && state["fan"]=='\0'){
//    pan_speed_led = pan_speed_led;
//    pan = pan;
//  } else if(state["fanled"]=='\0' && state["fan"]!='\0'){
//    pan_speed_led = pan_speed_led;
//    pan = state["fan"];
//  }else if(state["fanled"]!='\0' && state["fan"]=='\0'){
//    pan_speed_led = state["fanled"];
//    pan = pan;
//  }else{
//    pan_speed_led = state["fanled"];
//    pan = state["fan"];
//  }
  //Serial.println(led);
  const char* pan_speed_led1 = state["fanled"];
  const char* pan1 = state["fan"];
//  
  char payload[1024];
// 읽어온 값이 on이면 paylod에 올리고 led 켬. 
//  if (strcmp(led,"ON")==0) {
//    led1.on();
//    sprintf(payload,"{\"state\":{\"reported\":{\"LED\":\"%s\"}}}","ON");
//    sendMessage(payload);
//    
//  } else if (strcmp(led,"OFF")==0) {
//    led1.off();
//    sprintf(payload,"{\"state\":{\"reported\":{\"LED\":\"%s\"}}}","OFF");
//    sendMessage(payload);
//  }


  if (strcmp(pan_speed_led1,"1")==0) {
    pan_speed_led=pan_speed_led1;
    LED_G.on();
    LED_Y.off();
    LED_R.off();
    sprintf(payload,"{\"state\":{\"reported\":{\"fanled\":\"%d\"}}}",1);
    sendMessage(payload);    
  } else if (strcmp(pan_speed_led1,"2")==0) {
    pan_speed_led=pan_speed_led1;
    LED_G.off();
    LED_Y.on();
    LED_R.off();
    sprintf(payload,"{\"state\":{\"reported\":{\"fanled\":\"%d\"}}}",2);
    sendMessage(payload);
  }else if (strcmp(pan_speed_led1,"3")==0) {
    pan_speed_led=pan_speed_led1;
    LED_G.off();
    LED_Y.off();
    LED_R.on();
    sprintf(payload,"{\"state\":{\"reported\":{\"fanled\":\"%d\"}}}",3);
    sendMessage(payload);
  }else if (strcmp(pan_speed_led1,"0")==0) {
    pan_speed_led=pan_speed_led1;
    LED_G.off();
    LED_Y.off();
    LED_R.off();
    sprintf(payload,"{\"state\":{\"reported\":{\"fanled\":\"%d\"}}}",0);
    sendMessage(payload);
  }else{
    pan_speed_led=pan_speed_led;
  }
  if (strcmp(pan1,"Back")==0) {
    pan=pan1;
    digitalWrite(A,LOW); //뒤
    digitalWrite(B,HIGH);
    sprintf(payload,"{\"state\":{\"reported\":{\"fan\":\"%s\"}}}","Back");
    sendMessage(payload);
  }else if (strcmp(pan1,"Front")==0) {
    pan=pan1;
    digitalWrite(A,HIGH); //앞
    digitalWrite(B,LOW);
    sprintf(payload,"{\"state\":{\"reported\":{\"fan\":\"%s\"}}}","Front");
    sendMessage(payload);
  }else if (strcmp(pan1,"Stop")==0) {
    pan=pan1;
    digitalWrite(A,HIGH); // 전체 정지
    digitalWrite(B,HIGH);
    sprintf(payload,"{\"state\":{\"reported\":{\"fan\":\"%s\"}}}","Stop");
    sendMessage(payload);
  }else{
    pan=pan;
  }
 
}
