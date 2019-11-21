package ec.googleflights.lambda;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.googleflights.model.Query;
import ec.googleflights.model.result.QueryResponse;
import ec.googleflights.service.DefaultQueryService;
import ec.googleflights.service.QueryService;

public class SearchFlightsLambda implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  
  private static final ObjectMapper om = new ObjectMapper();
  static {
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
  
  private QueryService queryService = new DefaultQueryService();
  
  @Override
  public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
    APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
    
    String body = input.getBody();
    System.out.println("body: " + body);
    try {
      Query q = om.readValue(body, Query.class);
      System.out.print("Sending query" + q.toQueryString());
      QueryResponse qr = queryService.sendQuery(q);
      response.setStatusCode(200);
      String responseStr = om.writeValueAsString(qr);
      System.out.println("response: " + responseStr);
      response.setBody(responseStr);
      Map<String,String> headers = new HashMap<>();
      headers.put("Content-Type", "application/json");
      response.setHeaders(headers);
    } catch (JsonProcessingException jpe) {
      System.err.println(jpe);
      response.setStatusCode(400);
    } catch (Exception e) {
      System.err.println(e);
      response.setStatusCode(500);
    }
    return response;
  }

}
