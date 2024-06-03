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

}
