package accesscontrol;

import accesscontrol.controller.*;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.google.gson.Gson;
import spark.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


import static spark.Spark.*;

public class Aplication {
    static Gson gson = new Gson();
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("accessControlDB");
        final EntityManager entityManager = factory.createEntityManager();
        final UserController userController = new UserController(entityManager);
        final AttemptController attemptController = new AttemptController(entityManager);
        final ExitController exitController = new ExitController(entityManager);

    }
}