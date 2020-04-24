package ec.logbook.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

public class Utils {

  private Utils() {}
  
  public static void addCorsHeadersToResponse(APIGatewayV2ProxyResponseEvent response) {
    Map<String, String> headers = response.getHeaders();
    if (headers == null) {
      headers = new HashMap<>();
    }
    headers.put("Access-Control-Allow-Origin", "*");
    response.setHeaders(headers);
  }
}
