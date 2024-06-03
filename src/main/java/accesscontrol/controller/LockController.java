package accesscontrol.controller;

import com.google.gson.*;
import spark.Request;
import spark.Response;

public class LockController {
  /*
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

  public String lockDoors(Request req, Response res) {
    try {
      client.publish("lockDoors", new MqttMessage("true".getBytes()));
      return "{\"status\":\"success\", \"message\":\"Doors locked successfully.\"}";
    } catch (MqttException e) {
      System.out.println("Error al publicar el mensaje: " + e.getMessage());
      e.printStackTrace();
      res.status(500); // HTTP Internal Server Error
      return "{\"status\":\"error\", \"message\":\"Failed to lock doors.\"}";
    }
  }

   */
}
