package emre.colak.leftoverpolice.lambda.request;

import java.util.Objects;

/**
 * {
    "type": "LaunchRequest",
    "requestId": "amzn1.echo-api.request.0000000-0000-0000-0000-00000000000",
    "timestamp": "2015-05-13T12:34:56Z",
    "locale": "string"
  }
 * */
public class LaunchRequest implements IRequest {

  private BaseRequest br;
  
  public LaunchRequest(BaseRequest br) {
    Objects.requireNonNull(br);
    this.br = br;
  }
    
  public LaunchRequest(String requestId, String timestamp, String locale) {
    br = new BaseRequest(requestId, timestamp, locale);
  }
  
  @Override
  public String getRequestId() {
    return br.getRequestId();
  }

  @Override
  public String getTimestamp() {
    return br.getTimestamp();
  }

  @Override
  public String getLocale() {
    return br.getLocale();
  }

}
