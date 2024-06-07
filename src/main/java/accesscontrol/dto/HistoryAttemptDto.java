package accesscontrol.dto;

import accesscontrol.model.*;

import java.time.*;
import java.time.format.*;

public class HistoryAttemptDto {
  private String cardID;
  private String date;
  private String time;
  private String state;
  private String firstName;
  private String lastName;

  public HistoryAttemptDto(String state, String cardID, String time, String date, User user) {
    this.cardID = cardID;
    this.date = date;
    this.time = time;
    this.state = state;
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
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
    return Boolean.parseBoolean(state);
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

}

