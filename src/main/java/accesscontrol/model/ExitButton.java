package accesscontrol.model;

import com.google.gson.Gson;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class ExitButton {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exitId;

    public Long getAttemptId() {
        return exitId;
    }

    @Column(nullable = false)
    private LocalDate exitDate;

    public LocalDate getExitDate() {
        return exitDate;
    }

    public void setExitDate(LocalDate exitDate) {
        this.exitDate = exitDate;
    }

    @Column(nullable = false)
    private LocalTime exitTime;

    public LocalTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = exitTime;
    }

    public ExitButton(LocalDate exitDate, LocalTime exitTime){
        setExitDate(exitDate);
        setExitTime(exitTime);
    }

    public ExitButton() {
    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}

