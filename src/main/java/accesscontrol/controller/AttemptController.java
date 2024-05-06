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

    public AttemptController(EntityManager entityManager) {
        this.accessAttempt = new AccessAttempts(entityManager);
    }

    public void addAttempt(Request req, Response res){
        AttemptDto attemptDto = gson.fromJson(req.body(), AttemptDto.class);
        long attemptUID = attemptDto.getUid();
        boolean attemptBool = attemptDto.getAccess();
        LocalTime attemptTime = attemptDto.getTime();
        LocalDate attemptDate = attemptDto.getDate();

        AccessAttempt attempt = new AccessAttempt(attemptUID, attemptDate, attemptTime, attemptBool);

        accessAttempt.persist(attempt);
        res.type("application/json");

    }
}
