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

  public Admin(String firstName, String lastName, String username,  String password) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
  }

  public Admin() {
  }

  public String asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("firstName", this.firstName);
    json.addProperty("lastName", this.lastName);
    json.addProperty("username", this.username);
    json.addProperty("password", this.password);
    return json.toString();
  }

}
