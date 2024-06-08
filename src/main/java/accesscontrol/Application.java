package accesscontrol;

import accesscontrol.controller.*;
import accesscontrol.model.Admin;
import com.google.gson.Gson;
import spark.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static accesscontrol.EntityManagerController.*;
import static spark.Spark.*;


public class Application {
    static Gson gson = new Gson();
    private static final String broker = "tcp://18.209.1.171";

    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        setFactory(factory);

        MQTTPublisher mqttPublisher = new MQTTPublisher(broker);

        AuthenticationController autController = new AuthenticationController();
        ExitController exitController = new ExitController();
        AdminController adminController = new AdminController();
        AttemptController attemptController = new AttemptController();
        UidController uidController = new UidController();

        UserController userController = new UserController(mqttPublisher);
        LockController lockController = new LockController(mqttPublisher);

        MQTTListener mqttListener = new MQTTListener(broker,
                exitController,
                attemptController,
                uidController,
                userController,
                lockController);

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

        //attempts & exits
        Spark.get("/attempt/getAttempt", attemptController::getAttempts);
        Spark.get("/exit/getExits", exitController::getExits);
        Spark.get("/uid/getUid", uidController::requestUid);
        //admin
        Spark.post("/admin/login", autController::createAuthentication);
        Spark.post("/admin/normal-state", lockController::returnToNormal);
        Spark.post("/admin/lock", lockController::lockDoors);
        Spark.post("/admin/unlock", lockController::unlockDoors);
        //authentication
        Spark.get("/user/verify", autController::getCurrentUser);
        Spark.post("/user/login", autController::createAuthentication);
        Spark.post("/user/logout", autController::deleteAuthentication);
        //user
        Spark.post("/user/add", userController::addUser);
        Spark.get("/user/verify", autController::getCurrentUser);
        Spark.get("/users/findAll", userController::searchUsers);
        Spark.post("/user/deactivate/:id", userController::deactivateUser);
        Spark.post("/user/activate/:id", userController::activateUser);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}