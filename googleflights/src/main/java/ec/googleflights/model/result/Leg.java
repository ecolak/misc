package ec.googleflights.model.result;

/**
 * "amenities":{  
       "all_seats_power_and_usb":true,
       "on_demand_video":true
    },
    "arrival_code":"LGW",
    "arrival_time":{  
       "iso":"12:40",
       "offset_days":1
    },
    "departure_code":"SFO",
    "departure_time":{  
       "iso":"18:30",
       "offset_days":0
    },
    "duration_minutes":610,
    "is_train":false,
    "operating_flight_number":{  
       "airline":"DI",
       "number":"7174"
    },
    "seat_info":"AVERAGE_LEGROOM",
    "seat_info_legroom_size":"31 in",
    "seating_class":"ECONOMY",
    "warning":{  
       "is_overnight":true
    }
 * */

public class Leg {

  public static class Amenities {
    public boolean allSeatsPowerAndUsb;
    public boolean onDemandVideo;
  }
  
  public static class Time {
    public String iso;
    public int offsetDays;
  }
  
  public static class OperatingFlightNumber {
    public String airline;
    public String number;
  }
  
  public static class Warning {
    public boolean isOvernight;
  }
  
  public Amenities amenities;
  public String arrivalCode;
  public String departureCode;
  public Time arrivalTime;
  public Time departureTime;
  public int durationMinutes;
  public boolean isTrain;
  public OperatingFlightNumber operatingFlightNumber;
  public String seatInfo;
  public String seatInfoLegroomSize;
  public String seatingClass;
  public Warning warning;
  
}
