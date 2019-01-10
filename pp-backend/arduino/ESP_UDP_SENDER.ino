/**
 * PetPlant Project
 * copyright 2018 Konkuk University CSE 
 * UDP Sender Pseudo Code.
 */

#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

#include <Wire.h>
#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>

#define BMP_SCK 13
#define BMP_MISO 12
#define BMP_MOSI 11 
#define BMP_CS 10

Adafruit_BMP280 bme; // I2C
//Adafruit_BMP280 bme(BMP_CS); // hardware SPI
//Adafruit_BMP280 bme(BMP_CS, BMP_MOSI, BMP_MISO,  BMP_SCK);

/**
 * SSID, PASSWORD, SERVER_IP_ADDRESS.
 */
const char* ssid = "petra";
const char* password = "17352332";
const char* target_ip = "117.16.136.73";

WiFiUDP Udp;
unsigned int localUdpPort = 4210;  // local port to listen on
char incomingPacket[255];  // buffer for incoming packets
char  replyPacket[] = "Pet Plant Project :) ";  // a reply string to send back


void setup()
{
  Serial.begin(115200);
  Serial.println();

  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");

  Udp.begin(localUdpPort);
  // Serial.printf("Now listening at IP %s, UDP port %d\n", WiFi.localIP().toString().c_str(), localUdpPort);
}


void loop()
{
  /**
   * Using MH-Sensor Series Flying-Fish
   * Arduino Nano board.
   * Connected A0 PIN.
   */
  int illuminance = analogRead(A0);

  /**
   * Using BME280 Sensor.
   * Arduino Nano board.
   */
  int temperature = bme.readTemperature();
  
  Serial.printf("Now listening at IP %s, UDP port %d,  SEND UDP PACKET FUNCTION.\n", WiFi.localIP().toString().c_str(), localUdpPort);

  /**
   * UDP SEND TEST MODULE.
   */
  Udp.beginPacket(target_ip, 8080);
  Udp.write(replyPacket);
  Udp.endPacket();
  delay(2000);
}
