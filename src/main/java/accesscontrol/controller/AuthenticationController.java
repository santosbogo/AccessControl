package accesscontrol.controller;

import accesscontrol.model.*;
import accesscontrol.queries.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import accesscontrol.dto.AuthenticationDto;
import accesscontrol.dto.LoginDto;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MINUTES;

public class AuthenticationController {
  private final Gson gson = new Gson();
  private final Admins admins;
  private final Cache<String, String> usernameByToken = CacheBuilder.newBuilder().expireAfterWrite(30, MINUTES).build();

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

    if(admin == null){
      res.status(400);
      return "User does not exist";
    }

    if (admin.getPassword().equals(loginDto.getPassword())) {
      Optional<String> existingToken = usernameByToken.asMap().entrySet().stream()
          .filter(entry -> entry.getValue().equals(admin.getUsername()))
          .map(Map.Entry::getKey)
          .findFirst();

      if (existingToken.isPresent()) {
        usernameByToken.invalidate(existingToken.get());
      }

      final String token = UUID.randomUUID().toString();
      usernameByToken.put(token, admin.getUsername());

      AuthenticationDto authenticationDto = new AuthenticationDto(admin, token);
      res.status(200);
      return gson.toJson(authenticationDto);

    } else {
      res.status(404);
      return gson.toJson("User not found");
    }
  }


  public String deleteAuthentication(Request req, Response res){
    getToken(req)
        .ifPresentOrElse(token -> {
          usernameByToken.invalidate(token);
          res.status(204);
        }, () -> {
          res.status(404);
        });

    return "";
  }

  private static Optional<String> getToken(Request request) {
    return Optional.ofNullable(request.headers("Authorization")).map(AuthenticationController::getTokenFromHeader);
  }

  private static String getTokenFromHeader(String authorizationHeader) {
    return authorizationHeader.replace("Bearer ", "");
  }

  private boolean isAuthenticated(String token) {
    return usernameByToken.getIfPresent(token) != null;
  }

  public String getCurrentUser(Request req, Response res) {
    Optional<String> tokenOpt = getToken(req);

    if (!tokenOpt.isPresent()) {
      res.status(401);
      return "Not signed in";
    }

    String token = tokenOpt.get();
    String username = usernameByToken.getIfPresent(token);

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
}
