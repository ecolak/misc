package ec.googleflights.model;

public class TimeRange {

  private final int start;
  private final int end;
  
  public TimeRange(int start, int end) {
    if (start < 0 || start > 23) {
      throw new IllegalArgumentException("start cannot be less than 0 or greater than 23");
    }
    if (end < 0 || end > 23) {
      throw new IllegalArgumentException("end cannot be less than 0 or greater than 23");
    }
    if (start >= end) {
      throw new IllegalArgumentException("start must be less than end");
    }
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return "[start=" + start + ", end=" + end + "]";
  }
  
}
