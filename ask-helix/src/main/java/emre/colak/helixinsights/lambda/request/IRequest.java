package emre.colak.helixinsights.lambda.request;

public interface IRequest {
  String getRequestId();
  String getTimestamp();
  String getLocale();
  
  default String respondWithSSML() {
    return "I don't know";
  }
  
  default String respondWithPlainText() {
    return respondWithSSML();
  }
}
