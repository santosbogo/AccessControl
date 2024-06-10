package accesscontrol.controller;

import accesscontrol.MQTTPublisher;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import spark.Request;
import spark.Response;

public class LockController {
    private final MQTTPublisher mqttPublisher;
    int state = 0;


    public LockController(MQTTPublisher mqttPublisher) {
        this.mqttPublisher = mqttPublisher;
    }

    public String lockDoors(Request req, Response res) {
        MqttMessage state = new MqttMessage("1".getBytes());
        mqttPublisher.publishNewState(state);
        setState(1);
        return "success";
    }

    public String returnToNormal(Request req, Response res) {
        MqttMessage state = new MqttMessage("0".getBytes());
        mqttPublisher.publishNewState(state);
        setState(0);
        return "success";
    }

    public String unlockDoors(Request req, Response res) {
        MqttMessage state = new MqttMessage("2".getBytes());
        setState(2);
        mqttPublisher.publishNewState(state);
        return "success";
    }

    public void changeHardwareState(int state) {
        setState(state);
    }

    public String getState(Request req, Response res) {
        if(state == 0) return "Normal";
        else if (state == 1) return "Locked";
        else return "Unlocked";
    }

    public void setState(int state) {
        this.state = state;
    }
}