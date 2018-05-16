package emre.colak.leftoverpolice.lambda.request.intent;

import java.util.List;

import emre.colak.leftoverpolice.lambda.Utils;
import emre.colak.leftoverpolice.lambda.request.BaseRequest;
import emre.colak.leftoverpolice.lambda.request.IntentRequest;
import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService;

public class ListLeftoversRequest extends IntentRequest {

  public ListLeftoversRequest(BaseRequest br) {
    super(br);
  }

  @Override
  public String respondWithSSML() {
    ILeftoverService s = Utils.dbService();
    List<Leftover> leftovers = s.list();
    String ssml = leftovers.isEmpty() ? "Nothing" : Utils.leftoversToSSML(leftovers);
    return ssml;
  }

  @Override
  public String toString() {
    return "ListLeftoversRequest []";
  }

}
