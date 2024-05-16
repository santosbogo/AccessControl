package accesscontrol.dto;

public class LoginDto {

    private String username;
    private String password;

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginDto() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
