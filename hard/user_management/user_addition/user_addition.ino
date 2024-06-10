//Libraries
  #include <PubSubClient.h>
  #include <WiFi.h>
  #include <ArduinoJson.h>
  #include <SPI.h>
  #include <MFRC522.h>

//Pins
//RFID Pins
#define RST_PIN 33
#define SS_PIN 25

//Instances
//Rfid instance
MFRC522 mfrc522(SS_PIN, RST_PIN);

// WIFI instance
WiFiClient WIFI_CLIENT;

// MQTT
PubSubClient MQTT_CLIENT;

#define MQTT_IP "3.89.144.193"
#define WIFI_SSID "UA-Alumnos"
#define WIFI_PASSWORD "41umn05WLC"
// #define WIFI_SSID "Fila Bogo 2.4"
// #define WIFI_PASSWORD "244466666"


void setup() {
  Serial.begin(115200);

  //Initialize RFID
  SPI.begin();
  mfrc522.PCD_Init();

  connectWifi();
  connectMQTT();
}

void loop() {
  checkWifi();
  checkMQTTConnection();

  bool RFIDReaderCondition = mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial();

  if (RFIDReaderCondition) {
    String UID = readUid(mfrc522.uid.uidByte);
    publishUid(UID);
    Serial.println("Card UID: " + UID);
  }
}

void connectWifi() {
  const int maxAttempts = 16;
  int attempts = 0;

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(3000);
  }
}

void checkWifi() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWifi();
  }
}

void connectMQTT() {
  const int maxAttempts = 16;
  int attempts = 0;
  MQTT_CLIENT.setServer(MQTT_IP, 1883);
  MQTT_CLIENT.setClient(WIFI_CLIENT);

  while (!MQTT_CLIENT.connected()) {
    MQTT_CLIENT.connect("NewUserScanner");
  }

  Serial.println("CONECTADO A MQTT");
}

void checkMQTTConnection() {
  if (!MQTT_CLIENT.connected()) {
    Serial.println("Reconnecting to mqtt");
    connectMQTT();
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

void publishUid(String uid) {
  if (MQTT_CLIENT.publish("new_user", uid.c_str())) {
    Serial.println("UID published successfully");
  } else {
    Serial.println("Failed to publish UID");
  }
}