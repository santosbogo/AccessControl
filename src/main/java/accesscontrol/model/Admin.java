package accesscontrol.model;

import com.google.gson.*;
import javax.persistence.*;
import com.google.gson.Gson;
import javax.persistence.*;

@Entity
public class Admin {
  @Id
  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String password;

  public Admin(String username, String firstName, String lastName, String password) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
  }

  public Admin() {
  }

  public String asJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

}
