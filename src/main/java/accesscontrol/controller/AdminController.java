package accesscontrol.controller;

import accesscontrol.model.*;
import accesscontrol.queries.*;
import com.google.gson.*;

public class AdminController {
  private final Admins admins;
  private final Gson gson = new Gson();

  public AdminController() {
    this.admins = new Admins();
  }

  public void addAdmin(Admin admin) {
    admins.persist(admin);
  }

}
