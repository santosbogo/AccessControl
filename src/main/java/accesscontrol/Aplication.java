package accesscontrol;

import accesscontrol.controller.*;
import accesscontrol.dto.*;
import accesscontrol.model.*;
import accesscontrol.queries.*;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import spark.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.*;

import static accesscontrol.EntityManagerController.*;
import static spark.Spark.*;


public class Aplication {
    static Gson gson = new Gson();
    static String broker = "tcp://3.80.25.82:1883";
    static String clientId = "JavaClient";
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        setFactory(factory);

        ExitController exitController = new ExitController();
        AttemptController attemptController = new AttemptController();
        PublisherMQTT publisher = new PublisherMQTT();

        Spark.port(3333);

        before((req, resp) -> {
            resp.header("Access-Control-Allow-Origin", "*");
            resp.header("Access-Control-Allow-Headers", "*");
            resp.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
            resp.header("Access-Control-Allow-Credentials", "true");

        });

        options("/*", (req, resp) -> {
            resp.status(200);
            return "ok";
        });

        //Spark.get("/exit", exitController::getExits);
        Spark.get("/attempt/:date/getAttempt", attemptController::getAttempts);

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            System.out.println("Connecting to MQTT broker: " + broker);
            client.connect(options);
            System.out.println("Connected");

            publisher.publishUserCreation(client, "test1");

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost!");
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messageString = new String(message.getPayload());
                    System.out.println("Message received:\n\tTopic: " + topic + "\n\tMessage: " + messageString);

                    if (topic.equals("exit")) {
                        DateTimeMessage dateTimeMessage = gson.fromJson(messageString, DateTimeMessage.class);

                        LocalDate date = LocalDate.parse(dateTimeMessage.getDate());
                        LocalTime time = LocalTime.parse(dateTimeMessage.getTime());

                        System.out.println("Date: " + date);
                        System.out.println("Time: " + time);

                        exitController.addExit(date, time);

                    }
                    else if(topic.equals("access")) {
                        AttemptDto accessEvent = gson.fromJson(messageString, AttemptDto.class);
                        Long uid = accessEvent.getUid();
                        LocalDate date = accessEvent.getDate();
                        LocalTime time = accessEvent.getTime();
                        Boolean status = accessEvent.getStatus();

                        System.out.println("UID: " + accessEvent.getUid());
                        System.out.println("Date: " + accessEvent.getDate());
                        System.out.println("Time: " + accessEvent.getTime());
                        System.out.println("Status: " + accessEvent.getStatus());

                        attemptController.addAttempt(uid, date, time, status);
                    }

                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not needed for a subscriber
                }
            });

            client.subscribe("#");
            Thread.sleep(60000);
            client.disconnect();
            System.out.println("Disconnected");
        } catch (MqttException me) {
            System.out.println("Reason " + me.getReasonCode());
            System.out.println("Msg " + me.getMessage());
            System.out.println("Loc " + me.getLocalizedMessage());
            System.out.println("Cause " + me.getCause());
            System.out.println("Excep " + me);
            me.printStackTrace();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted");
        }

    }
}