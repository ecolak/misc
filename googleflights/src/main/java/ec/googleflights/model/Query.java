package ec.googleflights.model;

import java.util.Arrays;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Query {
   
  private List<Leg> legs = new LinkedList<>();
  private NumPassengers numPassengers;
  private Integer numBags;
  private BookingClass bookingClass;
  private Integer maxPrice;
  private boolean hideSeparateTickets;
  private Currency currency = Currency.getInstance("USD");
  
  public List<Leg> getLegs() {
    return legs;
  }

  public void setLegs(List<Leg> legs) {
    this.legs = legs;
  }

  public Integer getNumBags() {
    return numBags;
  }

  public void setNumBags(Integer numBags) {
    this.numBags = numBags;
  }

  public NumPassengers getNumPassengers() {
    return numPassengers;
  }

  public void setNumPassengers(NumPassengers numPassengers) {
    this.numPassengers = numPassengers;
  }

  public BookingClass getBookingClass() {
    return bookingClass;
  }

  public void setBookingClass(BookingClass bookingClass) {
    this.bookingClass = bookingClass;
  }

  public Integer getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(Integer maxPrice) {
    this.maxPrice = maxPrice;
  }

  public boolean getHideSeparateTickets() {
    return hideSeparateTickets;
  }

  public void setHideSeparateTickets(boolean hideSeparateTickets) {
    this.hideSeparateTickets = hideSeparateTickets;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    Objects.requireNonNull(currency);
    this.currency = currency;
  }
  
  /**
   * [
   *  [
   *   [
   *    [
   *     [
   *      [null,[["JFK",0],["EWR",0],["LGA",0]]],
   *      [null,[["MUC",0],["ZRH",0]]],["2019-10-16"],null
   *     ]
   *    ],
   *    2,[2,1,0,0],2,null,null,null,1,[400000,2,"USD"],2,null,null,null,null,null,true
   *   ],
   *   [
   *    [
   *      [1,["LH"],[[14,23]],[[0,11]],[],1200,["FRA","LHR"],[]]
   *    ]
   *   ],
   *   null,"USD",null,[],null,[null,2]
   *  ],
   *  [true]
   * ]
   * 
   */

  public String toQueryString() {
    StringBuilder result = new StringBuilder("[[[");
    
    // flight legs: from, to and date
    result.append("[");
    result.append(legs.stream().map(Leg::toDestinationAndDateString).collect(Collectors.joining(",")));
    result.append("]");
    result.append(",");
    
    // one way, round trip or multi-city
    int d = 0;
    if (legs.size() == 2) d = 1;
    else if (legs.size() == 1) d = 2;
    else if (legs.size() > 2) d = 3;
    result.append(d).append(",");
    
    // num passengers
    if (numPassengers == null) result.append("null");
    else {
      result.append("[");
      result.append(numPassengers.getAdults()).append(",")
            .append(numPassengers.getChildren()).append(",")
            .append(numPassengers.getInfantsInSeat()).append(",")
            .append(numPassengers.getInfantsOnLap());
      result.append("]");
    }
    result.append(",");
    
    // booking class
    result.append(bookingClass != null ? bookingClass.getValue() : "null").append(",");
    
    // unknown stuff
    result.append("null,null,null,1,");
    
    // max price
    if (maxPrice == null) result.append("null");
    else {
      result.append("[").append(maxPrice*100).append(",2,") // not sure what this `2` is
            .append("\"").append(currency.getCurrencyCode()).append("\"").append("]");
    }
    result.append(",");
    
    // more unknown stuff
    result.append("2,null,null,null,null,null,0,");
    
    // show/hide separate tickets. true means show separate tickets
    result.append(!hideSeparateTickets);
    
    result.append("]");
    result.append(",");
    
    // flight legs: other filters like departure time range, connecting airports etc.
    result.append("[[");
    result.append(legs.stream().map(Leg::toFiltersString).collect(Collectors.joining(",")));
    result.append("]]");
    result.append(",");
    
    // more unknown stuff
    result.append("null,\"").append(currency.getCurrencyCode()).append("\"").append(",null,[],null,");
    
    // num bags
    if (numBags == null) result.append("null");
    else result.append("[null,").append(numBags).append("]");
    
    result.append("]");
    result.append(",");
    
    // not sure what this is either
    result.append("[0]");
    
    result.append("]");
    return result.toString();
  }
  
  @Override
  public String toString() {
    return "[legs=" + legs + ", numPassengers=" + numPassengers + ", numBags=" + numBags
        + ", bookingClass=" + bookingClass + ", maxPrice=" + maxPrice + ", hideSeparateTickets="
        + hideSeparateTickets + ", currency=" + currency + "]";
  }

  public static void main(String[] args) throws Exception {
    Query q = new Query();
    
    /*Leg leg1 = new Leg();
    leg1.setFrom(Arrays.asList("JFK", "EWR"));
    leg1.setTo(Arrays.asList("MUC", "FRA", "ZRH"));
    leg1.setDate(Leg.DATE_FORMAT.parse("2019-10-03"));
    leg1.setNumStops(NumStops.ONE_OR_FEWER);
    leg1.setAirlines(Arrays.asList("LH", "UA"));
    leg1.setDepartureFilter(new TimeRange(17,22));
    leg1.setMaxDurationInMinutes(10*60);
    
    q.setLegs(Arrays.asList(leg1));
    q.setBookingClass(BookingClass.BUSINESS);
    q.setNumPassengers(new NumPassengers(2,1,0,0));
    q.setMaxPrice(1250);
    q.setNumBags(1);*/
    
    Leg leg1 = new Leg();
    leg1.setFrom(Arrays.asList("SFO"));
    leg1.setTo(Arrays.asList("MUC"));
    leg1.setDate(Leg.DATE_FORMAT.parse("2019-10-07"));
    leg1.setNumStops(NumStops.ONE_OR_FEWER);
    leg1.setDepartureFilter(new TimeRange(12,22));
    //leg1.setArrivalFilter(new TimeRange(0,12));
    //leg1.setMaxDurationInMinutes(20*60);
    
    Leg leg2 = new Leg();
    leg2.setFrom(Arrays.asList("MUC"));
    leg2.setTo(Arrays.asList("SFO"));
    leg2.setDate(Leg.DATE_FORMAT.parse("2019-10-12"));
    leg2.setNumStops(NumStops.ONE_OR_FEWER);
    //leg2.setDepartureFilter(new TimeRange(5,10));
    //leg2.setArrivalFilter(new TimeRange(7,15));
    //leg2.setMaxDurationInMinutes(20*60);
    
    q.setLegs(Arrays.asList(leg1, leg2));
    //q.setNumPassengers(new NumPassengers(1, null, null, null));
    q.setBookingClass(BookingClass.ECONOMY);
    
    System.out.println(q.toQueryString());
  }
}
