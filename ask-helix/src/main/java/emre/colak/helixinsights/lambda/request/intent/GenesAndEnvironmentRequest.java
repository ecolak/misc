package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.model.TraitReport;

public class GenesAndEnvironmentRequest extends InsightRequest {

  public GenesAndEnvironmentRequest(BaseRequest br, String insight) {
    super(br, insight);
  }
  
  @Override
  public String extractFromTraitReport(TraitReport tr) {
    return tr.traitDetails.genesEnvironment.body;
  }

  @Override
  public String toString() {
    return "GenesAndEnvironmentRequest [insight=" + insight + "]";
  }
}
