/*
 HC-SR04 Ping distance sensor]
 VCC to arduino 5v GND to arduino GND
 Echo to Arduino pin 13 Trig to Arduino pin 12
 Red POS to Arduino pin 11
 Green POS to Arduino pin 10
 560 ohm resistor to both LED NEG and GRD power rail
 More info at: http://goo.gl/kJ8Gl
 Original code improvements to the Ping sketch sourced from Trollmaker.com
 Some code and wiring inspired by http://en.wikiversity.org/wiki/User:Dstaub/robotcar
 */
//#include <Servo.h> 

//Servo servo1;
//Servo servo2;

/* Proximity sensor */
#define TRIG_PIN 13
#define ECHO_PIN 12

#define PROX_LED_CLOSE 8
#define PROX_LED_FAR 7

// Maximum value of proximity sensor
#define PROXIMITY_MAX 200

#define TEMPERATURE_PIN A0 
#define BRIGHTNESS_PIN A1
#define TWISTY_PIN A2

#define BRIGHTNESS_LED 9

#define PRINT_DELAY 500

// FUTURE INPUT VARIABLES
/*
int inputA=A5;
int inputB=A4;
int inputC=A3;
int inputD=2;
*/

int brightnessThreshold = 550;
long distanceThreshold = 4;

void setup() {
  //servo1.attach(11);
  //servo2.attach(10);
  Serial.begin (9600);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(PROX_LED_CLOSE, OUTPUT);
  pinMode(PROX_LED_FAR, OUTPUT);
  pinMode(BRIGHTNESS_LED, OUTPUT);
}

void loop() {
  long distance = calculateDistance();  
  int twisty = calculateTwistyTurnyThing();
  float temp = calculateTemperature();
  int brightness = calculateBrightness();

  setBrightnessLed(brightness);
  setProximityLeds(distance);

  if (Serial.available()) {
    Serial.read();
    printStatus(temp, brightness, twisty, distance);  
  }
  
  /* Printing interval */
  //delay(PRINT_DELAY);
}

void setBrightnessLed(int brightness) {
  if(brightness < brightnessThreshold){
    digitalWrite(BRIGHTNESS_LED, HIGH);
  } else {
    digitalWrite(BRIGHTNESS_LED, LOW);
  }  
}

void setProximityLeds(long distance) {
  if (distance < distanceThreshold) {
    digitalWrite(PROX_LED_CLOSE,HIGH);
    digitalWrite(PROX_LED_FAR,LOW);
  } else {
    digitalWrite(PROX_LED_CLOSE,LOW);
    digitalWrite(PROX_LED_FAR,HIGH);
  }
  
}

int calculateBrightness() {
  return analogRead(BRIGHTNESS_PIN);
}

float calculateTemperature() {
  float val = analogRead(TEMPERATURE_PIN);
  return val/1024.0*500; 
}

long calculateDistance() {
  //TODO: what
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long duration = pulseIn(ECHO_PIN, HIGH);
  long distance = duration / 58.2;
  if (distance > PROXIMITY_MAX) {
    return PROXIMITY_MAX;
  } else {
    return distance;
  }
}

int calculateTwistyTurnyThing() {
  return analogRead(TWISTY_PIN);
}

void printStatus(float temp, int brightness, int twisty, long distance)
{
  /* Temperature */
  
  Serial.print("{");
  Serial.print("TEMPERATURE: ");
  Serial.print(temp);
  Serial.print(",");
  /* Brightness */
  
  Serial.print("BRIGHTNESS: ");
  Serial.print(brightness);
  Serial.print(",");
  /* Turny twisty thing */
  
  Serial.print("TWISTY: ");
  Serial.print(twisty);
  Serial.print(",");
  /* Proximity */
  
  Serial.print("DISTANCE: ");
  Serial.print(distance);
  Serial.print("}\n");
}

