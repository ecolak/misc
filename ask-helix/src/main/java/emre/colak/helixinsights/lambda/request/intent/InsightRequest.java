package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.model.TraitReport;
import emre.colak.helixinsights.service.DefaultInsightsService;
import emre.colak.helixinsights.service.InsightsService;

public abstract class InsightRequest extends IntentRequest {

  protected final InsightsService service = new DefaultInsightsService();
  
  protected String insight;
  
  public InsightRequest(BaseRequest br, String insight) {
    super(br);
    this.insight = insight;
  }

  public String getInsight() {
    return insight;
  }
  
  public abstract String extractFromTraitReport(TraitReport tr);
  
  @Override
  public String respondWithSSML() {
    Report report = service.getReport();
    if (report == null) {
      return "Failed generating report";
    }
    TraitReport tr = report.traitReport(insight);
    if (tr != null) {
      return extractFromTraitReport(tr);
    }
    return "Failed fetching details for " + insight;
  }
}
