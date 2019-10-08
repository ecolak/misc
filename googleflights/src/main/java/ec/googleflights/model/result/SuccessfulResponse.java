package ec.googleflights.model.result;

import java.util.List;

public class SuccessfulResponse implements QueryResponse {

  public static class AircraftDictionary {
    public List<Aircraft> aircraft;
  }
  
  public static class AirlineDictionary {
    public List<Airline> airline;
  }
  
  public AircraftDictionary aircraftDictionary;
  public AirlineDictionary airlineDictionary;
  public QueryResults results;
  
  @Override
  public boolean isSuccessful() {
    return true;
  }
}
