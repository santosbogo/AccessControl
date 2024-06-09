package accesscontrol;

import accesscontrol.controller.*;
import accesscontrol.dto.AttemptDto;
import accesscontrol.dto.DateTimeMessage;
import com.google.gson.JsonObject;
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
    private final UserController userController;
    private final LockController lockController;


    public MQTTListener(String broker,
                        ExitController exitController,
                        AttemptController attemptController,
                        UidController uidController,
                        UserController userController,
                        LockController lockController) {
        this.broker = broker;
        this.exitController = exitController;
        this.attemptController = attemptController;
        this.uidController = uidController;
        this.userController = userController;
        this.lockController = lockController;
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
                else if (topic.equals("access")) {
                    persistAttempt(messageString);
                }
                else if (topic.equals("new_user")) {
                    sendNewUserUID(messageString);
                }
                else if (topic.equals("hardware_state")){
                    changeHardwareState(messageString);
                }
                else if (topic.equals("hardware_user_deactivate")){
                    deactivateUserWithUID(messageString);
                }
                else if (topic.equals("request_from_hardware")){
                    handleHardwareRequests(messageString);
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

    private void deactivateUserWithUID(String messageString){
        String uid = gson.fromJson(messageString, String.class);
        userController.deactivateUserWithUID(uid);
    }

    private void changeHardwareState(String messageString){
        int state = gson.fromJson(messageString, Integer.class);
        //TODO: LLamar a un metodo en  de cambiar estado de hardware
    }

    private void handleHardwareRequests(String messageString){
        JsonObject jsonObject = gson.fromJson(messageString, JsonObject.class);
        String requestType = jsonObject.get("request").getAsString();

        if (requestType.equals("usersList")) {
            sendUsersList();
        }
        else {
            System.out.println("Tipo de solicitud no reconocido: " + requestType);
        }
    }

    private void sendUsersList(){
        userController.publishUsersList();
    }
}