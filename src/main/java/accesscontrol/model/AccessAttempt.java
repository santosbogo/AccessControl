package accesscontrol.model;

import com.google.gson.Gson;

import javax.persistence.*;
import java.time.*;

@Entity
public class AccessAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    @Column(nullable = false)
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String cardId) {
        this.uid = cardId;
    }

    @Column(nullable = false)
    private LocalDate attemptDate;

    public LocalDate getAttemptDate() {
        return attemptDate;
    }

    public void setDate(LocalDate date) {
        this.attemptDate = date;
    }

    @Column(nullable = false)
    private LocalTime attemptHour;

    public LocalTime getAttemptHour() {
        return attemptHour;
    }

    public void setHour(LocalTime hour) {
        this.attemptHour = hour;
    }


    @Column(nullable = false)
    private boolean attemptStatus; // Rechazada(false) o Aprobada(true)

    public boolean getAttemptStatus() {
        return attemptStatus;
    }

    public void setAttemptStatus(boolean attemptStatus) {
        this.attemptStatus = attemptStatus;
    }

    // Constructor
    public AccessAttempt(String uid, LocalDate attemptDate, LocalTime attemptHour, boolean attemptStatus) {
        setUid(uid);
        setDate(attemptDate);
        setHour(attemptHour);
        setAttemptStatus(attemptStatus);
    }

    public AccessAttempt() {
    }
    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}