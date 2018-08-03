package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.model.TraitReport;
import emre.colak.helixinsights.service.SingleInsightsService;

public abstract class InsightRequest extends IntentRequest {
  
  protected String insight;
  
  public InsightRequest(BaseRequest br, String insight) {
    super(br);
    // convert hyphens to spaces
    // e.g. Alexa turns "risk taking behavior" to "risk-taking behavior"
    this.insight = insight.replaceAll("-", " ");
  }

  public String getInsight() {
    return insight;
  }
  
  public abstract String extractFromTraitReport(TraitReport tr);
  
  @Override
  public String respondWithSSML() {
    Report report = SingleInsightsService.INSTANCE.getReport();
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
