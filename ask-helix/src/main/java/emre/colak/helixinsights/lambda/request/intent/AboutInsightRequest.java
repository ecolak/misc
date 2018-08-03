package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.model.TraitReport;

public class AboutInsightRequest extends InsightRequest {

  public AboutInsightRequest(BaseRequest br, String insight) {
    super(br, insight);
  }
  
  @Override
  public String extractFromTraitReport(TraitReport tr) {
    return tr.traitDetails.aboutScience.body;
  }

  @Override
  public String toString() {
    return "AboutInsightRequest [insight=" + insight + "]";
  }
  
}
