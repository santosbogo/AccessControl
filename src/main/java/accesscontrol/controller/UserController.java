package accesscontrol.controller;

import accesscontrol.dto.*;
import accesscontrol.model.*;
import com.google.gson.Gson;
import accesscontrol.queries.Users;
import spark.*;

import javax.persistence.EntityManager;


public class UserController{
    private final Users users;
    private final Gson gson = new Gson();
    public UserController(EntityManager entityManager) {
        this.users = new Users(entityManager);
    }


    public void addUser(Request req, Response res){
        UserDto userDto = gson.fromJson(req.body(), UserDto.class);
        String name = userDto.getName();
        String lastName = userDto.getLastName();

        AttemptDto attemptDto = gson.fromJson(req.body(), AttemptDto.class);
        long attemptUID = attemptDto.getUid();
        User user = new User(attemptUID, name, lastName);
        users.persist(user);
        res.type("application/json");

    }
}
