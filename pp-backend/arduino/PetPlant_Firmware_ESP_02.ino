/*
   아두이노에 탑재될 코드
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


//가상 시리얼 설정해줌.
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
}

void loop() {
  /**
         Using MH-Sensor Series Flying-Fish
         Arduino Nano board.
         Connected A0 PIN.
  */
  Serial.println("loop");
  
  int illuminance = analogRead(A0);
  String illuminanceData = String(illuminance);
  Serial.println(illuminanceData);
  
  /**
     Using BME280 Sensor.
     Arduino Nano board.
  */
  int temperature = bme.readTemperature();
  String temperatureData = String(temperature);
  Serial.println(temperatureData);


  String data = illuminanceData + parseData + temperatureData;

  Serial.println(data);
  //ESPserial.print(data);
  
  // put your main code here, to run repeatedly:
  if (ESPserial.available()) {
    Serial.println(ESPserial.readString());
  }
  if (Serial.available()) {
    ESPserial.print(Serial.readString());
  }

  delay(400);
}
