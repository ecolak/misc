package emre.colak.leftoverpolice.lambda.request;

public interface IRequest {
  String getRequestId();
  String getTimestamp();
  String getLocale();
}
