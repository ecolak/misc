package ec.googleflights.model.result;

import java.util.List;

import ec.googleflights.model.Leg;

public class Itinerary {

  public boolean cheapest;
  public String displayedPrice;
  public boolean dominated;
  public int durationMinutes;
  public String fingerprint;
  public boolean isTrain;
  public List<Leg> leg;
  public List<String> operatingAirlineId;
  public int priceDecimalPlaces;
  public boolean priceGuarantee;
  public int priceValue;
  public boolean splitTicket;
  public List<Stopover> stopover;
  public boolean suggested;
}
