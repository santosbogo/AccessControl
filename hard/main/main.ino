//Libraries
  #include <Arduino.h>
  #include <Keypad.h>
  #include <SPI.h>
  #include <MFRC522.h>
  #include <LiquidCrystal_I2C.h>
  #include <WiFi.h>
  #include "time.h"
  #include "sntp.h"
  #include <PubSubClient.h>
  #include <ArduinoJson.h>


//Pins:
  //RFID Pins
  #define SS_PIN 25
  #define RST_PIN 33

  //Led Pins
  #define redLedPin 13
  #define greenLedPin 12

  //Button pin
  #define buttonPin 14

  //Door relay pin
  #define doorRelayPin 36  //In case we want to add a door relay

  //Keypad
  byte rowPins[4] = { 27, 5, 17, 16 };  // Keypad row pins
  byte colPins[4] = { 4, 0, 2, 15 };    // Keypad column pins


//Instances
  //Keypad
  char keys[4][4] = {
    { '1', '2', '3', 'A' },
    { '4', '5', '6', 'B' },
    { '7', '8', '9', 'C' },
    { '*', '0', '#', 'D' }
  };
  Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, 4, 4);

  //RFID
  MFRC522 mfrc522(SS_PIN, RST_PIN);

  //LCD
  LiquidCrystal_I2C lcd(0x27, 16, 2);

  //WIFI instance
  WiFiClient WIFI_CLIENT;

  //MQTT
  PubSubClient MQTT_CLIENT;


//Constants and variables
  //Constants
  #define DELAY 1000        //For not important or short messages
  #define THINK_DELAY 1500  //For important or long messages
  #define maxUsers 10       // Max capacity of users register

  //Variables
  bool interruptFlag = false;
  bool correctKey = false;
  bool isDefinitiveState = false;
  int state = 0;  //States: 0=Normal 1=Definitive Locked 2=Definitive Unlocked
  String users[maxUsers];
  int numUsers = 0;            // Actual number of users registred
  String adminkey = "123456";  //Deffault admin password

//MQTT instance
#define PUBLIC_IP "3.84.203.19"

void setup() {
  Serial.begin(9600);

  // Initialize keypad
  keypad.setDebounceTime(50);

  //Initialize LCD
  lcd.init();
  lcd.backlight();

  //Initialize RFID
  SPI.begin();
  mfrc522.PCD_Init();

  //Initialize Leds
  pinMode(greenLedPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);

  //Initialize Button
  pinMode(buttonPin, INPUT_PULLUP);

  // connectWifi("UA-Alumnos", "41umn05WLC");
  connectWifi("Flia Lando 2", "aabbccddeeff");
  // connectWifi("Fila Bogo 2.4", "244466666");

  //Set Time
  if (WiFi.status() == WL_CONNECTED) {
    getTimeFromWifi();
  } else {
    manualTimeConfiguration();
  }

  //Connect to MQTT server
  connectMQTT();

  LCDinitialMessage();
}

void loop() {
  char key = keypad.getKey();
  bool RFIDReaderCondition = mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial();
  String UID;

  buttonInterruption();

  checkMQTT();

  if (key) {
    if (key == '*') {
      interruptFlag = true;
      LCDinitialMessage();
    } else if (key >= '0' && key <= '9') {
      correctKey = false;  // Reset the flag if any key other than * is pressed
    }
  }

  if (interruptFlag) {
    // If the interrupt condition is met (the * key is pressed), request and verify the key
    correctKey = requestKey();
    interruptFlag = false;  // Reset the interrupt flag
    LCDinitialMessage();
  }

  if (correctKey) {
    char key = keypad.getKey();
    adminMenu();
    correctKey = false;  // Reset the correct key flag
    LCDinitialMessage();
  }

  if (RFIDReaderCondition) {
    UID = readUid(mfrc522.uid.uidByte);
    accessController(UID, true);
    LCDinitialMessage();
  }
}


//MQTT Methods


