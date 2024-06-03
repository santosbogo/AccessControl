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

}
