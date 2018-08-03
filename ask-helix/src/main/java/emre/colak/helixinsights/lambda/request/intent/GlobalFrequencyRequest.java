package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.model.TraitReport;

public class GlobalFrequencyRequest extends InsightRequest {
  
  public GlobalFrequencyRequest(BaseRequest br, String insight) {
    super(br, insight);
  }
  
  @Override
  public String extractFromTraitReport(TraitReport tr) {
    return tr.traitDetails.globalFrequency.body;
  }

  @Override
  public String toString() {
    return "GlobalFrequencyRequest [insight=" + insight + "]";
  }
  
}
