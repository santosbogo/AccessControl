package accesscontrol.controller;

import accesscontrol.dto.*;
import accesscontrol.model.*;
import accesscontrol.queries.*;
import com.google.gson.*;
import spark.*;

public class AdminController {
  private final Admins admins;
  private final Gson gson = new Gson();

  public AdminController() {
    this.admins = new Admins();
  }

  public String createAdmin(Request request, Response response) {
    AdminDto adminDto = gson.fromJson(request.body(), AdminDto.class);
    String firstName = adminDto.getFirstName();
    String lastName = adminDto.getLastName();
    String username = adminDto.getUsername();
    String password = adminDto.getPassword();

    if(admins.findAdminByUsernam(username)!=null){
      response.status(400);
      return "Username or email already registered";}

    Admin admin = new Admin(firstName, lastName, username, password);
    admins.persist(admin);
    response.type("application/json");
    return admin.asJson();
  }

  public String loginAdmin(Request request, Response response) {
    LoginDto loginDto = gson.fromJson(request.body(), LoginDto.class);
    String username = loginDto.getUsername();
    String password = loginDto.getPassword();
    Admin admin = admins.findAdminByUsernameAndPassword(username, password);
    if(admin == null){
      response.status(400);
      return "Invalid username or password";
    }
    response.type("application/json");
    return admin.asJson();
  }
}