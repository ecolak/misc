package emre.colak.leftoverpolice.lambda.request.intent;

import emre.colak.leftoverpolice.lambda.Utils;
import emre.colak.leftoverpolice.lambda.request.BaseRequest;
import emre.colak.leftoverpolice.lambda.request.IntentRequest;
import emre.colak.leftoverpolice.service.ILeftoverService;

public class RemoveAllLeftoversRequest extends IntentRequest {

  public RemoveAllLeftoversRequest(BaseRequest br) {
    super(br);
  }

  @Override
  public String respondWithSSML() {
    ILeftoverService s = Utils.dbService();
    int numRows = s.deleteAll();
    String ssml = numRows > 0 ? "OK" : "You don't have anything to remove";
    return ssml;
  }

  @Override
  public String toString() {
    return "RemoveAllLeftoversRequest []";
  }
}
