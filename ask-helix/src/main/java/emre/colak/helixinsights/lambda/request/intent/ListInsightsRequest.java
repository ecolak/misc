package emre.colak.helixinsights.lambda.request.intent;

import java.util.Arrays;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.model.Insight;
import emre.colak.helixinsights.util.AlexaUtils;

public class ListInsightsRequest extends IntentRequest {

  public ListInsightsRequest(BaseRequest br) {
    super(br);
  }

  @Override
  public String respondWithSSML() {
    return AlexaUtils.listToSSML(Arrays.asList(Insight.values()));
  }

  @Override
  public String toString() {
    return "ListInsightsRequest []";
  }
  
}
