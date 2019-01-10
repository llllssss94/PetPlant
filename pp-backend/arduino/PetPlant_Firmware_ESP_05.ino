/*
 * ESP 01에 탑재될 코드
 */

#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <Wire.h>
#include <SPI.h>

#define MachineSerial "KCG20130001"

const String SHOWCommand = String("AT");
const String SSIDCommand = String("AT+SSID");
const String PASSWDCommand = String("AT+PASSWD");
const String ENDCommand = String ("AT+END");

const String CommnadLine = "(" + SHOWCommand + "," + SSIDCommand + ", " + PASSWDCommand + ", " + ENDCommand + ")";

bool isConnect = false;
int count = 0;
String mSSID, mPASSWD;

const char* target_ip = "117.16.136.73";

WiFiUDP Udp;
unsigned int localUdpPort = 4210;  // local port to listen on
char incomingPacket[255];  // buffer for incoming packets
//char*  replyPacket;  // a reply string to send back

/**
 * Send data.
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
  if (!isConnect) {
    if (Serial.available()) {
      String str = Serial.readString();
      if (str.equals(SHOWCommand)) {
        Serial.println(str);
        Serial.println("OK\n");
        Serial.println("\nInput Command" + CommnadLine + "\n");
      } else if (str.equals(SSIDCommand)) {
        Serial.println(str);
        Serial.println("Input New SSID!");
        while (1) {
          if (Serial.available()) {
            mSSID = Serial.readString();
            Serial.print("New SSID is '");
            Serial.print(mSSID);
            Serial.println("'");
            Serial.println("\nInput Command" + CommnadLine + "\n");
            break;
          }
        }
      } else if (str.equals(PASSWDCommand)) {
        Serial.println(str);
        Serial.println("Input New SSID's Passwd!");
        while (1) {
          if (Serial.available()) {
            mPASSWD = Serial.readString();
            Serial.print("New SSID's Passwd is '");
            Serial.print(mPASSWD);
            Serial.println("'");
            Serial.println("\nInput Command" + CommnadLine + "\n");
            break;
          }
        }
      } else if (str.equals(ENDCommand)) {
        isConnect = true;

        WiFi.begin(mSSID.c_str(), mPASSWD.c_str());
        while (WiFi.status() != WL_CONNECTED)
        {
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
          delay(500);
          Udp.begin(localUdpPort);
        }
      } else {
        Serial.println(str);
        Serial.print("'");
        Serial.print(str);
        Serial.print("'");
        Serial.println("is not Command" + CommnadLine);
      }
    }
  } else {
    /**
     * init data.
     */
    
    sendData = Serial.readString();
    sendData = MachineSerial + parseData + sendData;
    Serial.print("sendData : ");
    Serial.println(sendData);

    char *cstrSendData = new char[sendData.length() + 1];
    strcpy(cstrSendData, sendData.c_str());
    Serial.print("cstrSendData : ");
    Serial.println(cstrSendData);
    
    /**
       UDP SEND TEST MODULE.
    */

    Serial.printf("Now listening at IP %s, UDP port %d,  SEND UDP PACKET FUNCTION.\n", WiFi.localIP().toString().c_str(), localUdpPort);

    Udp.beginPacket(target_ip, 8080);
    Udp.write(cstrSendData);
    Udp.endPacket();
    delay(5000);

    if (Serial.available()) {
      String str = Serial.readString();
      if (str.equals(SHOWCommand)) {
        isConnect = false;
        Serial.println(str);
        Serial.println("OK\n");
        printSettingValue();
        Serial.println("\nInput Command" + CommnadLine + "\n");
      }
    }
  }
