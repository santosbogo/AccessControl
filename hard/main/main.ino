//Libraries
  #include <Arduino.h>
  #include <Keypad.h>
  #include <SPI.h>
  #include <MFRC522.h>
  #include <LiquidCrystal_I2C.h>
  #include <SD.h>
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

  //SD reader pin
  #define NEW_SD_MOSI 32
  #define NEW_SD_MISO 34
  #define NEW_SD_SCLK 35
  #define NEW_SD_CS 26

  //Door relay pin
  #define doorRelayPin 36  //In case we want to add a door relay

  //Keypad
  byte rowPins[4] = { 27, 5, 17, 16 };   // Keypad row pins
  byte colPins[4] = { 4, 0, 2, 15 };  // Keypad column pins


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
  #define maxUsers 1000     // Max capacity of users register

  //Variables
  bool interruptFlag = false;
  bool correctKey = false;
  bool isDefinitiveOpen = false;
  String users[maxUsers];
  int numUsers = 0;                           // Actual number of users registred
  String adminkey = "123456";                 //Deffault admin password
  const String encryptionKey = "SantosBogo";  //It is used for encrypt and decrypt system file

//MQTT instance
#define PUBLIC_IP "54.89.103.143"


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

  // Configuración del lector SD con los nuevos pines
  SPIClass spiSD(HSPI);
  spiSD.begin(NEW_SD_SCLK, NEW_SD_MISO, NEW_SD_MOSI, NEW_SD_CS);

  //Initialize SD
  if (!SD.begin(NEW_SD_CS, spiSD)) {
    delay(DELAY);
    if (!SD.begin(NEW_SD_CS, spiSD)) {
      delay(DELAY);
      if (!SD.begin(NEW_SD_CS, spiSD)) {
        Serial.println("Fallo al inicializar el lector SD");
      }
    }
  }

  connectWifi("UA-Alumnos", "41umn05WLC");
  // connectWifi("Flia Lando 2", "aabbccddeeff");
  // connectWifi("Fila Bogo 2.4", "244466666");
  //connectWifi(wifiSSIDFileRead(), wifiPasswordFileRead());

  //Set Time
  if(WiFi.status() == WL_CONNECTED){
    getTimeFromWifi();
  }
  else{
    manualTimeConfiguration();
  }

  //Connect to MQTT server
  connectMQTT();

  admitedUsersFileRead();
  codeFileRead();
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
      Serial.println("conectado");
      MQTT_CLIENT.subscribe("users"); //Subscribe to all topics
      MQTT_CLIENT.subscribe("state");
      MQTT_CLIENT.subscribe("#");
    }
    delay(DELAY*6);
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

  char messageChar[length + 1]; // +1 for null terminator
  message.toCharArray(messageChar, length + 1);
  MQTT_CLIENT.publish("AccessControl/test", messageChar);
}

void accessRecordMQTTPublish(String UID, bool granted, bool fromOut){
  String persUID = lookForUid(UID);
  String personID = (granted) ? persUID.substring(0, 3) : "---";
  String cardID = (granted) ? persUID.substring(4) : UID;

  StaticJsonDocument<200> message;

  if(fromOut){
    if(granted){
    message["state"] = 1;
    }
    else{
      message["state"] = 0;
    }
    message["personID"] = personID;
    message["cardID"] = cardID;
  }
    
  message["time"] = getTime();
  message["date"] = getDate();

  size_t neededSize = measureJson(message) + 1;
  char jsonBuffer[neededSize];
  serializeJson(message, jsonBuffer, sizeof(jsonBuffer));

  if(fromOut) MQTT_CLIENT.publish("access", jsonBuffer);
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

  if (strcmp(topic, "state") == 0){
    Serial.println("state");
  }
  else if (strcmp(topic, "users") == 0){
    updateUsersList(payload);
  }
}

