package accesscontrol.controller;

import accesscontrol.dto.*;
import accesscontrol.model.*;
import accesscontrol.queries.*;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AttemptController {
    private final AccessAttempts accessAttempt;
    private final Users users;
    private final Gson gson = new Gson();

    public AttemptController() {
        this.accessAttempt = new AccessAttempts();
        this.users = new Users();
    }

    public void addAttempt(String uid, LocalDate date, LocalTime time, boolean success) {
        AccessAttempt accessAttempt = new AccessAttempt(uid, date, time, success);
        this.accessAttempt.persist(accessAttempt);
    }

    public String getAttempts(Request request, Response response) {
        String selectedDate = request.queryParams("selectedDate");
        LocalDate date = LocalDate.parse(selectedDate); // Parsea la fecha directamente si está en formato ISO-8601 (yyyy-MM-dd)
        List<AccessAttempt> attempts = accessAttempt.findAttemptsByDate(date);
        List<HistoryAttemptDto> attemptDtos = new ArrayList<>();
        for (AccessAttempt attempt : attempts) {
            User user = users.findUserByUid(attempt.getUid());
            attemptDtos.add(new HistoryAttemptDto(String.valueOf(attempt.getAttemptStatus()), attempt.getUid(), attempt.getAttemptHour().toString(), attempt.getAttemptDate().toString(), user));
        }
        response.type("application/json");
        return gson.toJson(attemptDtos);
    }

}
