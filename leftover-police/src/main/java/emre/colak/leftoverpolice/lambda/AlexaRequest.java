package emre.colak.leftoverpolice.lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emre.colak.leftoverpolice.lambda.request.BaseRequest;
import emre.colak.leftoverpolice.lambda.request.IRequest;
import emre.colak.leftoverpolice.lambda.request.IntentRequest;
import emre.colak.leftoverpolice.lambda.request.LaunchRequest;
import emre.colak.leftoverpolice.lambda.request.SessionEndedRequest;
import emre.colak.leftoverpolice.lambda.request.intent.AddLeftoverRequest;
import emre.colak.leftoverpolice.lambda.request.intent.ConfirmationStatus;
import emre.colak.leftoverpolice.lambda.request.intent.Intent;
import emre.colak.leftoverpolice.lambda.request.intent.ListLeftoversRequest;
import emre.colak.leftoverpolice.lambda.request.intent.RemoveAllLeftoversRequest;
import emre.colak.leftoverpolice.lambda.request.intent.RemoveLeftoverRequest;
import emre.colak.leftoverpolice.lambda.request.intent.Slot;
import emre.colak.leftoverpolice.lambda.request.intent.UnknownRequest;

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
  private static final String LIST_LEFTOVER_REQUEST = "ListLeftovers";
  private static final String ADD_LEFTOVER_REQUEST = "AddLeftover";
  private static final String REMOVE_LEFTOVER_REQUEST = "RemoveLeftover";
  private static final String REMOVE_ALL_LEFTOVERS_REQUEST = "RemoveAllLeftovers";
  
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
      case LIST_LEFTOVER_REQUEST:
        return new ListLeftoversRequest(br);
      case ADD_LEFTOVER_REQUEST:
        return AddLeftoverRequest.fromQuery(br, Slot.byName(slots, "food").getValue());
      case REMOVE_LEFTOVER_REQUEST:
        Slot foodSlot = Slot.byName(slots, "food"); // required
        Slot sourceSlot = Slot.byName(slots, "source"); // optional
        if (foodSlot != null) {
          return new RemoveLeftoverRequest(br, 
              foodSlot.getValue(),
              (sourceSlot != null ? sourceSlot.getValue() : null));
        }
      case REMOVE_ALL_LEFTOVERS_REQUEST:
        return new RemoveAllLeftoversRequest(br);
    }
    return null;
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
