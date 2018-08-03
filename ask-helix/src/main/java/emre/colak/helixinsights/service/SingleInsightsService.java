package emre.colak.helixinsights.service;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import emre.colak.helixinsights.model.AccessTokenRequest;
import emre.colak.helixinsights.model.AccessTokenResponse;
import emre.colak.helixinsights.model.Report;

public enum SingleInsightsService implements InsightsService {

  INSTANCE;

  private static final String REPORT_URI = "https://helix-app-interpretations.helix.com/v0/report/";
  
  private static final String ACCESS_TOKEN_URI = "https://store-api.helix.com/v0/oauth/access_token";
  
  private static final String BASIC_TOKEN = "Basic Y2wtZjBmYjdkOGUtZDQ1OS01OGM5LTgwNzQtOGRlYmIzMDJmZWFiOmNzLTkwM2ZiNzUwLTNmMjctNTkxNC1iZDk1LWU2ODgyMmE1YjMyOQ==";
  
  private final ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
  private String bearerToken;
  
  @Override
  public Report getReport() {
    if (bearerToken == null) {
      login();
    }
    
    System.out.println("Making a request to " + REPORT_URI);
    try {
      HttpResponse response = Request.Get(REPORT_URI).addHeader("Authorization", bearerToken).execute().returnResponse();
      if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
        System.out.println(bearerToken + "declined. Will attempt to login again");
        login();
      }
      byte[] bytes = Request.Get(REPORT_URI).addHeader("Authorization", bearerToken).execute().returnContent().asBytes();
      return om.readValue(bytes, Report.class);
    } catch (Exception ioe) {
      throw new RuntimeException(ioe);
    }
  }

  private void login() {
    bearerToken = "Bearer " + getAccessToken(new AccessTokenRequest(System.getenv("USERNAME"), System.getenv("PASSWORD"))).accessToken;
  }
  
  @Override
  public AccessTokenResponse getAccessToken(AccessTokenRequest request) {
    System.out.println("Making a request to " + ACCESS_TOKEN_URI);
    try {
      byte[] bytes = Request.Post(ACCESS_TOKEN_URI)
          .addHeader("Content-Type", "application/x-www-form-urlencoded")
          .addHeader("Authorization", BASIC_TOKEN)
          .bodyForm(new BasicNameValuePair("username", request.username), 
                    new BasicNameValuePair("password", request.password),
                    new BasicNameValuePair("scope", request.scope),
                    new BasicNameValuePair("grant_type", request.grantType))
          .execute().returnContent().asBytes();
      return om.readValue(bytes, AccessTokenResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
}
