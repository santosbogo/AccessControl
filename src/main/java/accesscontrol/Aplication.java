package accesscontrol;

import com.google.gson.Gson;
import accesscontrol.controller.AttemptController;
import accesscontrol.controller.UserController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Aplication {
    static Gson gson = new Gson();
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("AccessControl");
        final EntityManager entityManager = factory.createEntityManager();
        final UserController userController = new UserController(entityManager);
        final AttemptController attemptController = new AttemptController(entityManager); 
    }
}
