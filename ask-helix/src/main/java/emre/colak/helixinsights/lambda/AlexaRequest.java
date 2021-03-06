package emre.colak.helixinsights.lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emre.colak.helixinsights.lambda.request.BaseRequest;
import emre.colak.helixinsights.lambda.request.IRequest;
import emre.colak.helixinsights.lambda.request.IntentRequest;
import emre.colak.helixinsights.lambda.request.LaunchRequest;
import emre.colak.helixinsights.lambda.request.SessionEndedRequest;
import emre.colak.helixinsights.lambda.request.intent.AboutInsightRequest;
import emre.colak.helixinsights.lambda.request.intent.ConfirmationStatus;
import emre.colak.helixinsights.lambda.request.intent.GenesAndEnvironmentRequest;
import emre.colak.helixinsights.lambda.request.intent.GeneticImpactRequest;
import emre.colak.helixinsights.lambda.request.intent.GetInsightRequest;
import emre.colak.helixinsights.lambda.request.intent.GlobalFrequencyRequest;
import emre.colak.helixinsights.lambda.request.intent.Intent;
import emre.colak.helixinsights.lambda.request.intent.ListInsightsRequest;
import emre.colak.helixinsights.lambda.request.intent.ReadyRequest;
import emre.colak.helixinsights.lambda.request.intent.RecommendProductsRequest;
import emre.colak.helixinsights.lambda.request.intent.Slot;
import emre.colak.helixinsights.lambda.request.intent.UnknownRequest;

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
  
  // Different intents
  private static final String READY_REQUEST = "CheckReady";
  private static final String LIST_INSIGHTS_REQUEST = "ListInsights";
  private static final String GET_INSIGHT_REQUEST = "GetInsight";
  private static final String RECOMMEND_PRODUCTS_REQUEST = "RecommendProducts";
  private static final String GLOBAL_FREQUENCY_REQUEST = "CompareToPopulation";
  private static final String ABOUT_INSIGHT_REQUEST = "AboutInsight";
  private static final String GENETIC_IMPACT_REQUEST = "GeneticImpact";
  private static final String GENES_AND_ENV_REQUEST = "GenesAndEnvironment";
  
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
      IntentRequest ir = getIntentRequest(getIntent(request), br);
      result = ir != null ? ir : new UnknownRequest(br);
    } else if (SESSION_ENDED_REQUEST.equals(type)) {
      result = new SessionEndedRequest(br);
    } else {
      throw new IllegalArgumentException("Invalid request type");
    }
    return result;
  }
  
  private static IntentRequest getIntentRequest(Intent intent, BaseRequest br) {
    if (intent == null) {
      return null;
    }
    List<Slot> slots = intent.getSlots();
    switch (intent.getName()) {
      case READY_REQUEST:
        return new ReadyRequest(br);
      case LIST_INSIGHTS_REQUEST:
        return new ListInsightsRequest(br);
      case GET_INSIGHT_REQUEST:
        return new GetInsightRequest(br, getInsightSlot(slots));
      case RECOMMEND_PRODUCTS_REQUEST:
        return new RecommendProductsRequest(br, getInsightSlot(slots));
      case GLOBAL_FREQUENCY_REQUEST:
        return new GlobalFrequencyRequest(br, getInsightSlot(slots));
      case ABOUT_INSIGHT_REQUEST:
        return new AboutInsightRequest(br, getInsightSlot(slots));
      case GENETIC_IMPACT_REQUEST:
        return new GeneticImpactRequest(br, getInsightSlot(slots));
      case GENES_AND_ENV_REQUEST:
        return new GenesAndEnvironmentRequest(br, getInsightSlot(slots));
        
    }
    return null;
  }
  
  private static String getInsightSlot(List<Slot> slots) {
    return Slot.byName(slots, "insight").getValue();
  }
  
  @SuppressWarnings("unchecked")
  private static Intent getIntent(Map<String, Object> request) {
    Map<String, Object> objIntent = getTypedValueFromMap(request, "intent", Map.class);
    if (objIntent == null) {
      return null;
    }
    String intentName = getTypedValueFromMap(objIntent, "name", String.class);
    String intentConfStatus = getTypedValueFromMap(objIntent, "confirmationStatus", String.class);
    Map<String, Object> objSlots = getTypedValueFromMap(objIntent, "slots", Map.class);
    List<Slot> slots = new ArrayList<>();
    if (objSlots != null) {
      for (Object objSlot : objSlots.values()) {
        if (Map.class.isInstance(objSlot)) {
          Map<String, Object> os = (Map<String, Object>) objSlot;
          String slotName = getTypedValueFromMap(os, "name", String.class);
          String slotValue = getTypedValueFromMap(os, "value", String.class);
          String slotConfStatus = getTypedValueFromMap(os, "confirmationStatus", String.class);
          slots.add(new Slot(slotName, slotValue, ConfirmationStatus.valueOf(slotConfStatus)));
        }  
      } 
    }
    Intent intent = new Intent();
    intent.setName(intentName);
    intent.setConfirmationStatus(ConfirmationStatus.valueOf(intentConfStatus));
    intent.setSlots(slots);
    return intent;
  }
  
  private static <T> T getTypedValueFromMap(Map<String, Object> map, String key, Class<T> clazz) {
    Object o = map.get(key);
    if (o == null) {
      return null;
    }
    if (!clazz.isInstance(o)) {
      return null;
    }
    return clazz.cast(o);
  }
}
