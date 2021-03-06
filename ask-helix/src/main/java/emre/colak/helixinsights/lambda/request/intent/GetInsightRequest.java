package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.model.TraitReport;

public class GetInsightRequest extends InsightRequest {
  
  public GetInsightRequest(BaseRequest br, String insight) {
    super(br, insight);
  }
  
  @Override
  public String extractFromTraitReport(TraitReport tr) {
    return tr.traitDetails.userResult.body;
  }

  @Override
  public String toString() {
    return "GetInsightRequest [insight=" + insight + "]";
  }
  
  
}
