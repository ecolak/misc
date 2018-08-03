package emre.colak.helixinsights.lambda.request.intent;

import java.util.List;
import java.util.stream.Collectors;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.model.TraitReport;
import emre.colak.helixinsights.service.DefaultInsightsService;
import emre.colak.helixinsights.service.InsightsService;
import emre.colak.helixinsights.util.AlexaUtils;

public class RecommendProductsRequest extends IntentRequest {

  private final InsightsService service = new DefaultInsightsService();
  
  private String insight;
  
  public RecommendProductsRequest(BaseRequest br, String insight) {
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
      List<String> products = tr.traitDetails.recommendedProducts.products.stream().map(p -> p.productName).collect(Collectors.toList());
      int numProducts = products.size();
      if (numProducts == 0) {
        return "Helix insights cannot recommend you any products for " + insight + " right now. Check back later";
      }
      return AlexaUtils.listToSSML(products, 
          String.format("Helix insights recommends you %d product%s for %s", numProducts, (numProducts > 1 ? "s" : ""),  insight));
    }
    return "Failed fetching details for " + insight;
  }

}
