package accesscontrol.controller;

import accesscontrol.dto.*;
import com.google.gson.Gson;
import accesscontrol.queries.AccessAttempts;

import javax.persistence.EntityManager;
import accesscontrol.model.AccessAttempt;
import spark.Request;
import spark.Response;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;


public class AttemptController {
    private final AccessAttempts accessAttempt;
    private final Gson gson = new Gson();

    public AttemptController() {
        this.accessAttempt = new AccessAttempts();
    }

    public void addAttempt(Long uid, LocalDate date, LocalTime time, boolean success) {
        AccessAttempt accessAttempt = new AccessAttempt(uid, date, time, success);
        this.accessAttempt.persist(accessAttempt);
    }
}
