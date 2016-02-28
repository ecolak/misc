package self.ec.btcbots.model;


public class PointInTime {

  private boolean now;
  private Duration delayFromNow;
  private Long timeInMillis;

  public boolean isNow() {
    return now;
  }

  public void setNow(boolean now) {
    this.now = now;
  }

  public Duration getDelayFromNow() {
    return delayFromNow;
  }

  public void setDelayFromNow(Duration delayFromNow) {
    this.delayFromNow = delayFromNow;
  }

  public Long getTimeInMillis() {
    return timeInMillis;
  }

  public void setTimeInMillis(Long timeInMillis) {
    this.timeInMillis = timeInMillis;
  }
}
