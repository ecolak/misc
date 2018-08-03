package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.model.TraitReport;

public class GeneticImpactRequest extends InsightRequest {

  public GeneticImpactRequest(BaseRequest br, String insight) {
    super(br, insight);
  }
  
  @Override
  public String extractFromTraitReport(TraitReport tr) {
    return tr.traitDetails.geneticImpact.body;
  }

  @Override
  public String toString() {
    return "GeneticImpactRequest [insight=" + insight + "]";
  }
}
