/**
 * PetPlant Project
 * copyright 2018 Konkuk University CSE 
 * Light Sensor Pseudo Code.
 */

void setup() {
  Serial.begin(9600);
}

void loop() {
  /**
   * Using MH-Sensor Series Flying-Fish
   * Arduino Nano board.
   * Connected A0 PIN.
   */
  int lightSensorValue = analogRead(A0);  

  Serial.print("Light Value : ");
  Serial.println(lightSensorValue);

  delay(1);
}
