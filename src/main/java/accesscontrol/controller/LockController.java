package accesscontrol.controller;

import accesscontrol.MqttPublisher;
import com.google.gson.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import spark.Request;
import spark.Response;

public class LockController {
  private final Gson gson = new Gson();
  private final MqttClient client;
  private final MqttPublisher publisherMQTT;


  public LockController(MqttClient client) {
    this.client = client;
    this.publisherMQTT = new MqttPublisher();
  }

  public String lockDoors(Request req, Response res) {
      MqttMessage state = new MqttMessage("1".getBytes());
      publisherMQTT.publishNewState(state, client);
      return "success";
  }

  public String returnToNormal(Request req, Response res) {
      MqttMessage state = new MqttMessage("0".getBytes());
      publisherMQTT.publishNewState(state, client);
      return "success";
  }

  public String unlockDoors(Request req, Response res) {
      MqttMessage state = new MqttMessage("2".getBytes());
      publisherMQTT.publishNewState(state, client);
      return "success";
  }

}
