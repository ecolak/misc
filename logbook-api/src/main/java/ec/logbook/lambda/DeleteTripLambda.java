package ec.logbook.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

import ec.logbook.service.DynamoDbTripRepo;
import ec.logbook.service.ITripRepo;
import ec.logbook.service.auth.DefaultAuthService;
import ec.logbook.service.auth.UnauthorizedException;

public class DeleteTripLambda implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

  private ITripRepo tripRepo = new DynamoDbTripRepo();
  
  @Override
  public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
    APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
    
    String tripId = input.getPathParameters().getOrDefault("id", "");
    if ("".equals(tripId)) {
      response.setStatusCode(400);
    }
    
    try {
      String userId = DefaultAuthService.INSTANCE.validateAuthorization(input);
      tripRepo.deleteByUserIdAndTripId(userId, tripId);
      response.setStatusCode(200); 
      Utils.addCorsHeadersToResponse(response);
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
