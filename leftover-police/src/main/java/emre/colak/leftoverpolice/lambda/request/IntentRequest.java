package emre.colak.leftoverpolice.lambda.request;

import java.util.Objects;

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
public class IntentRequest implements IRequest {
  
  private BaseRequest br;
  private String dialogState;
  private Intent intent;
  
  public IntentRequest(BaseRequest br) {
    Objects.requireNonNull(br);
    this.br = br;
  }
  
  public IntentRequest(String requestId, String timestamp, String locale) {
    br = new BaseRequest(requestId, timestamp, locale);
  }
  
  public String getDialogState() {
    return dialogState;
  }

  public void setDialogState(String dialogState) {
    this.dialogState = dialogState;
  }

  public Intent getIntent() {
    return intent;
  }

  public void setIntent(Intent intent) {
    this.intent = intent;
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
