package accesscontrol.dto;
public class StateUserDto {
  String uid;
  String firstName;
  String lastName;
  Boolean state;
  public StateUserDto(String uid, String firstName, String lastName, Boolean state){
    this.uid = uid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.state = state;
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

    public boolean getState(){
        return state;
    }
}