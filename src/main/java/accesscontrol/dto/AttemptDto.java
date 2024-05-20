package accesscontrol.dto;

import java.time.*;
import java.time.format.*;

public class AttemptDto {
    private String uid;
    private String date;
    private String time;
    private String status;

    public AttemptDto(String uid, String date, String time, String status) {
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public LocalDate getDate() {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    public LocalTime getTime() {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public Boolean getStatus() {
        return Boolean.parseBoolean(status);
    }
}

