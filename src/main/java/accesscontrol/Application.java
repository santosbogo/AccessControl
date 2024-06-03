package accesscontrol;

import accesscontrol.controller.*;
import accesscontrol.dto.*;
import accesscontrol.model.Admin;
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
    static String broker = "tcp://54.89.103.143:1883";
    static String clientId = "JavaClient";
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        setFactory(factory);

        AuthenticationController autController = new AuthenticationController();
        ExitController exitController = new ExitController();
        AttemptController attemptController = new AttemptController();

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            //CollectController collectController = new CollectController(client);

            System.out.println("Connecting to MQTT broker: " + broker);
            client.connect(options);
            System.out.println("Connected");

            AdminController adminController = new AdminController();
            UidController uidController = new UidController();
            UserController userController = new UserController(client);
            LockController LockController = new LockController(client);
            PublisherMQTT publisher = new PublisherMQTT();

            Admin adminUser = new Admin("Fernando", "Lichtschein", "taylor", "swift");
            adminController.addAdmin(adminUser);

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
            Spark.get("/uid/getUid", uidController::requestUid);
            Spark.post("/user/add", userController::addUser);
            Spark.post("/admin/lock", LockController::lockDoors);
            Spark.post("/admin/unlock", LockController::unlockDoors);
            Spark.post("/admin/normal-state", LockController::returnToNormal);
            Spark.get("/attempt/:date/getAttempt", attemptController::getAttempts);
            Spark.post("/admin/login", autController::createAuthentication);
            Spark.post("/user/login", autController::createAuthentication);
            Spark.post("/user/logout", autController::deleteAuthentication);
            Spark.get("/user/verify", autController::getCurrentUser);
            Spark.get("/uid/getUid", uidController::requestUid);
            Spark.post("/user/add", userController::addUser);
            Spark.get("/users/findAll", userController::searchUsers);
            Spark.post("/user/deactivate/:id", userController::deactivateUser);



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