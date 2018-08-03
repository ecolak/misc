package emre.colak.helixinsights.main;

import emre.colak.helixinsights.model.AccessTokenRequest;
import emre.colak.helixinsights.model.AccessTokenResponse;
import emre.colak.helixinsights.service.SingleInsightsService;

public class Login {

  public static void main(String[] args) {
    AccessTokenResponse response = SingleInsightsService.INSTANCE.getAccessToken(new AccessTokenRequest(args[0], args[1]));
    System.out.println(response);
  }

}
