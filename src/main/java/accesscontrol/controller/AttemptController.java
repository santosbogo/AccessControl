package accesscontrol.controller;

import accesscontrol.dto.*;
import com.google.gson.Gson;
import accesscontrol.queries.AccessAttempts;

import accesscontrol.model.AccessAttempt;
import spark.Request;
import spark.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


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

    public String getAttempts(Request request, Response response) {
        LocalDate date = LocalDate.parse(request.params(":date"));
        List<AccessAttempt> attempts = accessAttempt.findAttemptsByDate(date);
        List<AttemptDto> attemptDtos = new ArrayList<>();
        for(AccessAttempt attempt : attempts) {
            attemptDtos.add(new AttemptDto(attempt.getUid().toString(), attempt.getAttemptDate().toString(), attempt.getAttemptHour().toString(), String.valueOf(attempt.getAttemptStatus())));
        }
        response.type("application/json");
        return gson.toJson(attemptDtos);
    }
}
