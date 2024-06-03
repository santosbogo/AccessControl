package accesscontrol;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.util.List;


public class MqttPublisher {
    private final Gson gson = new Gson();

    public String convertListToJson(List<String> users) {
        return gson.toJson(users);
    }

    public void publishUsersList(List<String> users, MqttClient client) {
        String jsonUsers = convertListToJson(users);
        MqttMessage message = new MqttMessage(jsonUsers.getBytes());
        message.setQos(2);
        try {
            client.publish("users/topic", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