void connectMQTT() {
  const int maxAttempts = 16;
  int attempts = 0;
  MQTT_CLIENT.setServer(PUBLIC_IP, 1883);
  MQTT_CLIENT.setCallback(callback);
  MQTT_CLIENT.setClient(WIFI_CLIENT);

  lcd.setCursor(0, 1);
  lcd.print("                ");

  while (!MQTT_CLIENT.connected()) {
    if (MQTT_CLIENT.connect("ESP32Client")) {
      MQTT_CLIENT.subscribe("users");  //Subscribe to all topics
      MQTT_CLIENT.subscribe("state");
      MQTT_CLIENT.subscribe("#");
    }
    delay(DELAY * 6);
    lcd.print(".");
    attempts++;
    if (attempts >= maxAttempts) {
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("FAILED CONECTION");
      delay(DELAY);
    }
  }

  Serial.println("CONECTADO A MQTT");
}

void checkMQTT() {
  if (!MQTT_CLIENT.connected()) {
    LCDReconectMQTTMessage();
    connectMQTT();
    LCDinitialMessage();
  }
  MQTT_CLIENT.loop();
}

void publishJson(String message) {
  int length = message.length();

  char messageChar[length + 1];  // +1 for null terminator
  message.toCharArray(messageChar, length + 1);
  MQTT_CLIENT.publish("AccessControl/test", messageChar);
}

void accessRecordMQTTPublish(String UID, bool granted, bool fromOut) {
  String cardID = UID;

  StaticJsonDocument<200> message;

  if (fromOut) {
    if (granted) {
      message["state"] = 1;
    } else {
      message["state"] = 0;
    }
    message["cardID"] = cardID;
  }

  message["time"] = getTime();
  message["date"] = getDate();

  size_t neededSize = measureJson(message) + 1;
  char jsonBuffer[neededSize];
  serializeJson(message, jsonBuffer, sizeof(jsonBuffer));

  if (fromOut) MQTT_CLIENT.publish("access", jsonBuffer);
  else MQTT_CLIENT.publish("exit", jsonBuffer);
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Mensaje recibido en el topic: ");
  Serial.print(topic);
  Serial.print(". Mensaje: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  if (strcmp(topic, "state") == 0) {
    Serial.println("state");
  } else if (strcmp(topic, "users") == 0) {
    updateUsersList(payload);
  }
}

void updateUsersList(byte* payload) {  //TESTEAR
  int payloadMaxSize = 1000;

  // Payload to string
  char json[payloadMaxSize];
  int i;
  for (i = 0; i < payloadMaxSize && payload[i] != '\0'; i++) {
    json[i] = (char)payload[i];
  }
  json[i] = '\0';  // Termina la cadena con un caracter nulo

  // ArduinoJson to parse JSON String
  DynamicJsonDocument doc(payloadMaxSize);
  DeserializationError error = deserializeJson(doc, json);

  if (error) {
    Serial.print("Error deserializando JSON: ");
    Serial.println(error.c_str());
    return;
  }

  // Limpia el array de usuarios actual
  for (int j = 0; j < maxUsers; j++) {
    users[j] = "";
  }

  // Itera sobre el documento JSON y almacena los usuarios en el arreglo
  int index = 0;
  for (JsonVariant value : doc.as<JsonArray>()) {
    if (index < maxUsers) {
      users[index] = value.as<String>();
      index++;
    } else {
      break;  // Sal del bucle si se alcanza el máximo de usuarios
    }
  }

  // Imprime los usuarios actualizados para verificar
  for (int j = 0; j < maxUsers; j++) {
    Serial.print("User ");
    Serial.print(j);
    Serial.print(": ");
    Serial.print(users[j]);
    Serial.println("|");
  }

}

void updateStatus() {
}

//Functional methods


bool requestKey() {
  String enteredKey;
  LCDRequestKeyMessage();

  while (true) {
    buttonInterruption();
    rfidScanInterruption();

    char key = keypad.getKey();
    if (key) {
      if (key == '*') {
        lcd.blink_off();
        return false;  // If * is pressed before entering the complete key, return false
      } else if (key >= '0' && key <= '9') {
        enteredKey += key;
        lcd.print("*");
      }
    }

    if (enteredKey.length() >= adminkey.length()) {
      break;
    }
  }
  lcd.blink_off();
  delay(DELAY);

  return isCorrectKey(enteredKey);
}

