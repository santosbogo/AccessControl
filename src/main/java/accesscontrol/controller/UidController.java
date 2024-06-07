package accesscontrol.controller;

import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;

public class UidController {
  private MqttClient client;
  private volatile String receivedUid = null;
  private final Object uidLock = new Object();

  public UidController() {
    try {
      this.client = new MqttClient("tcp://54.236.15.136", MqttClient.generateClientId());
      MqttConnectOptions options = new MqttConnectOptions();
      options.setCleanSession(true);
      options.setAutomaticReconnect(true);

      client.setCallback(new MqttCallback() {
        public void connectionLost(Throwable cause) {
          System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
          System.out.println("Message arrived. Topic: " + topic + ", Message: " + new String(message.getPayload()));
          if (topic.equals("new_user")) {
            final String uid = new String(message.getPayload(), "UTF-8");
            System.out.println("Received new user: " + uid);
            synchronized (uidLock) {
              receivedUid = uid;
              uidLock.notify();
            }
          }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
          //ignorar por ahora
        }
      });

      client.connect(options);
      System.out.println("Connected to MQTT broker.");

      client.subscribe("new_user");
      System.out.println("Subscribed to topic 'new_user'.");
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  public String requestUid(spark.Request req, spark.Response res) {
    synchronized (uidLock) {
      try {
        receivedUid = null;
        System.out.println("Waiting for UID...");

        while (receivedUid == null) {
          uidLock.wait(10000); // Espera hasta 10 segundos
        }

        if (receivedUid != null) {
          res.type("application/json");
          JsonObject json = new JsonObject();
          json.addProperty("uid", receivedUid);
          return json.toString();
        } else {
          res.status(408); // Request Timeout
          return "{}";
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        res.status(500);
        return "{}";
      }
    }
  }
}
