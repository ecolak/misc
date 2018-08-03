package emre.colak.helixinsights.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

  @JsonProperty("access_token")
  public String accessToken;
  
  @JsonProperty("token_type")
  public String tokenType;
  
  @JsonProperty("expires_in")
  public Integer expiresIn; 
  
  public String scope;

  @Override
  public String toString() {
    return "AccessTokenResponse [accessToken=" + accessToken + ", tokenType=" + tokenType
        + ", expiresIn=" + expiresIn + ", scope=" + scope + "]";
  }
}
