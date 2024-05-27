package accesscontrol.controller;

import accesscontrol.dto.*;
import accesscontrol.model.*;
import com.google.gson.Gson;
import accesscontrol.queries.Users;
import spark.*;


public class UserController{
    private final Users users;
    private final Gson gson = new Gson();
    public UserController() {
        this.users = new Users();
    }
    public String addUser(Request req, Response res){
        UserDto userDto = gson.fromJson(req.body(), UserDto.class);
        String uid = userDto.getUid();
        String name = userDto.getName();
        String lastName = userDto.getLastName();
        User user = new User(uid, name, lastName);
        users.persist(user);
        res.type("application/json");
        return user.asJson();
    }
}
