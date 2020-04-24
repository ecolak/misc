package ec.logbook.service;

public class TripNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1155337478911121806L;

  public TripNotFoundException() {
    super("Trip not found");
  }
}
