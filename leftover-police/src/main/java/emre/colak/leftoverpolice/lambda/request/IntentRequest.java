package emre.colak.leftoverpolice.lambda.request;

import java.util.Objects;

import emre.colak.leftoverpolice.lambda.request.intent.Intent;

/**
 * {
    "type": "IntentRequest",
    "requestId": " amzn1.echo-api.request.0000000-0000-0000-0000-00000000000",
    "timestamp": "2015-05-13T12:34:56Z",
    "dialogState": "COMPLETED",
    "locale": "string",
    "intent": {
      "name": "GetZodiacHoroscopeIntent",
      "confirmationStatus": "NONE"
      "slots": {
        "ZodiacSign": {
          "name": "ZodiacSign",
          "value": "virgo",
          "confirmationStatus": "NONE"
        }
      }
    }
 * */
public abstract class IntentRequest implements IRequest {
  
  protected final BaseRequest br;
  protected final String dialogState;
  protected final Intent intent;
  
  public IntentRequest(BaseRequest br) {
    this(br, null, null);
  }
  
  public IntentRequest(BaseRequest br, String dialogState, Intent intent) {
    Objects.requireNonNull(br);
    this.br = br;
    this.dialogState = dialogState;
    this.intent = intent;
  }

  public String getDialogState() {
    return dialogState;
  }

  public Intent getIntent() {
    return intent;
  }

  @Override
  public String getRequestId() {
    return br.getRequestId();
  }

  @Override
  public String getTimestamp() {
    return br.getTimestamp();
  }

  @Override
  public String getLocale() {
    return br.getLocale();
  }
}