void updateUsersList(byte* payload){
  int payloadMaxSize = 1000;
  // Convierte el payload a una cadena de caracteres
  char json[payloadMaxSize];
  int i;
  for (i = 0; i < payloadMaxSize && payload[i] != '\0'; i++) {
    json[i] = (char)payload[i];
  }
  json[i] = '\0';  // Termina la cadena con un caracter nulo

  // Utiliza ArduinoJson para parsear la cadena JSON
  DynamicJsonDocument doc(payloadMaxSize);
  DeserializationError error = deserializeJson(doc, json);

  if (error) {
    Serial.print("Error deserializando JSON: ");
    Serial.println(error.c_str());
    return;
  }

  // Limpia el array de usuarios actual
  for (int j = 0; j < 100; j++) {
    users[j] = "";
  }

  // Extrae la lista de usuarios del documento JSON
  JsonArray usersArray = doc.as<JsonArray>();
  int index = 0;
  for (JsonVariant v : usersArray) {
    if (index < 100) {
      users[index] = "111|" + String(v.as<const char*>());
      index++;
    }
  }

  // Imprime los usuarios actualizados
  Serial.println("Usuarios actualizados:");
  for (int j = 0; j < index; j++) {
    Serial.println(users[j] + users[j].length());
  }
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
    accessRecordsFileWrite("Inside button", true, false);
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
          deleteMenu();
          break;
        case 'C':
          changePassword();
          break;
        case 'D':
          definitiveChange();
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
        String personalID = inputPersonalID();
        if (personalID == "CANCELED") return;
        for (int i = 0; i < numUsers; i++) {
          if (users[i].substring(0, 3) == personalID) {
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("PERSONAL ID");
            lcd.setCursor(0, 1);
            lcd.print("ALREADY REG");
            delay(THINK_DELAY);
            return;
          }
        }

        Serial.print("\n");
        Serial.print("Added UID:");
        Serial.print("\"");
        Serial.print(UID);
        Serial.print("\"");

        users[numUsers] = personalID + '|' + UID;
        numUsers++;
        admitedUsersFileWrite();
        addedUsersFileWrite(personalID, UID);
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
  Serial.println("Usuarios actualizados:");
  for (int j = 0; j < sizeof(users); j++) {
      Serial.println(users[j]); //Tir
  }
}

String inputPersonalID() {
  String personalID = "";
  int digitsEntered = 0;

  lcd.print("PERSONAL ID:");

  while (digitsEntered < 3) {
    buttonInterruption();
    rfidScanInterruption();

    char key = keypad.getKey();
    lcd.blink_on();
    if (key) {
      if (key >= '0' && key <= '9') {
        lcd.print(key);
        personalID += key;
        digitsEntered++;
      } else if (key == '*') {
        LCDCanceledMessage();
        lcd.blink_off();
        return ("CANCELED");
      } else {
        lcd.setCursor(0, 1);
        lcd.blink_off();
        lcd.print("JUST NUMBERS ID");
        delay(DELAY);
        lcd.setCursor(0, 1);
        lcd.print("                ");
        lcd.setCursor(digitsEntered + 12, 0);
      }
    }
  }
  lcd.blink_off();
  return personalID;
}

