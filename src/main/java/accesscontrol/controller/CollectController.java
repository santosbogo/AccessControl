package accesscontrol.controller;

import accesscontrol.dto.*;
import accesscontrol.model.*;
import accesscontrol.queries.*;
import com.google.gson.*;

import java.util.*;


public class CollectController {
  /*

  private final Users users;
  private final Gson gson = new Gson();
  private MqttClient client;
  public CollectController(MqttClient client) {
    this.users = new Users();
    this.client = client;
  }
  public void collectAndPublishUsers() {
    List<User> activeUsers = users.findAllActive();
    List<UserDto> userDtos = new ArrayList<>();
    for(User user : activeUsers){
      userDtos.add(new UserDto(user.getUid(), user.getFirstName(), user.getLastName()));
    }
    try {
      MqttMessage message = new MqttMessage(userDtos.toString().getBytes());
      client.publish("admittedUsers", message);
    } catch (MqttException e) {
      System.err.println("Error publishing active users: " + e.getMessage());
      e.printStackTrace();
    }
  }

 */
}
