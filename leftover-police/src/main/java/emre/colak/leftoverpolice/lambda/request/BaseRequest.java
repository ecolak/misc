package emre.colak.leftoverpolice.lambda.request;

import java.util.Map;

public class BaseRequest {

  private final String requestId;
  private final String timestamp;
  private final String locale;
  
  public BaseRequest(String requestId, String timestamp, String locale) {
    this.requestId = requestId;
    this.timestamp = timestamp;
    this.locale = locale;
  }

  public String getRequestId() {
    return requestId;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getLocale() {
    return locale;
  }
  
  public static BaseRequest fromRequestJson(Map<String, Object> json) {
    Object objReqId = json.get("requestId");
    String requestId = objReqId != null && String.class.isInstance(objReqId) ? (String)objReqId : null;
    Object objTs = json.get("timestamp");
    String timestamp = objTs != null && String.class.isInstance(objTs) ? (String)objTs : null;
    Object objLoc = json.get("locale");
    String locale = objLoc != null && String.class.isInstance(objLoc) ? (String)objLoc : null;
    
    return new BaseRequest(requestId, timestamp, locale);
  }
}
