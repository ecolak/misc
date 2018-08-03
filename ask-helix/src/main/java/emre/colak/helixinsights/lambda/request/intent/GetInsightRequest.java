package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.model.TraitReport;
import emre.colak.helixinsights.service.DefaultInsightsService;
import emre.colak.helixinsights.service.InsightsService;

public class GetInsightRequest extends IntentRequest {

  private final InsightsService service = new DefaultInsightsService();
  
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
    Report report = service.getReport();
    if (report == null) {
      return "Failed generating report";
    }
    TraitReport tr = report.traitReport(insight);
    if (tr != null) {
      return tr.traitDetails.userResult.body;
    }
    return "Failed fetching details for " + insight;
  }

  @Override
  public String toString() {
    return "GetInsightRequest [insight=" + insight + "]";
  }
  
  
}
