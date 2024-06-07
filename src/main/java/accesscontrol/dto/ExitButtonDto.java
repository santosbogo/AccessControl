package accesscontrol.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExitButtonDto {
    private String exitDate;
    private String time;

    public ExitButtonDto(String exitDate, String time){
        this.time = time;
        this.exitDate = exitDate;
    }

    public LocalTime getTime(){
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public LocalDate getDate(){
        return LocalDate.parse(exitDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}


