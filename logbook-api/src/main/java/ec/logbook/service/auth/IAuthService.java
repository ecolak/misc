package ec.logbook.service.auth;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;

public interface IAuthService {

  String validateAuthorization(APIGatewayV2ProxyRequestEvent input) throws UnauthorizedException;
}
