package emre.colak.helixinsights.service;

import emre.colak.helixinsights.model.AccessTokenRequest;
import emre.colak.helixinsights.model.AccessTokenResponse;
import emre.colak.helixinsights.model.Report;

public interface InsightsService {

  AccessTokenResponse getAccessToken(AccessTokenRequest request);
  
  Report getReport();
  
}
