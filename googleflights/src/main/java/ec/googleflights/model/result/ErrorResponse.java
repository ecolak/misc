package ec.googleflights.model.result;

import java.util.List;

/**
 * "error": {
      "error_codes": [
          "TEMPORARY_SERVER_ERROR"
      ],
      "error_type": "SERVER_ERROR"
   }
 * */
public class ErrorResponse implements QueryResponse {

  public static class Error {
    public List<String> errorCodes;
    public String errorType;
    
    @Override
    public String toString() {
      return "[errorCodes=" + errorCodes + ", errorType=" + errorType + "]";
    }
    
  }
  
  public Error error;
  
  @Override
  public boolean isSuccessful() {
    return false;
  }

  @Override
  public String toString() {
    return "[error=" + error + "]";
  }
  
}
