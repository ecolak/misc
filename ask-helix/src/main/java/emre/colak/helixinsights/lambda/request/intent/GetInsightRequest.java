package emre.colak.helixinsights.lambda.request.intent;

import java.util.Objects;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;

public class GetInsightRequest extends IntentRequest {

  private String insight;
  
  public GetInsightRequest(BaseRequest br, String insight) {
    super(br);
    this.insight = insight;
  }

  public String getInsight() {
    return insight;
  }
  
  @Override
  public String respondWithSSML() {
    // Utils.leftoversToSSML(leftovers);
    return "OK";
  }
  
  public static GetInsightRequest fromQuery(BaseRequest br, String query) {
    Objects.requireNonNull(query);
    return new GetInsightRequest(br, query);
  }
}
