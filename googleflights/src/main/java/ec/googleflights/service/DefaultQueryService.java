package ec.googleflights.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import ec.googleflights.model.Query;
import ec.googleflights.model.result.ErrorResponse;
import ec.googleflights.model.result.QueryResponse;
import ec.googleflights.model.result.SuccessfulResponse;

public class DefaultQueryService implements QueryService {

  public static final String GOOGLE_FLIGHTS_SEARCH_API_URL = "https://www.google.com/async/flights/search";
    
  private static class ErrorAPIResponse {
    @JsonProperty("_r")
    private ErrorResponse r;
  }
  
  private static class SuccessfulAPIResponse {
    @JsonProperty("_r")
    private SuccessfulResponse r;
  }
  
  @Override
  public QueryResponse SendQuery(Query q) {
    try {
      Map<String,String> asyncParams = new LinkedHashMap<>();
      asyncParams.put("data", q.toQueryString());
      asyncParams.put("s", "s");
      
      StringBuilder asyncValue = new StringBuilder();
      int i = 0;
      for (Iterator<Map.Entry<String,String>> it = asyncParams.entrySet().iterator(); it.hasNext(); i++) {
        if (i > 0) {
          asyncValue.append(",");
        }
        Map.Entry<String, String> e = it.next();
        asyncValue.append(e.getKey()).append(":").append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString()));
      }
      
      String url = String.format("%s?async=%s", GOOGLE_FLIGHTS_SEARCH_API_URL, asyncValue.toString());
      String content = Request.Get(url).execute().returnContent().asString();
      if (content.startsWith(")]}'")) {
        content = content.substring(4);
      }
      
      ObjectMapper om = new ObjectMapper();
      om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
      om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);      
      
      ErrorResponse errResponse = null;
      try {
        ErrorAPIResponse resp = om.readValue(content, ErrorAPIResponse.class); 
        if (resp.r.error != null) {
          errResponse = resp.r;
        }
      } catch (JsonProcessingException e) {
        // ignore, we'll check success next
        System.err.println(e.getMessage());
      }
      
      if (errResponse != null) {
        return errResponse;
      }
      

      SuccessfulAPIResponse resp = om.readValue(content, SuccessfulAPIResponse.class); 
      return resp.r;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
