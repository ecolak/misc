package emre.colak.helixinsights.lambda.request.intent;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;

public class ReadyRequest extends IntentRequest {

  public ReadyRequest(BaseRequest br) {
    super(br);
  }
  
  @Override
  public String respondWithSSML() {
    return "It sure is!";
  }

  @Override
  public String toString() {
    return "ReadyRequest []";
  }

}
