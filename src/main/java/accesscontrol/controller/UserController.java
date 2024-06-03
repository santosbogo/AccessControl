package accesscontrol.controller;
import accesscontrol.MqttPublisher;
import accesscontrol.dto.*;
import accesscontrol.model.*;
import com.google.gson.Gson;
import accesscontrol.queries.Users;
import org.eclipse.paho.client.mqttv3.MqttClient;
import spark.*;

import java.util.*;


public class UserController{
    private final Users users;
    private final MqttClient client;
    private final MqttPublisher publisherMQTT;

    private final Gson gson = new Gson();
    public UserController(MqttClient client) {
        this.client = client;
        this.users = new Users();
        this.publisherMQTT = new MqttPublisher();

    }
    public String addUser(Request req, Response res){
        UserDto userDto = gson.fromJson(req.body(), UserDto.class);
        String uid = userDto.getUid();
        String name = userDto.getName();
        String lastName = userDto.getLastName();
        User user = new User(uid, name, lastName);
        users.persist(user);
        publisherMQTT.publishUsersList(users.findAllUsers(), client);
        res.type("application/json");
        return user.asJson();
    }

    public String searchUsers(Request req, Response res) {
        List<User> foundUsers = users.findAllUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : foundUsers) {
            userDtos.add(new UserDto(user.getUid(), user.getFirstName(), user.getLastName()));
        }
        res.type("application/json");
        return gson.toJson(userDtos);
    }


    public String deactivateUser(Request req, Response res) {
        String userId = req.params("id");
        User user = users.findUserByUid(userId);
        if (user != null) {
            user.deactivate();
            users.persist(user); // Actualizar el usuario en la base de datos
            publisherMQTT.publishUsersList(users.findAllUsers(), client);
            res.status(200);
            return "User deactivated successfully.";
        } else {
            res.status(404);
            return "User not found.";
        }
    }

    public String activateUser(Request req, Response res) {
        String userId = req.params("id");
        User user = users.findUserByUid(userId);
        if (user != null) {
            user.activate();
            users.persist(user); // Actualizar el usuario en la base de datos
            publisherMQTT.publishUsersList(users.findAllUsers(), client);
            res.status(200);
            return "User activated successfully.";
        } else {
            res.status(404);
            return "User not found.";
        }
    }
}
