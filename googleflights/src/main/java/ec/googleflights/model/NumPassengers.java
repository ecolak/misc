package ec.googleflights.model;

public class NumPassengers {

  private Integer adults;
  private Integer children;
  private Integer infantsInSeat;
  private Integer infantsOnLap;
  
  public NumPassengers() {}
  
  public NumPassengers(Integer adults, Integer children, Integer infantsInSeat,
      Integer infantsOnLap) {
    this.adults = adults;
    this.children = children;
    this.infantsInSeat = infantsInSeat;
    this.infantsOnLap = infantsOnLap;
  }
  public Integer getAdults() {
    return adults;
  }
  public void setAdults(Integer adults) {
    this.adults = adults;
  }
  public Integer getChildren() {
    return children;
  }
  public void setChildren(Integer children) {
    this.children = children;
  }
  public Integer getInfantsInSeat() {
    return infantsInSeat;
  }
  public void setInfantsInSeat(Integer infantsInSeat) {
    this.infantsInSeat = infantsInSeat;
  }
  public Integer getInfantsOnLap() {
    return infantsOnLap;
  }
  public void setInfantsOnLap(Integer infantsOnLap) {
    this.infantsOnLap = infantsOnLap;
  }

  @Override
  public String toString() {
    return "[adults=" + adults + ", children=" + children + ", infantsInSeat="
        + infantsInSeat + ", infantsOnLap=" + infantsOnLap + "]";
  }
  
}
