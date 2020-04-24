package ec.logbook.service;

import java.util.List;

import ec.logbookapi.model.Trip;

public interface ITripRepo {
  
  List<Trip> list();
  
  List<Trip> getByUserId(String userId);
  
  Trip getByUserIdAndTripId(String userId, String tripId) throws TripNotFoundException;  
    
  void create(Trip trip);
  
  void deleteByUserId(String userId);
  
  void deleteByUserIdAndTripId(String userId, String tripId);
}