bool isCorrectKey(String enteredKey) {
  if (enteredKey == adminkey) {
    return true;
  } else {
    LCDIncorrectCodeMessage();
    return false;
  }
}

void buttonInterruption() {
  int buttonStatus = digitalRead(buttonPin);
  if (buttonStatus == LOW) {
    accessOutput(true, false);
    accessRecordMQTTPublish("Inside button", true, false);
  }
}

void rfidScanInterruption() {
  String UID;
  bool RFIDReaderCondition = mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial();
  if (RFIDReaderCondition) {
    UID = readUid(mfrc522.uid.uidByte);
    accessController(UID, false);
  }
}

void adminMenu() {
  LCDAdminMenuMessage();
  while (true) {
    buttonInterruption();
    rfidScanInterruption();
    char key = keypad.getKey();
    if (key) {
      lcd.blink_off();
      lcd.setCursor(14, 1);
      lcd.print(key);
      delay(DELAY);
      switch (key) {
        case 'A':
          addUser();
          break;
        case 'B':
          deleteUserWithRFID();
          break;
        case 'C':
          changePassword();
          break;
        case 'D':
          definitiveChangeMenu();
          break;
        case '*':
          LCDCanceledMessage();
          break;
        default:
          LCDInvalidOptionMessage();
          adminMenu();
          break;
      }
      break;
    }
  }
}

void addUser() {
  lcd.clear();
  lcd.setCursor(0, 0);

  if (numUsers >= maxUsers) {
    lcd.println("LIMIT REACHED");
    delay(DELAY);
    return;
  }

  lcd.print("SCAN RFID DEVICE");

  while (true) {
    buttonInterruption();
    if (keypad.getKey() == '*') {
      LCDCanceledMessage();
      return;
    }
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      String UID = readUid(mfrc522.uid.uidByte);

      lcd.clear();
      lcd.setCursor(0, 0);

      if (lookForUid(UID) == "-1") {
        users[numUsers] = UID;

        Serial.print("\nUID AGREGADO:\n|");
        Serial.print(UID);
        Serial.print("|\n\n");

        numUsers++;
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("USER ADDED!");
        delay(DELAY);
        break;
      } else {
        lcd.print("RFID ALREADY REG");
        delay(DELAY);
        break;
      }
    }
  }
}

void deleteUserWithRFID() {
  lcd.clear();
  lcd.setCursor(0, 0);

  if (numUsers == 0) {
    lcd.print("NO USERS ADDED!");
    delay(DELAY);
    return;
  }

  lcd.print("SCAN RFID DEVICE");

  while (true) {

    buttonInterruption();

    char key = keypad.getKey();
    if (key == '*') {
      LCDCanceledMessage();
      return;
    }
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      String UID = readUid(mfrc522.uid.uidByte);
      lcd.clear();
      lcd.setCursor(0, 0);

      if (lookForUid(UID) != "-1") {
        for (int i = 0; i < numUsers; i++) {
          if (users[i] == UID) {
            //Se podria reemplazar el ultimo por el que se elimino (menos costoso)
            //Move elements to left to clean empty spaces
            for (int j = i; j < numUsers - 1; j++) {
              users[j] = users[j + 1];
            }
            numUsers--;
            lcd.print("USER DELETED!");
          }
        }
        break;
      } else {
        lcd.print("NOT REGISTRED!");
        break;
      }
    }
  }
  delay(DELAY);
}

