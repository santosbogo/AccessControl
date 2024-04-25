package accesscontrol.model;

import com.google.gson.Gson;

import javax.persistence.*;

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
    private Long uid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long cardId) {
        this.uid = cardId;
    }

    @Column(nullable = false)
    private String attemptDate;

    public String getAttemptDate() {
        return attemptDate;
    }

    public void setDate(String date) {
        this.attemptDate = date;
    }

    @Column(nullable = false)
    private String attemptHour;

    public String getAttemptHour() {
        return attemptHour;
    }

    public void setHour(String hour) {
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
    public AccessAttempt(Long attemptId, Long uid, String attemptDate, String attemptHour, boolean attemptStatus) {
        setAttemptId(attemptId);
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
