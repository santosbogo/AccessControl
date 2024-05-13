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

    public ExitController(EntityManager entityManager){
        this.exitAttempts = new ExitAttempts(entityManager);
    }

    public void addExit(String date, String time){
        ExitButtonDto exitDto = new ExitButtonDto(date, time);
        LocalDate exitDate = exitDto.getDate();
        LocalTime exitTime = exitDto.getTime();

        ExitButton exitButton = new ExitButton(exitDate, exitTime);

        exitAttempts.persist(exitButton);
    }

}
