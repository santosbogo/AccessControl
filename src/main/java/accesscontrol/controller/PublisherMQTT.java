package accesscontrol.controller;
import org.eclipse.paho.client.mqttv3.*;

public class PublisherMQTT {

    public void publishUserCreation(MqttClient client, String username) {
        try {
            MqttMessage message = new MqttMessage(username.getBytes());
            client.publish("user", message);
            System.out.println("Published message to user" + username);
        } catch (MqttException e) {
            System.out.println("Failed to publish message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