void changePassword() {
  String newKey = "";
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("ENTER NEW CODE");
  delay(THINK_DELAY);

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("NEW CODE:");
  lcd.setCursor(0, 1);
  lcd.print("(6 DIGITS)");
  lcd.setCursor(9, 0);
  lcd.blink_on();

  while (true) {

    buttonInterruption();
    rfidScanInterruption();

    char key = keypad.getKey();
    if (key) {
      if (key >= '0' && key <= '9') {
        newKey += key;
        lcd.print("*");
      }
      if (newKey.length() == 6) {
        delay(DELAY);
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("CONFIRM:");
        String confirmationKey = "";
        while (newKey != confirmationKey) {
          key = keypad.getKey();
          if (newKey.length() == confirmationKey.length()) {
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.blink_off();
            lcd.print("CODE NOT MATCH");
            lcd.setCursor(0, 1);
            lcd.print("CANCELED!");
            delay(THINK_DELAY);
            return;
          }
          if (key) {
            if (key >= '0' && key <= '9') {
              confirmationKey += key;
              lcd.print("*");
            }
          }
        }
        lcd.blink_off();
        adminkey = newKey;
        delay(DELAY);
        LCDSavedMessage();
        break;
      }
      // If asterisk (*) is pressed, finish the input and cancel
      else if (key == '*') {
        LCDCanceledMessage();
        break;
      } else {
      }
    }
  }
  lcd.blink_off();
}

void definitiveChangeMenu() {
  LCDDefinitiveChangeMenu();
  while (true) {
    buttonInterruption();
    rfidScanInterruption();
    char key = keypad.getKey();
    if (key) {
      switch (key) {
        case 'A':
          switch (state) {
            case 0:  //Normal
              definitiveLockStatus();
              break;
            default:  //Locked or Unlocked
              returnToNormalStatus();
              break;
          }
          break;
        case 'B':
          switch (state) {
            case 0:  //Normal
              definitiveUnlockStatus();
              break;
            case 1:  //Locked
              definitiveUnlockStatus();
              break;
            default:  //Unlocked
              definitiveLockStatus();
              break;
          }
          break;
        default:
          LCDInvalidOptionMessage();
          definitiveChangeMenu();
          break;
      }
      break;
    }
  }
}

void returnToNormalStatus() {
  isDefinitiveState = false;
  state = 0;
  digitalWrite(greenLedPin, LOW);
  digitalWrite(redLedPin, LOW);
}

void definitiveLockStatus() {
  isDefinitiveState = true;
  state = 1;
  digitalWrite(greenLedPin, LOW);
  digitalWrite(redLedPin, HIGH);
}

void definitiveUnlockStatus() {
  isDefinitiveState = true;
  state = 2;
  digitalWrite(redLedPin, LOW);
  digitalWrite(greenLedPin, HIGH);
}

String readUid(byte* uid) {
  String UID = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    if (uid[i] < 0x10) {
      UID += "0";
    }
    UID += String(uid[i], HEX);
    UID += " ";
  }
  mfrc522.PICC_HaltA();  // Stop comunication with the card
  return UID;
}

String lookForUid(String UID) {
  for (int i = 0; i < maxUsers; i++) {

    if (users[i] == UID) {
      return users[i];
    }
  }
  return "-1";
}

void accessController(String UID, bool message) {
  if (lookForUid(UID) != "-1") {
    accessOutput(true, message);
    accessRecordMQTTPublish(UID, true, true);
  } else {
    accessOutput(false, message);
    accessRecordMQTTPublish(UID, false, true);
  }
}

void accessOutput(bool condition, bool message) {
  if (isDefinitiveState) return;
  if (condition) {
    if (message) {
      LCDAccessGrantedMessage();
    }
    openDoor();

  } else {
    if (message) {
      LCDAccessDeniedMessage();
    }
    deniedAccessLight();
  }
}

void openDoor() {
  digitalWrite(doorRelayPin, HIGH);
  approvedAccessLight();
  digitalWrite(doorRelayPin, LOW);
}

void approvedAccessLight() {
  digitalWrite(greenLedPin, HIGH);
  delay(DELAY * 2);
  digitalWrite(greenLedPin, LOW);
}

void deniedAccessLight() {
  digitalWrite(redLedPin, HIGH);
  delay(DELAY * 2);
  digitalWrite(redLedPin, LOW);
}


//Time methods


