package ec.logbook.lambda;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.logbook.service.DynamoDbTripRepo;
import ec.logbook.service.ITripRepo;
import ec.logbook.service.auth.DefaultAuthService;
import ec.logbook.service.auth.UnauthorizedException;
import ec.logbookapi.model.Trip;

public class CreateTripLambda implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  private static final ObjectMapper om = new ObjectMapper();
  static {
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
  
  private ITripRepo tripRepo = new DynamoDbTripRepo();
  
  @Override
  public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
    APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
    
    // TODO: Add logging
    String body = input.getBody();
    
    try {
      String userId = DefaultAuthService.INSTANCE.validateAuthorization(input);
      
      Trip trip = om.readValue(body, Trip.class);
      String tripId = trip.getId();
      if ("".equals(tripId)) {
        trip.setId(UUID.randomUUID().toString());
      }

      trip.setCreatedAt(System.currentTimeMillis());
      trip.setUserId(userId);
      tripRepo.create(trip);
    
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
