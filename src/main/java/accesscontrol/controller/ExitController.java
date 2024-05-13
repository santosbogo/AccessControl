package accesscontrol.controller;

import accesscontrol.dto.ExitButtonDto;
import accesscontrol.model.ExitButton;
import accesscontrol.queries.ExitAttempts;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

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

}
