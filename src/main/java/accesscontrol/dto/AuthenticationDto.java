package accesscontrol.dto;


import accesscontrol.model.*;

public class AuthenticationDto {
  private String username;
  private String password;
  private String token;

  public AuthenticationDto(Admin admin, String token){
    this.username = admin.getUsername();
    this.password = admin.getPassword();
    this.token = token;
  }
  public String getUsername(){
    return username;
  }

  public String getPassword(){
    return password;
  }

  public String getToken(){
    return token;
  }

}
