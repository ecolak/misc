package emre.colak.leftoverpolice.lambda;

import java.util.HashMap;
import java.util.Map;

import emre.colak.leftoverpolice.lambda.request.BaseRequest;
import emre.colak.leftoverpolice.lambda.request.IRequest;
import emre.colak.leftoverpolice.lambda.request.IntentRequest;
import emre.colak.leftoverpolice.lambda.request.LaunchRequest;
import emre.colak.leftoverpolice.lambda.request.SessionEndedRequest;

/**
 * {
  "version": "1.0",
  "session": {
    "new": false,
    "sessionId": "amzn1.echo-api.session.0000000-0000-0000-0000-00000000000",
    "application": {
      "applicationId": "amzn1.echo-sdk-ams.app.000000-d0ed-0000-ad00-000000d00ebe"
    },
    "attributes": {
      "supportedHoroscopePeriods": {
        "daily": true,
        "weekly": false,
        "monthly": false
      }
    },
    "user": {
      "userId": "amzn1.account.AM3B00000000000000000000000"
    }
  },
  "context": {
    "System": {
      "application": {
        "applicationId": "amzn1.echo-sdk-ams.app.000000-d0ed-0000-ad00-000000d00ebe"
      },
      "user": {
        "userId": "amzn1.account.AM3B00000000000000000000000"
      },
      "device": {
        "supportedInterfaces": {
          "AudioPlayer": {}
        }
      }
    },
    "AudioPlayer": {
      "offsetInMilliseconds": 0,
      "playerActivity": "IDLE"
    }
  },
  "request": {}
}
 * */
public class AlexaRequest {

  private static final String LAUNCH_REQUEST = "LaunchRequest";
  private static final String INTENT_REQUEST = "IntentRequest";
  private static final String SESSION_ENDED_REQUEST = "SessionEndedRequest";
  
  public String version;
  public Map<String, Object> session = new HashMap<>();
  public Map<String, Object> context = new HashMap<>();
  public Map<String, Object> request = new HashMap<>();
  
  public static IRequest getTypedRequest(Map<String, Object> request) {
    Object objType = request.get("type");
    if (objType == null || !String.class.isInstance(objType))  {
      throw new IllegalArgumentException("Request type not found");
    }
    String type = (String) objType;
    final IRequest result;
    BaseRequest br = BaseRequest.fromRequestJson(request);
    if (LAUNCH_REQUEST.equals(type)) {
      result = new LaunchRequest(br);
    } else if (INTENT_REQUEST.equals(type)) {
      result = new IntentRequest(br);
    } else if (SESSION_ENDED_REQUEST.equals(type)) {
      result = new SessionEndedRequest(br);
    } else {
      throw new IllegalArgumentException("Invalid request type");
    }
    return result;
  }
}
