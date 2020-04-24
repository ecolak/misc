package ec.logbook.lambda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.logbook.service.DynamoDbTripRepo;
import ec.logbook.service.ITripRepo;
import ec.logbook.service.auth.DefaultAuthService;
import ec.logbook.service.auth.UnauthorizedException;
import ec.logbookapi.model.Trip;

public class ListTripsLambda implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  private static final ObjectMapper om = new ObjectMapper();
  static {
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    om.setSerializationInclusion(Include.NON_NULL);
  }
  
  private ITripRepo tripRepo = new DynamoDbTripRepo();
  
  @Override
  public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
    System.out.println("Received ListTrips event with input: " + input);
    
    APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
    
    try {
      String userId = DefaultAuthService.INSTANCE.validateAuthorization(input);
      List<Trip> trips = tripRepo.getByUserId(userId);
      String responseStr = om.writeValueAsString(trips);
      System.out.println("response: " + responseStr);
      response.setBody(responseStr);
      Map<String,String> headers = new HashMap<>();
      headers.put("Content-Type", "application/json");
      response.setHeaders(headers);
      response.setStatusCode(200);
      Utils.addCorsHeadersToResponse(response);
    } catch (JsonProcessingException jpe) {
      System.err.println(jpe);
      response.setStatusCode(400);
    } catch (UnauthorizedException e) {
      System.err.println(e);
      response.setStatusCode(401);
    } catch (Exception e) {
      System.err.println(e);
      response.setStatusCode(500);
    }
    return response;
  }

}
