package accesscontrol.controller;
import com.google.gson.*;
import org.eclipse.paho.client.mqttv3.*;

public class UidController {
  private MqttClient client;
  private volatile String receivedUid = null;
  private final Object uidLock = new Object();
  public UidController() {
    try {
      this.client = new MqttClient("tcp://broker-address:1883", MqttClient.generateClientId());
      MqttConnectOptions options = new MqttConnectOptions();
      options.setCleanSession(true);
      options.setAutomaticReconnect(true);
      client.connect(options);
      client.setCallback(new MqttCallback() {
        public void connectionLost(Throwable cause) {
          System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
          if (topic.equals("uid")) {
            final String uid = new String(message.getPayload());

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
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
  public String requestUid(spark.Request req, spark.Response res) {
    synchronized (uidLock) {
      try {
        receivedUid = null;
        client.publish("requestUid", new MqttMessage("Request UID".getBytes()));

        // Espera a recibir el UID
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
      } catch (MqttException e) {
        res.status(500);
        return "{}";
      }
    }
  }

}
