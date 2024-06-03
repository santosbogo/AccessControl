package accesscontrol.dto;

import java.time.*;
import java.time.format.*;

public class AttemptDto {
    private String cardID;
    private String date;
    private String time;
    private String state;
    private String personID;

    public AttemptDto(String state, String personID, String cardID, String time, String date) {
        this.cardID = cardID;
        this.date = date;
        this.time = time;
        this.state = state;
        this.personID = personID;
    }

    public String getCardId() {
        return cardID;
    }

    public LocalDate getDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, dateFormatter);
    }


    public LocalTime getTime() {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public Boolean getState() {
        return "1".equals(state);
    }

    public String getPersonId() {
        return personID;
    }
}

