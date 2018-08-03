package emre.colak.helixinsights.service;

import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import emre.colak.helixinsights.model.Report;

public class DefaultInsightsService implements InsightsService {

  private static final String URI = "https://helix-app-interpretations.helix.com/v0/report/";
      
  private final ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
  @Override
  public Report getReport() {
    System.out.println("Making a request to " + URI);
    try {
      byte[] bytes = Request.Get(URI).addHeader("Authorization", System.getenv("AUTH_TOKEN")).execute().returnContent().asBytes();
      return om.readValue(bytes, Report.class);
    } catch (Exception ioe) {
      throw new RuntimeException(ioe);
    }
  }

}
