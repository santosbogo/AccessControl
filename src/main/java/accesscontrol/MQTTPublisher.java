package accesscontrol;

import org.eclipse.paho.client.mqttv3.*;

import accesscontrol.model.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class MQTTPublisher {
    private MqttClient client;
    private final String broker;

    private final Gson gson = new Gson();


    public MQTTPublisher(String broker) {
        this.broker = broker;
        connectMQTT();
    }

    private void connectMQTT() {
        try {
            if (this.client == null) {
                String clientId = "awsPublisher";
                this.client = new MqttClient(broker, clientId);
            }
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            System.out.println("Connecting to MQTT broker: " + broker);
            client.connect(options);
            System.out.println("Connected");


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public MqttClient getClient() {
        while (!this.client.isConnected()) {
            System.out.println("MQTT client is disconnected. Attempting to reconnect...");
            connectMQTT();
        }
        return this.client;
    }

    public String convertListToJson(List<String> users) {
        return gson.toJson(users);
    }

    public void publishNewState(MqttMessage state) {
        state.setQos(2);
        try {
            getClient().publish("state", state);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishUsersList(List<User> users) {
        List<String> usersJson = convertUsersListToJson(users);
        String jsonUsers = convertListToJson(usersJson);
        MqttMessage message = new MqttMessage(jsonUsers.getBytes());
        message.setQos(2);

        try {
            getClient().publish("users", message);
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
