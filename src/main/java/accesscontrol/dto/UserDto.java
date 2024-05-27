package accesscontrol.dto;
public class UserDto {
  String uid;
  String firstName;
  String lastName;
  public UserDto(String uid, String firstName, String lastName){
    this.uid = uid;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getUid(){
    return uid;
  }

  public String getName(){
    return firstName;
  }

  public String getLastName(){
    return lastName;
  }

}