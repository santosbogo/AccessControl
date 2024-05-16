package accesscontrol.dto;
public class UserDto {
  String name;
  String lastName;
  String username;
  public UserDto(String name, String lastName, String username){
    this.name = name;
    this.lastName = lastName;
    this.username = username;
  }
  public String getName(){
    return name;
  }

  public String getLastName(){
    return lastName;
  }

  public String getUsername(){
    return username;
  }

}