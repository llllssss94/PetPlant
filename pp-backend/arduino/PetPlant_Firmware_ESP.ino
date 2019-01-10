/**
   Konkuk University CSE
   2018 Sem 2 Graduation Project.
   copyright 2018 PetPlant. All rights reserved.
*/

/**
   ESP8266 01 Firmware code.
   ------
   ESP8266 WIFI Module.
   Connection with Arduino Nano Board.
*/

/**
   ESP8266 WiFi.h : ESP Library.
   WiFiUdp.h : UDP Module header in IDE.
   Wire.h
   SPI.h
*/
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <Wire.h>
#include <SPI.h>

/**
   Setting the Unique Machine Serial Number.
*/
#define MachineSerial "KCG20130001"

/**
   Wifi SSID, Password Value.
*/
const String SHOWCommand = String("AT");
// const String SSIDCommand = String("AT+SSID");
// const String PASSWDCommand = String("AT+PASSWD");
const String ENDCommand = String ("AT+END");

const String CommnadLine = "(" + SHOWCommand + ", " + ENDCommand + ")";

bool isConnect = false;
int count = 0;
String mSSID = "PetPlant";
String mPASSWD = "12345678";

const char* target_ip = "117.16.136.73";

WiFiUDP Udp;
unsigned int localUdpPort = 4210;  // local port to listen on
char incomingPacket[255];  // buffer for incoming packets
//char*  replyPacket;  // a reply string to send back

/**
   Send data.
*/
String sendData = "";
String parseData = "::";

void printSettingValue() {
  Serial.println("Current Setting Value");
  Serial.println("SSID : " + mSSID);
  Serial.println("SSID's Passwd : " + mPASSWD);
}

void setup() {
  Serial.begin(115200);
}

void loop() {
  isConnect = true;

  WiFi.begin(mSSID.c_str(), mPASSWD.c_str());
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
    count++;
    if (count > 10) {
      count = 0;
      isConnect = false;
      Serial.println("\nFail");
      break;
    }
  }
  if (isConnect) {
    Serial.println("CO");
    //delay(500);
    Udp.begin(localUdpPort);
  }

  /**
     init data.
  */
  
  if(Serial.available()) {
    sendData = Serial.readString();
    sendData = MachineSerial + parseData + sendData;
    //Serial.print("sendData : ");
    //Serial.println(sendData);

    char *cstrSendData = new char[sendData.length() + 1];
    strcpy(cstrSendData, sendData.c_str());
    //Serial.print("cstrSendData : ");
    //Serial.println(cstrSendData);

  /**
     UDP Packet send test message.
  */
    Serial.printf("Now listening at IP %s, UDP port %d,  SEND UDP PACKET FUNCTION.\n", WiFi.localIP().toString().c_str(), localUdpPort);

  /**
     Packet send module.
  */
    Udp.beginPacket(target_ip, 8080);
    Udp.write(cstrSendData);
    Udp.endPacket();
  }
  delay(100);
}