void connectWifi(String ssid, String password) {
  const int maxAttempts = 16;
  int attempts = 0;

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("CONECTING WIFI");
  lcd.setCursor(0, 1);

  WiFi.begin(ssid.c_str(), password.c_str());

  while (WiFi.status() != WL_CONNECTED) {
    delay(DELAY);
    lcd.print(".");
    attempts++;

    if (attempts >= maxAttempts) {
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("FAILED CONECTION");
      delay(DELAY);
      WiFi.disconnect(true);
      WiFi.mode(WIFI_OFF);
      manualTimeConfiguration();
      return;
    }
  }

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("CONECTED!");
  delay(DELAY);
}

void getTimeFromWifi() {
  const int maxAttempts = 16;
  int attempts = 0;

  const char* ntpServer1 = "pool.ntp.org";
  const char* ntpServer2 = "time.nist.gov";
  const char* time_zone = "UTC+3";  // TimeZone rule for Argentina

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SETTING TIME");
  lcd.setCursor(0, 1);

  configTzTime(time_zone, ntpServer1, ntpServer2);
  attempts = 0;
  struct tm timeinfo;
  while (true) {
    lcd.print(".");
    delay(DELAY);
    if (getLocalTime(&timeinfo)) {
      break;
    }
    attempts++;
    if (attempts >= maxAttempts) {
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("FAILED TO");
      lcd.setCursor(0, 1);
      lcd.print("OBTAIN TIME");
      delay(THINK_DELAY);
      manualTimeConfiguration();
      break;
    }
  }
}

void manualTimeConfiguration() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SET DATE");
  delay(DELAY);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("DD/MM/YYYY");

  String date = "";
  lcd.setCursor(0, 1);
  lcd.blink_on();
  while (true) {
    char key = keypad.getKey();
    if (key) {
      if (key >= '0' && key <= '9') {
        date += key;
        lcd.print(key);
      }
    }
    if (date.length() == 2 || date.length() == 5) {
      lcd.print('/');
      date += '/';
    }
    if (date.length() == 10) break;
  }
  lcd.blink_off();
  delay(DELAY);

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SET TIME");
  delay(DELAY);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("HH:MM");

  String time = "";
  lcd.setCursor(0, 1);
  lcd.blink_on();
  while (true) {
    char key = keypad.getKey();
    if (key) {
      if (key >= '0' && key <= '9') {
        time += key;
        lcd.print(key);
      }
    }
    if (time.length() == 2) {
      lcd.print(':');
      time += ':';
    }
    if (time.length() == 5) break;
  }
  lcd.blink_off();
  delay(DELAY);

  struct tm timeinfo;
  int year, month, day, hour, minute, second;

  year = date.substring(6, 10).toInt();
  month = date.substring(3, 5).toInt();
  day = date.substring(0, 2).toInt();
  hour = time.substring(0, 2).toInt();
  minute = time.substring(3, 5).toInt();
  second = 0;

  //Date Check
  if (year < 2023) {
    LCDIncorrectTimeMessage("DATE");
    manualTimeConfiguration();
    return;
  }
  if (month < 1 || month > 12) {
    LCDIncorrectTimeMessage("DATE");
    manualTimeConfiguration();
    return;
  }
  if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) && (day < 1 || day > 31)) {
    LCDIncorrectTimeMessage("DATE");
    manualTimeConfiguration();
    return;
  }
  int february = (year % 4 == 0) ? 29 : 28;
  if (month == 2 && (day < 1 || day > february)) {
    LCDIncorrectTimeMessage("DATE");
    manualTimeConfiguration();
    return;
  }
  if ((month == 4 || month == 6 || month == 9 || month == 11) && (day < 1 || day > 30)) {
    LCDIncorrectTimeMessage("DATE");
    manualTimeConfiguration();
    return;
  }

  //Time Check
  if (hour > 24) {
    LCDIncorrectTimeMessage("TIME");
    manualTimeConfiguration();
    return;
  }
  if (minute > 59) {
    LCDIncorrectTimeMessage("TIME");
    manualTimeConfiguration();
    return;
  }

  timeinfo.tm_year = year - 1900;
  timeinfo.tm_mon = month - 1;
  timeinfo.tm_mday = day;
  timeinfo.tm_hour = hour;
  timeinfo.tm_min = minute;
  timeinfo.tm_sec = second;

  time_t t = mktime(&timeinfo);
}

