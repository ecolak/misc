package ec.googleflights.model;

public enum BookingClass {
  
  ECONOMY(1), PREMIUM_ECONOMY(2), BUSINESS(3), FIRST_CLASS(4);

  Integer value;
  
  BookingClass(Integer v) {
    value = v;
  }
  
  public Integer getValue() {
    return value;
  }
}
