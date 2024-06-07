package accesscontrol.controller;

import com.google.gson.JsonObject;

public class UidController {
  private volatile String receivedUid = null;
  private final Object uidLock = new Object();

  public UidController() {
    // Initialization if needed
  }

  public void processNewUserUID(String messageString) {
    synchronized (uidLock) {
      receivedUid = messageString;
      uidLock.notify();
    }
  }

  public String requestUid(spark.Request req, spark.Response res) {
    synchronized (uidLock) {
      try {
        receivedUid = null;
        System.out.println("Waiting for UID...");

        while (receivedUid == null) {
          uidLock.wait(10000); // Espera hasta 10 segundos
        }

        if (receivedUid != null) {
          res.type("application/json");
          JsonObject json = new JsonObject();
          json.addProperty("uid", receivedUid);
          return json.toString();
        } else {
          res.status(408); // Request Timeout
          return "{}";
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        res.status(500);
        return "{}";
      }
    }
  }
}
