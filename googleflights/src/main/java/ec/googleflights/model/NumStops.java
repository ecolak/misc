package ec.googleflights.model;

public enum NumStops {
  
  ANY(null), ZERO(0), ONE_OR_FEWER(1), TWO_OR_FEWER(2);
  
  Integer value;
  
  NumStops(Integer v) {
    value = v;
  }
  
  public Integer getValue() {
    return value;
  }
}
