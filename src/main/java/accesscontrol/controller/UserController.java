package accesscontrol.controller;

import accesscontrol.*;
import accesscontrol.dto.*;
import accesscontrol.model.*;
import com.google.gson.Gson;
import accesscontrol.queries.Users;
import spark.*;

import java.util.*;


public class UserController{
    private final Users users;
    private final MQTTPublisher mqttPublisher;

    private final Gson gson = new Gson();

    public UserController(MQTTPublisher mqttPublisher) {
        this.mqttPublisher = mqttPublisher;
        this.users = new Users();
    }
    public String addUser(Request req, Response res){
        UserDto userDto = gson.fromJson(req.body(), UserDto.class);
        String uid = userDto.getUid();
        String name = userDto.getName();
        String lastName = userDto.getLastName();
        User user = new User(uid, name, lastName);
        users.persist(user);
        publishUsersList();
        res.type("application/json");
        return user.asJson();
    }

    public String searchUsers(Request req, Response res) {
        List<User> foundUsers = users.findAllUsers();
        List<StateUserDto> stateUserDtos = new ArrayList<>();
        for (User user : foundUsers) {
            stateUserDtos.add(new StateUserDto(user.getUid(), user.getFirstName(), user.getLastName(), String.valueOf(user.state())));
        }
        res.type("application/json");
        return gson.toJson(stateUserDtos);
    }


    public String deactivateUser(Request req, Response res) {
        UidDto uidDto = gson.fromJson(req.body(), UidDto.class);
        String userId = uidDto.getUid();
        User user = users.findUserByUid(userId);
        if (user != null) {
            if (!user.state()) {
                res.status(400);
                return "User is already deactivated.";
            }
            user.deactivate();
            users.persist(user);
            publishUsersList();
            res.status(200);
            return "User deactivated successfully.";
        } else {
            res.status(404);
            return "User not found.";
        }
    }

    public String activateUser(Request req, Response res) {
        UidDto uidDto = gson.fromJson(req.body(), UidDto.class);
        String userId = uidDto.getUid();
        User user = users.findUserByUid(userId);
        if (user != null) {
            if (user.state()) {
                res.status(400);
                return "User is already activated.";
            }
            user.activate();
            users.persist(user);
            publishUsersList();
            res.status(200);
            return "User activated successfully.";
        } else {
            res.status(404);
            return "User not found.";
        }
    }

    public void deactivateUserWithUID(String uid){
        User user = users.findUserByUid(uid);
        if (user != null) {
            user.deactivate();
            users.persist(user);
        }
    }

    public void publishUsersList(){
        mqttPublisher.publishUsersList(users.findAllActive());
    }
}
