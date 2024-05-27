package accesscontrol.dto;
public class UserDto {
  String uid;
  String name;
  String lastName;
  public UserDto(String uid, String name, String lastName){
    this.uid = uid;
    this.name = name;
    this.lastName = lastName;
  }

  public String getUid(){
    return uid;
  }

  public String getName(){
    return name;
  }

  public String getLastName(){
    return lastName;
  }

}