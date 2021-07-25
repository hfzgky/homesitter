#include <SoftwareSerial.h>

int Tx = 6; 
int Rx = 7; 

SoftwareSerial BtSerial(Tx,Rx);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  Serial.println("hello");
  BtSerial.begin(9600);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (BtSerial.available()) {       
    Serial.write(BtSerial.read());
  }
  if (Serial.available()) {         
    BtSerial.write(Serial.read());
  }


}
