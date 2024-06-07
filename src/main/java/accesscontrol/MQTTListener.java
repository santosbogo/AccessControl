package accesscontrol;

import accesscontrol.controller.AttemptController;
import accesscontrol.controller.ExitController;
import accesscontrol.controller.UidController;
import accesscontrol.dto.AttemptDto;
import accesscontrol.dto.DateTimeMessage;
import org.eclipse.paho.client.mqttv3.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static accesscontrol.Application.gson;

public class MQTTListener {
    private MqttClient client;
    private final String broker;

    private final ExitController exitController;
    private final AttemptController attemptController;
    private final UidController uidController;


    public MQTTListener(String broker, ExitController exitController, AttemptController attemptController, UidController uidController) {
        this.broker = broker;
        this.exitController = exitController;
        this.attemptController = attemptController;
        this.uidController = uidController;
        connectMQTT();
    }

    private void connectMQTT() {
        try {
            if (this.client == null) {
                String clientId = "awsListener";
                this.client = new MqttClient(broker, clientId);
            }
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            System.out.println("Connecting to MQTT broker: " + broker);
            client.connect(options);
            System.out.println("Connected");

            client.setCallback(defineCallback());

            client.subscribe("#");
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

    private MqttCallback defineCallback(){
        return new MqttCallback() {
            public void connectionLost(Throwable cause) {
                connectMQTT();
            }

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String messageString = new String(message.getPayload());
                System.out.println("Message received:\n\tTopic: " + topic + "\n\tMessage: " + messageString);

                if (topic.equals("exit")) {
                    persistExit(messageString);
                }
                else if(topic.equals("access")) {
                    persistAttempt(messageString);
                }
                else if (topic.equals("new_user")) {
                    sendNewUserUID(messageString);
                }
                else if (topic.equals("hardware_state")){
                    //TODO: hardware_state
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
                // Not needed for a subscriber, only publisher
            }
        };
    }


    private void persistExit(String messageString){
        DateTimeMessage dateTimeMessage = gson.fromJson(messageString, DateTimeMessage.class);

        LocalDate date = dateTimeMessage.getDate();
        LocalTime time = dateTimeMessage.getTime();

        System.out.println("Date: " + date);
        System.out.println("Time: " + time);

        exitController.addExit(date, time);
    }

    private void persistAttempt(String messageString){
        AttemptDto accessEvent = gson.fromJson(messageString, AttemptDto.class);

        String uid = accessEvent.getCardId();
        LocalDate date = accessEvent.getDate();
        LocalTime time = accessEvent.getTime();
        Boolean status = accessEvent.getState();

        System.out.println("UID: " + uid);
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
        System.out.println("Status: " + status);

        attemptController.addAttempt(uid, date, time, status);
    }

    private void sendNewUserUID(String messageString){
        uidController.processNewUserUID(messageString);
    }
}
