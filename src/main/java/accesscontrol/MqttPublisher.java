package accesscontrol;

import accesscontrol.model.User;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;


public class MqttPublisher {
    private final Gson gson = new Gson();

    public String convertListToJson(List<String> users) {
        return gson.toJson(users);
    }

    public void publishNewState(MqttMessage state, MqttClient client) {
        state.setQos(2);
        try {
            client.publish("state", state);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishUsersList(List<User> users, MqttClient client) {
        List<String> usersJson = convertUsersListToJson(users);
        String jsonUsers = convertListToJson(usersJson);
        MqttMessage message = new MqttMessage(jsonUsers.getBytes());
        message.setQos(2);
        try {
            client.publish("users", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private List<String> convertUsersListToJson(List<User> users) {
        ArrayList<String> usersJson = new ArrayList<>();
        for (User user : users) {
            usersJson.add(user.getUid());
        }
        return usersJson;
    }


}
