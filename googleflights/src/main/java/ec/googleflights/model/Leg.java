package ec.googleflights.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Leg {

  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  
  private List<String> from = new LinkedList<>();
  private List<String> to = new LinkedList<>();
  private Date date;
  private String selectedFlightCode;
  
  private NumStops numStops;
  private TimeRange departureFilter;
  private TimeRange arrivalFilter;
  private Integer maxDurationInMinutes;
  private Integer maxLayoverInMinutes;
  private List<String> airlines = new LinkedList<>();
  private List<String> exclusionAirlines = new LinkedList<>();
  private List<String> connectingAirports = new LinkedList<>();
  
  public List<String> getFrom() {
    return from;
  }
  public void setFrom(List<String> from) {
    Objects.requireNonNull(from);
    this.from = from;
  }
  public List<String> getTo() {
    return to;
  }
  public void setTo(List<String> to) {
    Objects.requireNonNull(to);
    this.to = to;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public String getSelectedFlightCode() {
    return selectedFlightCode;
  }
  public void setSelectedFlightCode(String selectedFlightCode) {
    this.selectedFlightCode = selectedFlightCode;
  }
  public NumStops getNumStops() {
    return numStops;
  }
  public void setNumStops(NumStops numStops) {
    this.numStops = numStops;
  }
  public TimeRange getDepartureFilter() {
    return departureFilter;
  }
  public void setDepartureFilter(TimeRange departureFilter) {
    this.departureFilter = departureFilter;
  }
  public TimeRange getArrivalFilter() {
    return arrivalFilter;
  }
  public void setArrivalFilter(TimeRange arrivalFilter) {
    this.arrivalFilter = arrivalFilter;
  }
  public Integer getMaxDurationInMinutes() {
    return maxDurationInMinutes;
  }
  public void setMaxDurationInMinutes(Integer maxDurationInMinutes) {
    this.maxDurationInMinutes = maxDurationInMinutes;
  }
  public Integer getMaxLayoverInMinutes() {
    return maxLayoverInMinutes;
  }
  public void setMaxLayoverInMinutes(Integer maxLayoverInMinutes) {
    this.maxLayoverInMinutes = maxLayoverInMinutes;
  }
  public List<String> getAirlines() {
    return airlines;
  }
  public void setAirlines(List<String> airlines) {
    this.airlines = airlines;
  }
  public List<String> getExclusionAirlines() {
    return exclusionAirlines;
  }
  public void setExclusionAirlines(List<String> exclusionAirlines) {
    this.exclusionAirlines = exclusionAirlines;
  }
  public List<String> getConnectingAirports() {
    return connectingAirports;
  }
  public void setConnectingAirports(List<String> connectingAirports) {
    this.connectingAirports = connectingAirports;
  }
  
  /**
   * Examples:
   * [[null,[["JFK",0]]],[null,[["MUC",0]]],["2019-10-16"],null]
   * [[null,[["JFK",0],["EWR",0],["LGA",0]]],[null,[["MUC",0],["ZRH",0]]],["2019-10-16"],"JFKMUC0SQ25"]
   * 
   * */ 
  public String toDestinationAndDateString() {
    StringBuilder result = new StringBuilder("[");
    
    result.append(airportsString(from)).append(",")
          .append(airportsString(to)).append(",")
          .append("[\"").append(DATE_FORMAT.format(date)).append("\"]").append(",");
    if (selectedFlightCode != null) {
      result.append("\"").append(selectedFlightCode).append("\"");
    } else {
      result.append("null");
    }
    
    result.append("]");
    return result.toString();
  }
  
  private static StringBuilder airportsString(List<String> airports) {
    StringBuilder sb = new StringBuilder("[null,[");
    sb.append(airports.stream().map(a -> String.format("[\"%s\",0]", a)).collect(Collectors.joining(",")));
    sb.append("]]");
    return sb;
  }
  
  /**
   * Examples:
   * [0,["STAR_ALLIANCE"],[[8,23]],[[0,16]],[],1380,["LHR","LAX"],[],null,300]
   * [1,["LH", "UA"],[[8,23]],[[0,16]],[],1380,["LHR","LAX"],[],null,300]
   * [1,[],[[8,23]],[[0,16]],["LH", "UA"],1380,["LHR","LAX"],[],null,300]
   * */
  public String toFiltersString() {
    StringBuilder result = new StringBuilder("[");
    
    // num stops
    if (numStops != null) result.append(numStops.getValue()); else result.append("null");
    result.append(",");
    
    // airlines
    result.append("[")
          .append(airlines.stream().map(a -> String.format("\"%s\"", a)).collect(Collectors.joining(",")))
          .append("]");
    result.append(",");
    
    // departure and arrival times
    result.append("[");
    if (departureFilter != null) {
      result.append("[").append(departureFilter.getStart()).append(",").append(departureFilter.getEnd()).append("]");
    }
    result.append("],");
    
    result.append("[");
    if (arrivalFilter != null) {
      result.append("[").append(arrivalFilter.getStart()).append(",").append(arrivalFilter.getEnd()).append("]");
    }
    result.append("],");
    
    // excluded airlines
    result.append("[")
          .append(exclusionAirlines.stream().map(a -> String.format("\"%s\"", a)).collect(Collectors.joining(",")))
          .append("]");
    result.append(",");
    
    // max flight duration
    if (maxDurationInMinutes != null) result.append(maxDurationInMinutes); else result.append("null");
    result.append(",");
    
    // connecting airports
    result.append("[")
          .append(connectingAirports.stream().map(a -> String.format("\"%s\"", a)).collect(Collectors.joining(",")))
          .append("]");
    result.append(",");
    
    // TODO: Not sure what these are for
    result.append("[],null,");
    
    // max layover
    result.append(maxLayoverInMinutes != null ? maxLayoverInMinutes : "null");
    
    result.append("]");
    return result.toString();
  }
  
  @Override
  public String toString() {
    return "[from=" + from + ", to=" + to + ", date=" + date + ", selectedFlightCode="
        + selectedFlightCode + ", numStops=" + numStops + ", departureFilter=" + departureFilter
        + ", arrivalFilter=" + arrivalFilter + ", maxDurationInMinutes=" + maxDurationInMinutes
        + ", maxLayoverInMinutes=" + maxLayoverInMinutes + ", airlines=" + airlines
        + ", exclusionAirlines=" + exclusionAirlines + ", connectingAirports=" + connectingAirports
        + "]";
  }
  public static void main(String[] args) throws Exception {
    Leg leg = new Leg();
    leg.setFrom(Arrays.asList("JFK", "EWR"));
    leg.setTo(Arrays.asList("MUC", "FRA", "ZRH"));
    leg.setDate(DATE_FORMAT.parse("2019-10-03"));
    leg.setNumStops(NumStops.ONE_OR_FEWER);
    leg.setAirlines(Arrays.asList("LH", "UA"));
    leg.setDepartureFilter(new TimeRange(17,22));
    leg.setMaxDurationInMinutes(10*60);
    
    System.out.println(leg.toDestinationAndDateString());
    System.out.println(leg.toFiltersString());
  }
}
