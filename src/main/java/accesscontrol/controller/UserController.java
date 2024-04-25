package accesscontrol.controller;

import com.google.gson.Gson;
import accesscontrol.queries.Users;

import javax.persistence.EntityManager;


public class UserController{
    private final Users users;
    private final Gson gson = new Gson();
    public UserController(EntityManager entityManager) {
        this.users = new Users(entityManager);
    }
}
