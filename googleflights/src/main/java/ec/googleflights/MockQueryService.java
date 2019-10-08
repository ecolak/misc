package ec.googleflights;

import java.io.InputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import ec.googleflights.model.Query;
import ec.googleflights.model.result.QueryResponse;
import ec.googleflights.model.result.SuccessfulResponse;
import ec.googleflights.service.QueryService;

public class MockQueryService implements QueryService {

  @Override
  public QueryResponse SendQuery(Query q) {
    System.out.println("Received query " + q);
    
    try (InputStream is = MockQueryService.class.getResourceAsStream("mock_query_result.json")) {
      ObjectMapper om = new ObjectMapper();
      om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
      om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return om.readValue(is, SuccessfulResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }    
  }

}
