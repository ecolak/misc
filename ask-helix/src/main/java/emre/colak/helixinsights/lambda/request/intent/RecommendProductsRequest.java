package emre.colak.helixinsights.lambda.request.intent;

import java.util.List;
import java.util.stream.Collectors;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.model.TraitReport;
import emre.colak.helixinsights.util.AlexaUtils;

public class RecommendProductsRequest extends InsightRequest {
  
  public RecommendProductsRequest(BaseRequest br, String insight) {
    super(br, insight);
  }

  public String getInsight() {
    return insight;
  }
  
  @Override
  public String extractFromTraitReport(TraitReport tr) {
    List<String> products = tr.traitDetails.recommendedProducts.products.stream().map(p -> p.productName).collect(Collectors.toList());
    int numProducts = products.size();
    if (numProducts == 0) {
      return "Helix insights cannot recommend you any products for " + insight + " right now. Check back later";
    }
    return AlexaUtils.listToSSML(products, 
        String.format("Helix insights recommends you %d product%s for %s", numProducts, (numProducts > 1 ? "s" : ""),  insight));
  }

  @Override
  public String toString() {
    return "RecommendProductsRequest [insight=" + insight + "]";
  }
  
}
