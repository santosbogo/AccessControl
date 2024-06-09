package accesscontrol.controller;

import accesscontrol.model.*;
import accesscontrol.queries.*;
import accesscontrol.service.*;

import com.google.gson.Gson;
import accesscontrol.dto.AuthenticationDto;
import accesscontrol.dto.LoginDto;
import spark.Request;
import spark.Response;

import java.util.Optional;

public class AuthenticationController {
  private final Gson gson = new Gson();
  private final Admins admins;
  private final TokenStore tokenStore = new TokenStore();

  public AuthenticationController() {
    this.admins = new Admins();
  }

  public String createAuthentication(Request req, Response res) {
    LoginDto loginDto = gson.fromJson(req.body(), LoginDto.class);

    if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
      res.status(400);
      return gson.toJson("Invalid username or password");
    }

    Admin admin = admins.findAdminByUsername(loginDto.getUsername());
    if (admin == null || !admin.getPassword().equals(loginDto.getPassword())) {
      res.status(404);
      return gson.toJson("User not found or password mismatch");
    }

    res.status(200);
    return gson.toJson(admin.asJson());
  }

  public String deleteAuthentication(Request req, Response res) {
    getToken(req).ifPresentOrElse(tokenStore::invalidateToken, () -> res.status(404));
    return "";
  }

  public String getCurrentUser(Request req, Response res) {
    Optional<String> tokenOpt = getToken(req);
    if (!tokenOpt.isPresent()) {
      res.status(401);
      return "Not signed in";
    }

    String username = tokenStore.getUsername(tokenOpt.get());
    if (username == null) {
      res.status(403);
      return "Invalid token";
    }

    Admin user = admins.findAdminByUsername(username);
    if (user == null) {
      res.status(404);
      return "User not found";
    }

    res.type("application/json");
    return user.asJson();
  }

  private static Optional<String> getToken(Request request) {
    return Optional.ofNullable(request.headers("Authorization")).map(auth -> auth.replace("Bearer ", ""));
  }
}
