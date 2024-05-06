package accesscontrol.dto;

import java.time.*;
import java.time.format.*;

public class AttemptDto {
    private String uid;
    private String access;
    private String time;
    private String date;

    public AttemptDto(String uid, String access, String time, String date) {
        this.uid = uid;
        this.access = access;
        this.time = time;
        this.date = date;
    }

    public Long getUid() {
        return Long.parseLong(uid);
    }

    public boolean getAccess() {
        int accessNum = Integer.parseInt(access);
        if(accessNum == 1) return true;
        else return false;
    }
    public LocalTime getTime() {
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public LocalDate getDate() {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }


}

