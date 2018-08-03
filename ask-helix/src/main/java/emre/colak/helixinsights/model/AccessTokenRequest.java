package emre.colak.helixinsights.model;

public class AccessTokenRequest {

  public String username;
  public String password;
  public String scope = "customer";
  public String grantType = "password";
  
  public AccessTokenRequest() {}
  
  public AccessTokenRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
