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
    Admin admin = new Admin(adminDto.getUsername(), adminDto.getFirstName(), adminDto.getLastName(), adminDto.getPassword());
    admins.persist(admin);
    response.type("application/json");
    return admin.asJson();
  }
}