void deleteMenu() {
  LCDDeleteMenuMessage();
  while (true) {
    buttonInterruption();
    rfidScanInterruption();

    char key2 = keypad.getKey();
    if (key2) {
      switch (key2) {
        case 'A':
          deleteUserWithRFID();
          break;
        case 'B':
          deleteUserWithPersonalID();
          break;
        case '*':
          LCDCanceledMessage();
          break;
        default:
          LCDInvalidOptionMessage();
          deleteMenu();
      }
      break;
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
          if (users[i].substring(4) == UID) {
            deletedUsersFileWrite(users[i].substring(0, 3), UID);
            //Move elements to left to clean empty spaces
            for (int j = i; j < numUsers - 1; j++) {
              users[j] = users[j + 1];
            }
            numUsers--;
            admitedUsersFileWrite();
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

void deleteUserWithPersonalID() {
  lcd.clear();
  lcd.setCursor(0, 0);

  if (numUsers == 0) {
    lcd.print("NO USERS ADDED!");
    delay(DELAY);
    return;
  }

  String personalID = inputPersonalID();
  if (personalID == "CANCELED") return;

  bool userDeleted = false;

  for (int i = 0; i < numUsers; i++) {
    String storedPersonalID = users[i].substring(0, 3);
    if (storedPersonalID == personalID) {
      deletedUsersFileWrite(personalID, users[i].substring(4));
      // Move elements to the left to clean empty spaces
      for (int j = i; j < numUsers - 1; j++) {
        users[j] = users[j + 1];
      }
      numUsers--;
      admitedUsersFileWrite();
      userDeleted = true;
      break;
    }
  }

  lcd.clear();
  lcd.setCursor(0, 0);

  if (userDeleted) {
    lcd.print("USER DELETED");
  } else {
    lcd.print("USER NOT FOUND");
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
        changedCodesFileWrite(adminkey, newKey);
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

void definitiveChange() {
  if (isDefinitiveOpen) {
    doorStatusChangesFileWrite(true);
    isDefinitiveOpen = false;
    lcd.clear();
    lcd.setCursor(0, 0);
    digitalWrite(greenLedPin, LOW);
    lcd.print("DOOR SECURED");
    delay(DELAY);
  } else {
    doorStatusChangesFileWrite(false);
    isDefinitiveOpen = true;
    lcd.clear();
    lcd.setCursor(0, 0);
    digitalWrite(greenLedPin, HIGH);
    lcd.print("DOOR UNLOCKED");
    delay(DELAY);
  }
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
  for (int i = 0; i < numUsers; i++) {
    if (users[i].substring(4) == UID) {
      return users[i];
    }
  }
  return "-1";
}

void accessController(String UID, bool message) {
  if (lookForUid(UID) != "-1") {
    accessOutput(true, message);
    accessRecordMQTTPublish(UID, true, true);
    accessRecordsFileWrite(UID, true, true);
  } else {
    accessOutput(false, message);
    accessRecordMQTTPublish(UID, false, true);
    accessRecordsFileWrite(UID, false, true);
  }
}

void accessOutput(bool condition, bool message) {
  if (isDefinitiveOpen) return;
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


//Encryption methods


String encrypt(String message, String key) {
  String encryptedMessage = "";
  int keyIndex = 0;
  int messageLength = message.length();
  int keyLength = key.length();

  for (int i = 0; i < messageLength; i++) {
    char encryptedChar = message[i] ^ key[keyIndex];
    encryptedMessage += encryptedChar;

    keyIndex++;
    if (keyIndex >= keyLength) {
      keyIndex = 0;
    }
  }

  return encryptedMessage;
}

String decrypt(String message, String key) {
  String decryptedMessage = "";
  int keyIndex = 0;
  int messageLength = message.length();
  int keyLength = key.length();

  for (int i = 0; i < messageLength; i++) {
    char decryptedChar = message[i] ^ key[keyIndex];
    decryptedMessage += decryptedChar;

    keyIndex++;
    if (keyIndex >= keyLength) {
      keyIndex = 0;
    }
  }

  return decryptedMessage;
}


//File methods


void accessRecordsFileWrite(String UID, bool granted, bool fromOut) {
  String persUID;
  if (UID == "Inside button") persUID = "--- Inside button";
  else persUID = lookForUid(UID);
  File file = SD.open("/access_records.txt", FILE_APPEND);
  if (file) {
    String access = (granted) ? "Granted" : "Denied";
    String persID = (granted) ? persUID.substring(0, 3) : "---";
    String cardID = (granted) ? persUID.substring(4) : UID;
    String from = (fromOut) ? "Outside" : "Inside";

    file.println(access + "\t   " + persID + "\t\t" + cardID + "\t" + from + "\t\t" + getDate() + "\t" + getTime());
    file.close();

  } else {
    LCDCantOpenFileMessage();
  }
}

void systemFileWrite(String code) {
  code = encrypt(code, encryptionKey);
  File file = SD.open("/system_files/system.txt", FILE_WRITE);
  if (file) {
    file.print(code);
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
}

void changedCodesFileWrite(String oldCode, String newCode) {
  File file = SD.open("/admin_records/changed_codes.txt", FILE_APPEND);
  if (file) {
    file.println(oldCode + "\t\t" + getDate() + "\t" + getTime());
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
  systemFileWrite(newCode);
}

void addedUsersFileWrite(String personalID, String UID) {
  File file = SD.open("/admin_records/added_users.txt", FILE_APPEND);
  if (file) {
    file.println("   " + personalID + "\t\t" + UID + "\t" + getDate() + "\t" + getTime());
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
}

void deletedUsersFileWrite(String personalID, String UID) {
  File file = SD.open("/admin_records/deleted_users.txt", FILE_APPEND);
  if (file) {
    file.println("   " + personalID + "\t\t" + UID + "\t" + getDate() + "\t" + getTime());
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
}

void doorStatusChangesFileWrite(bool secured) {
  String doorStatus = (secured) ? "Secured\t" : "Unlocked";
  File file = SD.open("/admin_records/door_status_changes.txt", FILE_APPEND);
  if (file) {
    file.println(doorStatus + "\t" + getDate() + "\t" + getTime());
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
}

void admitedUsersFileWrite() {
  File file = SD.open("/admited_users.txt", FILE_WRITE);
  if (file) {
    file.seek(0);
    file.print("ID | Card ID\n");
    for (int i = 0; i < numUsers; i++) {
      file.print(users[i]);
      file.print("\n");
    }
    file.print("Total = " + String(numUsers));
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
}

void admitedUsersFileRead() {
  File file = SD.open("/admited_users.txt", FILE_READ);
  if (file) {
    int lineCount = 0;
    String persUID;
    file.readStringUntil('\n');
    while (file.available()) {
      persUID = file.readStringUntil('\n');
      if (persUID.substring(0, 5) == "Total") {
        numUsers = persUID.substring(8).toInt();
        break;
      }
      users[lineCount] = persUID;
      lineCount++;
    }
    file.close();
    numUsers = lineCount;
  } else {
    LCDCantOpenFileMessage();
  }
}

void codeFileRead() {
  File file = SD.open("/system_files/system.txt", FILE_READ);
  if (file) {
    String code = decrypt(file.readStringUntil('\n'), encryptionKey);
    file.close();
    adminkey = (code.length() == 6) ? code : "123456";
  } else Serial.println("Codigo NO asignado");
}

String wifiSSIDFileRead() {
  File file = SD.open("/wifi_credentials.txt", FILE_READ);
  String ssid;
  if (file) {
    while (file.available()) {
      ssid = file.readStringUntil('\n');
      if (ssid.substring(0, 5) == "SSID:") {
        ssid = ssid.substring(6);
        break;
      }
    }
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
  return ssid;
}

String wifiPasswordFileRead() {
  File file = SD.open("/wifi_credentials.txt", FILE_READ);
  String password;
  if (file) {
    while (file.available()) {
      password = file.readStringUntil('\n');
      if (password.substring(0, 9) == "Password:") {
        password = password.substring(10);
        break;
      }
    }
    file.close();
  } else {
    LCDCantOpenFileMessage();
  }
  return password;
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
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("SCAN RFID OR");
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
  if (isDefinitiveOpen) {
    lcd.print("D: LOCK DOOR");
  } else {
    lcd.print("D: UNLOCK DOOR");
  }
  delay(THINK_DELAY);

  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("*: EXIT");
  lcd.setCursor(0, 1);
  lcd.print("SELECT OPTION:");
  lcd.blink_on();
}

void LCDDeleteMenuMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("A: WITH RFID");
  lcd.setCursor(0, 1);
  lcd.print("B: WITH ID");
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

void LCDCantOpenFileMessage() {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("CAN'T OPEN FILE");
  lcd.setCursor(0, 1);
  lcd.print("CONTACT SUPPORT");
  delay(THINK_DELAY);
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