String getTime() {
  struct tm timeinfo;
  getLocalTime(&timeinfo);

  String timeString = "";
  if (timeinfo.tm_hour < 10) {
    timeString += "0";
  }
  timeString += timeinfo.tm_hour;
  timeString += ":";
  if (timeinfo.tm_min < 10) {
    timeString += "0";
  }
  timeString += timeinfo.tm_min;


  //getLocalTime(&timeinfo);
  Serial.println(String(timeinfo.tm_hour) + ":" + String(timeinfo.tm_min) + "." + String(timeinfo.tm_sec) + " " + String(timeinfo.tm_mday) + "/" + String(timeinfo.tm_mon + 1) + "/" + String(timeinfo.tm_year + 1900));


  return timeString;
}

String getDate() {
  struct tm timeinfo;
  getLocalTime(&timeinfo);

  String dateString = "";
  if (timeinfo.tm_mday < 10) {
    dateString += "0";
  }
  dateString += timeinfo.tm_mday;
  dateString += "/";
  if ((timeinfo.tm_mon + 1) < 10) {
    dateString += "0";
  }
  dateString += (timeinfo.tm_mon + 1);
  dateString += "/";
  dateString += (timeinfo.tm_year + 1900);

  return dateString;
}



// LCD display methods



void LCDinitialMessage() {
  switch (state) {
    case 0:
      LCDNormalStateMessage();
      break;
    case 1:
      LCDSecuredDoorMessage();
      break;
    default:
      LCDUnsecuredDoorMessage();
      break;
  }
}

void LCDNormalStateMessage(){
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SCAN RFID OR");
  lcd.setCursor(0, 1);
  lcd.print("PRESS *");
}

void LCDUnsecuredDoorMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("DOOR UNLOCKED");
  lcd.setCursor(0, 1);
  lcd.print("PRESS *");
}

void LCDSecuredDoorMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("DOOR LOCKED");
  lcd.setCursor(0, 1);
  lcd.print("PRESS *");
}

void LCDRequestKeyMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("ENTER ADMIN CODE");
  lcd.setCursor(0, 1);
  lcd.blink_on();
}

void LCDAccessGrantedMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("ACCESS GRANTED");
}

void LCDAccessDeniedMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("ACCESS DENIED");
}

void LCDAdminMenuMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("A: ADD USER");
  lcd.setCursor(0, 1);
  lcd.print("B: DELETE USER");

  delay(THINK_DELAY);

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("C: CHANGE CODE");
  lcd.setCursor(0, 1);
  lcd.print("D: CHANGE STATUS");
  delay(THINK_DELAY);

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("*: EXIT");
  lcd.setCursor(0, 1);
  lcd.print("SELECT OPTION:");
  lcd.blink_on();
}

void LCDDefinitiveChangeMenu() {
  lcd.clear();
  lcd.setCursor(0, 0);

  switch (state) {
    case 0:
      lcd.print("A: LOCK");
      lcd.setCursor(0, 1);
      lcd.print("B: UNLOCK");
      break;
    case 1:
      lcd.print("A: NORMAL STATE");
      lcd.setCursor(0, 1);
      lcd.print("B: UNLOCK DOOR");
      break;
    default:
      lcd.print("A: NORMAL STATE");
      lcd.setCursor(0, 1);
      lcd.print("B: LOCK DOOR");
      break;
  }
}

void LCDCanceledMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("CANCELED!");
  delay(DELAY);
}

void LCDInvalidOptionMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("INVALID OPTION!");
  lcd.setCursor(0, 1);
  lcd.print("TRY AGAIN");
  delay(DELAY);
}

void LCDSavedMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SAVED!");
  delay(DELAY);
}

void LCDIncorrectTimeMessage(String time) {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("INCORRECT " + time);
  lcd.setCursor(0, 1);
  lcd.print("TRY AGAIN");
  delay(THINK_DELAY);
}

void LCDIncorrectCodeMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("INCORRECT CODE!");
  delay(THINK_DELAY);
}

void LCDReconectMQTTMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("RECONECTING MQTT");
  delay(THINK_DELAY);
}