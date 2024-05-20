package accesscontrol;

import accesscontrol.controller.*;
import accesscontrol.dto.*;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import spark.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.*;

import static accesscontrol.EntityManagerController.*;
import static spark.Spark.*;


public class Application {
    static Gson gson = new Gson();
    static String broker = "tcp://18.234.56.90:1883";
    static String clientId = "JavaClient";
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        setFactory(factory);

        ExitController exitController = new ExitController();
        AttemptController attemptController = new AttemptController();
        AdminController adminController = new AdminController();
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

        Spark.get("/attempt/:date/getAttempt", attemptController::getAttempts);
        Spark.post("/user/signup", adminController::createAdmin);
        Spark.post("/admin/login", adminController::loginAdmin);

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

                        LocalDate date = dateTimeMessage.getDate();
                        LocalTime time = dateTimeMessage.getTime();

                        System.out.println("Date: " + date);
                        System.out.println("Time: " + time);

                        exitController.addExit(date, time);

                    }
                    else if(topic.equals("access")) {
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

                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not needed for a subscriber, only publisher
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