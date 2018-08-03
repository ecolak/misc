package emre.colak.helixinsights.lambda.request.intent;

import java.util.List;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.service.DefaultInsightsService;
import emre.colak.helixinsights.service.InsightsService;
import emre.colak.helixinsights.util.AlexaUtils;

public class ListInsightsRequest extends IntentRequest {

  private final InsightsService service = new DefaultInsightsService();
  
  public ListInsightsRequest(BaseRequest br) {
    super(br);
  }

  @Override
  public String respondWithSSML() {
    Report report = service.getReport();
    if (report == null) {
      return "Nothing";
    }
    List<String> traitNames = report.traitNames();
    System.out.println("trait names: " + traitNames);
    return AlexaUtils.listToSSML(traitNames, "You have insights for the following traits available");
  }

  @Override
  public String toString() {
    return "ListInsightsRequest []";
  }
  
}
