#include <MFRC522.h>
#include <SPI.h>
#include <Servo.h>

int ledPin = 7;

Servo door;

byte masterCard[4];

#define SS_PIN 10
#define RST_PIN 9

MFRC522 mfrc522(SS_PIN, RST_PIN);

void setup()
{
  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();
  
  masterCard[0] = 222;
  masterCard[1] = 24;
  masterCard[2] = 128;
  masterCard[3] = 67;
  
  pinMode (ledPin, OUTPUT);
  
  door.attach(3, 500, 2400);
  door.write(180);
  delay(1000);
}

void loop()
{
  if (Serial.available()) {
    Serial.println(F("change"));
    ChangeMaster(Serial.read() - '0');
  }
  
  else {
    CheckAndOpen();
  }
}

void ChangeMaster (int key)
{
  key = key + 48;
  
  Serial.write(key);
  Serial.println();
  
  switch (key)
  {
    case '0':
      masterCard[0] = 222;
      masterCard[1] = 24;
      masterCard[2] = 128;
      masterCard[3] = 67;
      break;
    case '1':
      masterCard[0] = 148;
      masterCard[1] = 7;
      masterCard[2] = 247;
      masterCard[3] = 184;
      break;
    case '2':
      masterCard[0] = 194;
      masterCard[1] = 104;
      masterCard[2] = 108;
      masterCard[3] = 169;
      break;
    case '3':
      masterCard[0] = 132;
      masterCard[1] = 152;
      masterCard[2] = 104;
      masterCard[3] = 184;
      break;
    case '4':
      masterCard[0] = 162;
      masterCard[1] = 33;
      masterCard[2] = 213;
      masterCard[3] = 253;
      break;
    default:
      masterCard[0] = 222;
      masterCard[1] = 24;
      masterCard[2] = 128;
      masterCard[3] = 67;
  }
}

void CheckAndOpen()
{
  if ( ! mfrc522.PICC_IsNewCardPresent())
    return;
  
  if ( ! mfrc522.PICC_ReadCardSerial())
    return;

  if (mfrc522.uid.uidByte[0] == 222 && 
    mfrc522.uid.uidByte[1] == 24 && 
    mfrc522.uid.uidByte[2] == 128 && 
    mfrc522.uid.uidByte[3] == 67 ) {

    Serial.println(F("Correct"));
    door.write(0);
    delay(3000);
    door.write(180);
  }

  else if (mfrc522.uid.uidByte[0] == masterCard[0] && 
    mfrc522.uid.uidByte[1] == masterCard[1] && 
    mfrc522.uid.uidByte[2] == masterCard[2] && 
    mfrc522.uid.uidByte[3] == masterCard[3] ) {
      
      Serial.println(F("Correct"));
      door.write(0);
      delay(3000);
      door.write(180);
  }
  
  else {
    
    Serial.println(F("Wrong"));
    /* door.write(90);
    delay(3000);
    door.write(180); */
    
    for (int i = 0; i < 10; i++) {
      digitalWrite (ledPin, HIGH);
      delay(75);
      digitalWrite (ledPin, LOW);
      delay(75);
    }
  }
  delay(1000);
}
