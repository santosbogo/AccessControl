package accesscontrol.dto;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeMessage {
    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    public LocalDate getDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, dateFormatter);
    }

    public LocalTime getTime() {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
