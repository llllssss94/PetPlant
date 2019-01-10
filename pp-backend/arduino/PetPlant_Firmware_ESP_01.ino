/*
 * ESP 01에 탑재될 코드
 */

#include <EEPROM.h>
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <Wire.h>
#include <SPI.h>

#define EEP_DATA_LEN 100

const String SHOWCommand = String("AT");
const String SSIDCommand = String("AT+SSID");
const String PASSWDCommand = String("AT+PASSWD");
const String BROKERCommand = String("AT+BROKER");
const String ENDCommand = String ("AT+END");

const String NAMECommand = String("AT+NAME");

const String CommnadLine = "(" + SHOWCommand + "," + SSIDCommand + ", " + PASSWDCommand + ", " + BROKERCommand + ", " + ENDCommand + ")";

bool isConnect = true;
int count = 0;
String mSSID, mPASSWD, mBROKER, mNAME;

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

byte mac[6];

void EEPread() {
  int startAt, endAt;
  String readData = "";
  char temp;
  for (int i = 0; i < EEP_DATA_LEN; ++i)
  {
    temp = char(EEPROM.read(i));
    if (temp == '!')
      break;
    readData += temp;
  }

  endAt = readData.indexOf('/', 0);
  mSSID = readData.substring(0, endAt);

  startAt = endAt + 1;
  endAt = readData.indexOf('/', startAt);
  mPASSWD = readData.substring(startAt, endAt);

  startAt = endAt + 1;
  endAt = readData.indexOf('/', startAt);
  mBROKER = readData.substring(startAt, endAt);

  startAt = endAt + 1;
  endAt = readData.indexOf('/', startAt);
  mNAME = readData.substring(startAt, endAt);
  
}

void EEPwrite() {
  String writeData = mSSID + "/" + mPASSWD + "/" + mBROKER + "!";

  for (int i = 0; i < EEP_DATA_LEN; ++i) {
    EEPROM.write(i, 0); //clearing
  }
  EEPROM.commit();  //esp8266

  for (int i = 0; i < writeData.length(); ++i)
  {
    EEPROM.write(i, writeData[i]);
  }

  EEPROM.commit();  //esp8266
}

void printSettingValue() {
  Serial.println("Current Setting Value");
  Serial.println("SSID : " + mSSID);
  Serial.println("SSID's Passwd : " + mPASSWD);
  Serial.println("Broker IP : " + mBROKER);
  Serial.println("User Name : " + mName);
}

void setup() {
  Serial.begin(115200);
  EEPROM.begin(EEP_DATA_LEN);      //Arduino Board(ex.UNO) need this command.But other Board may not need this command
  EEPread();
  WiFi.begin(mSSID.c_str(), mPASSWD.c_str());
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
    count++;
    if (count > 10) {
      count = 0;
      isConnect = false;
      Serial.println("\nConnection Failed \nInput Command" + CommnadLine + "\n");
      break;
    }
  }
  if (isConnect) {
    Serial.println(" connected");
    Udp.begin(localUdpPort);
  }
}

void loop() {
  if (!isConnect) {
    if (Serial.available()) {
      String str = Serial.readString();
      if (str.equals(SHOWCommand)) {
        Serial.println(str);
        Serial.println("OK\n");
        printSettingValue();
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
            EEPwrite();
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
            EEPwrite();
            Serial.println("\nInput Command" + CommnadLine + "\n");
            break;
          }
        }
      } else if (str.equals(BROKERCommand)) {
        Serial.println(str);
        Serial.println("Input New Broker IP!");
        while (1) {
          if (Serial.available()) {
            mBROKER = Serial.readString();
            Serial.print("New Broker IP is '");
            Serial.print(mBROKER);
            Serial.println("'");
            EEPwrite();
            Serial.println("\nInput Command" + CommnadLine + "\n");
            break;
          }
        }
      } else if (str.equals(ENDCommand)) {
        Serial.println(str);
        printSettingValue();
        Serial.print("\nSetting Value Saved and END!!\nStart Connecting!\n");
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
            Serial.println("\nConnection Failed \nInput Command" + CommnadLine + "\n");
            break;
          }
        }
        if (isConnect) {
          Serial.println(" connected");
          Udp.begin(localUdpPort);
        }
      } else if (str.equals(NAMECommand)){
        Serial.println(str);
        Serial.println("Input New Name!");
        while(1){
          if(Serial.available()){
            mNAME = Serial.readString();
            Serial.print("New Name is '");
            Serial.print(mNAME);
            Serial.println("'");
            EEPwrite();
            Serial.println("\nInput Command" + commandLine + "\n");
            break;
          }
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
    delay(4000);
  }
}
