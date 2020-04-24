package ec.logbook.service.auth;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;

public enum DefaultAuthService implements IAuthService {

  INSTANCE;
  
  private static final String API_KEY = "S3tWy6Qkqj37LGtD";
  
  @Override
  public String validateAuthorization(APIGatewayV2ProxyRequestEvent input) throws UnauthorizedException {
    Map<String, String> headers = input.getHeaders();
    String apiKey = headers.getOrDefault("Authorization", "");
    if (!API_KEY.equals(apiKey)) {
      throw new UnauthorizedException();
    }
    String userId = headers.getOrDefault("x-user-id", "");
    if ("".equals(userId)) {
      throw new UnauthorizedException();
    }
    return userId;
  }

}
