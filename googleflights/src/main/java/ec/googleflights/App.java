package ec.googleflights;

import java.util.Arrays;

import ec.googleflights.model.BookingClass;
import ec.googleflights.model.Leg;
import ec.googleflights.model.NumStops;
import ec.googleflights.model.Query;
import ec.googleflights.model.TimeRange;
import ec.googleflights.model.result.ErrorResponse;
import ec.googleflights.model.result.QueryResponse;
import ec.googleflights.model.result.SuccessfulResponse;
import ec.googleflights.service.DefaultQueryService;
import ec.googleflights.service.QueryService;

public class App {
  public static void main(String[] args) throws Exception {
    System.out.println("Hello Google Flights");
    
    Query q = new Query();
    Leg leg1 = new Leg();
    leg1.setFrom(Arrays.asList("SFO", "OAK", "SJC"));
    leg1.setTo(Arrays.asList("IST"));
    leg1.setDate(Leg.DATE_FORMAT.parse("2019-12-21"));
    //leg1.setNumStops(NumStops.ONE_OR_FEWER);
    //leg1.setDepartureFilter(new TimeRange(14,23));
    //leg1.setMaxDurationInMinutes(20*60);
    
    Leg leg2 = new Leg();
    leg2.setFrom(Arrays.asList("IST"));
    leg2.setTo(Arrays.asList("SFO", "OAK", "SJC"));
    leg2.setDate(Leg.DATE_FORMAT.parse("2020-01-05"));
    leg2.setNumStops(NumStops.ZERO);
    leg2.setMaxDurationInMinutes(20*60);
    
    q.setLegs(Arrays.asList(leg1));
    //q.setBookingClass(BookingClass.ECONOMY);
    
    QueryService service = new DefaultQueryService();
    System.out.println("Running query " + q);
    QueryResponse response = service.sendQuery(q);
    if (!response.isSuccessful()) {
      System.out.println(((ErrorResponse)response).error);
    } else {
      SuccessfulResponse r = (SuccessfulResponse) response;
      System.out.println("Results:");
      System.out.println(String.format("Cheapest price: %d %s", 
          r.results.sliceResults.cheapestPrice.priceValue,
          r.results.sliceResults.cheapestPrice.currencyCode));
    }
  }
}
