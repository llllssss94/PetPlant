/**
 * Konkuk University CSE 
 * 2018 Sem 2 Graduation Project.
 * copyright 2018 PetPlant. All rights reserved.
 */

/**
 * Arduino Nano Firmware code.
 * ------
 * Arduino Nano Baord.
 */

/**
 * Wire.h
 * SPI.h
 * SoftwareSerial.h : Connection with ESP8266-01 Module.
 * Adafruit_Sensor.h : Sensor header.
 * Adafruit_BMP280.h : BMP280 header.
 */

#include <Wire.h>
#include <SPI.h>
#include <SoftwareSerial.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>


#define EEP_DATA_LEN 100

#define BMP_SCK 13
#define BMP_MISO 12
#define BMP_MOSI 11
#define BMP_CS 10

Adafruit_BMP280 bme; // I2C
//Adafruit_BMP280 bme(BMP_CS); // hardware SPI
//Adafruit_BMP280 bme(BMP_CS, BMP_MOSI, BMP_MISO,  BMP_SCK);


/**
 * Set virtual serial.
 */
SoftwareSerial ESPserial(2, 3); //ESP의 TX를 아두이노 D2에, RX를 아두이노 D3에 꽂음됨

String parseData = "::";

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  ESPserial.begin(115200);

  if (!bme.begin()) {  
    Serial.println("Could not find a valid BMP280 sensor, check wiring!");
    while (1);
  }

  Serial.println("");
  Serial.println("Remember to to set Both NL&CR in the serial monitor.");
  Serial.println("Ready");
  Serial.println("");

  while(1){
    if(Serial.available()){
      ESPserial.print(Serial.readString());
    }
    if(ESPserial.available()){
      String reading = ESPserial.readString();
      Serial.print(reading);
      if(reading.indexOf("CO")!=-1){
        break;      
      }
    }
  }
}

void loop() {
  /**
   * Using SEN
   * ------
   * Arduino Nano board.
   * Connected A2 Pin.
   */
  int moisture = analogRead(A2);
  if(moisture>700){
    moisture = 700;
  }
  moisture = moisture / 7;
  String moistureData = String(moisture);

  /**
   * Using MH-Sensor Series Flying-Fish
   * Arduino Nano board.
   * Connected A0 Pin.
   */
  int illuminance = (1023 - analogRead(A0)) / 10.23;
  String illuminanceData = String(illuminance);

  /**
   * Using BMP280 Sensor.
   * Arduino Nano board.
   */
  int temperature = bme.readTemperature();
  String temperatureData = String(temperature);

  String data = ""; 
  data = moistureData + parseData + illuminanceData + parseData + temperatureData;

  ESPserial.flush();
  ESPserial.print(data);
  
  delay(4000);
}
