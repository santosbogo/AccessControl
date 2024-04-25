package accesscontrol.controller;

import com.google.gson.Gson;
import accesscontrol.queries.AccessAttempts;

import javax.persistence.EntityManager;


public class AttemptController {
    private final AccessAttempts accessAttempt;
    private final Gson gson = new Gson();

    public AttemptController(EntityManager entityManager) {
        this.accessAttempt = new AccessAttempts(entityManager);
    }
}
