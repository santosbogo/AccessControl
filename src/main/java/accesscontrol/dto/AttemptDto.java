package accesscontrol.dto;

public class AttemptDto {
    private String uid;
    private String access;
    private String time;
    private String date;

    // Constructor
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
        return Boolean.parseBoolean(access);
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }


}
