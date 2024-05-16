package accesscontrol.dto;
import com.google.gson.annotations.SerializedName;

public class DateTimeMessage {
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;

    // Getters
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
