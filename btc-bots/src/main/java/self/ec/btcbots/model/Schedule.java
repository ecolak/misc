package self.ec.btcbots.model;

public class Schedule {

  private Duration period;
  private PointInTime startTime;

  public Schedule() {}

  public Schedule(Duration period, PointInTime startTime) {
    this.period = period;
    this.startTime = startTime;
  }

  public Duration getPeriod() {
    return period;
  }

  public void setPeriod(Duration period) {
    this.period = period;
  }

  public PointInTime getStartTime() {
    return startTime;
  }

  public void setStartTime(PointInTime startTime) {
    this.startTime = startTime;
  }

}
