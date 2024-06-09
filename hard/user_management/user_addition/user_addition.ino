#include <PubSubClient.h>
#include <WiFi.h>
#include <ArduinoJson.h>
#include <SPI.h>
#include <MFRC522.h>

#define RST_PIN         33          // Configurable, see typical pin layout above
#define SS_PIN          25         // Configurable, see typical pin layout above

MFRC522 mfrc522(SS_PIN, RST_PIN);  // Create MFRC522 instance
#define DELAY 1000  // For not important or short messages

// WIFI instance
WiFiClient WIFI_CLIENT;

// Topic name
#define NEW_USER_TOPIC "new_user"

// MQTT
PubSubClient MQTT_CLIENT(WIFI_CLIENT);

// MQTT instance
#define PUBLIC_IP "34.207.199.67"

// Topic name
#define NEW_USER_TOPIC "new_user"

// WiFi credentials
const char* ssid = "Fila Bogo 2.4";
const char* password = "244466666";

void setup() {
	Serial.begin(9600);		// Initialize serial communications with the PC
	while (!Serial);		// Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
	SPI.begin();			// Init SPI bus
	mfrc522.PCD_Init();		// Init MFRC522
	delay(4);				// Optional delay. Some board do need more time after init to be ready, see Readme
	mfrc522.PCD_DumpVersionToSerial();	// Show details of PCD - MFRC522 Card Reader details
	connectWifi(ssid, password);
  connectMQTT();
}

void loop() {
    checkMQTTConnection();
    // Look for new cards
    if (!mfrc522.PICC_IsNewCardPresent()) {
        return;
    }

    // Select one of the cards
    if (!mfrc522.PICC_ReadCardSerial()) {
        return;
    }

    // Read and print UID
    String UID = readUid(mfrc522.uid.uidByte);
    Serial.println("Card UID: " + UID);

    // Publish the UID
    publishUid(UID);

    // Halt PICC
    mfrc522.PICC_HaltA();
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

void connectWifi(String ssid, String password) {
  const int maxAttempts = 16;
  int attempts = 0;

  WiFi.begin(ssid.c_str(), password.c_str());

  while (WiFi.status() != WL_CONNECTED) {
    delay(DELAY);
    attempts++;

    if (attempts >= maxAttempts) {
      delay(DELAY);
      WiFi.disconnect(true);
      WiFi.mode(WIFI_OFF);
      return;
    }
  }
  delay(DELAY);
}

void connectMQTT() {
  const int maxAttempts = 16;
  int attempts = 0;
  MQTT_CLIENT.setServer(PUBLIC_IP, 1883);
  MQTT_CLIENT.setClient(WIFI_CLIENT);


  MQTT_CLIENT.connect("AccessControl");  //AccessControl: MQTT identifier

  while (!MQTT_CLIENT.connected()) {
    delay(DELAY/6);
    attempts++;
    if (attempts >= maxAttempts) {
      Serial.println("Failed to connect");
      delay(DELAY);
    }
  }

  Serial.println("CONECTADO A MQTT");
}

void publishUid(String uid) {
  // Publish to new_user topic
  if (MQTT_CLIENT.publish(NEW_USER_TOPIC, uid.c_str())) {
    Serial.println("UID published successfully");
  } else {
    Serial.println("Failed to publish UID");
  }
}

void checkMQTTConnection() {
  if (!MQTT_CLIENT.connected()) {
    Serial.println("Reconnecting to the mqtt");
    connectMQTT();
  }
}