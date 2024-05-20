package accesscontrol.dto;
public class UserDto {
  String name;
  String lastName;
  public UserDto(String name, String lastName, String username){
    this.name = name;
    this.lastName = lastName;
  }
  public String getName(){
    return name;
  }

  public String getLastName(){
    return lastName;
  }

}