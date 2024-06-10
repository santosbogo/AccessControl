package accesscontrol.controller;

import accesscontrol.dto.*;
import accesscontrol.model.*;
import accesscontrol.queries.ExitAttempts;
import com.google.gson.Gson;
import spark.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ExitController {
    private final Gson gson = new Gson();
    private final ExitAttempts exitAttempts;

    public ExitController(){
        this.exitAttempts = new ExitAttempts();
    }

    public void addExit(LocalDate date, LocalTime time){
        ExitButton exitButton = new ExitButton(date, time);
        exitAttempts.persist(exitButton);
    }

    public String getExits(Request request, Response response){
        String selectedDate = request.queryParams("selectedDate");
        LocalDate date = LocalDate.parse(selectedDate); // Parsea la fecha directamente si está en formato ISO-8601 (yyyy-MM-dd)
        List<ExitButton> exitButtons = exitAttempts.findExitsByDate(date);
        List<ExitButtonDto> exitDtos = new ArrayList<>();
        for (ExitButton exit : exitButtons) {
            exitDtos.add(new ExitButtonDto(exit.getExitDate().toString(), exit.getExitTime().toString()));
        }
        response.type("application/json");
        return gson.toJson(exitDtos);
    }
}