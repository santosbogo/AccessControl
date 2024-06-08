package accesscontrol.service;
import java.util.*;

public class TokenStore {
  private static final long EXPIRATION_LIMIT = 30 * 60 * 1000; // 30 minutes in milliseconds

  private final Map<String, TokenInfo> tokens = new HashMap<>();

  public String generateToken(String username) {
    String token = UUID.randomUUID().toString();
    tokens.put(token, new TokenInfo(username, new Date().getTime() + EXPIRATION_LIMIT));
    return token;
  }

  public String getUsername(String token) {
    TokenInfo info = tokens.get(token);
    if (info != null && new Date().getTime() < info.expirationTime) {
      return info.username;
    }
    tokens.remove(token); // Remove expired token
    return null;
  }

  public void invalidateToken(String token) {
    tokens.remove(token);
  }

  private static class TokenInfo {
    String username;
    long expirationTime;

    TokenInfo(String username, long expirationTime) {
      this.username = username;
      this.expirationTime = expirationTime;
    }
  }
}
