package accesscontrol.controller;

import com.google.gson.*;
import org.eclipse.paho.client.mqttv3.*;
import spark.Request;
import spark.Response;

public class LockController {
  private final Gson gson = new Gson();
  private MqttClient client;

  public LockController() {
    try {
      this.client = new MqttClient("tcp://broker-address:1883", MqttClient.generateClientId());
      MqttConnectOptions options = new MqttConnectOptions();
      options.setCleanSession(true);
      options.setAutomaticReconnect(true);
      client.connect(options);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  public void lockDoors(Request req, Response res) {
    try {
      // Publicar el mensaje para bloquear las puertas
      client.publish("lockDoors", new MqttMessage("true".getBytes()));
    } catch (MqttException e) {
      System.out.println("Error al publicar el mensaje" + e.getMessage());
      e.printStackTrace();
    }
  }
}
