package accesscontrol.controller;

import accesscontrol.MQTTPublisher;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import spark.Request;
import spark.Response;

public class LockController {
  private final MQTTPublisher mqttPublisher;


  public LockController(MQTTPublisher mqttPublisher) {
    this.mqttPublisher = mqttPublisher;
  }

  public String lockDoors(Request req, Response res) {
      MqttMessage state = new MqttMessage("1".getBytes());
      mqttPublisher.publishNewState(state);
      return "success";
  }

  public String returnToNormal(Request req, Response res) {
      MqttMessage state = new MqttMessage("0".getBytes());
      mqttPublisher.publishNewState(state);
      return "success";
  }

  public String unlockDoors(Request req, Response res) {
      MqttMessage state = new MqttMessage("2".getBytes());
      mqttPublisher.publishNewState(state);
      return "success";
  }

}
