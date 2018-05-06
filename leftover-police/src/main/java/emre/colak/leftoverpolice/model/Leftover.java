package emre.colak.leftoverpolice.model;

public class Leftover {

  private String id;
  private String name;
  private String source; // e.g. restaurant name or our cooking
  private String boxColor;
  private long dateAdded;
  private boolean deleted;
  
  public Leftover() {}
  
  public Leftover(String id) {
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getBoxColor() {
    return boxColor;
  }
  public void setBoxColor(String boxColor) {
    this.boxColor = boxColor;
  }
  public long getDateAdded() {
    return dateAdded;
  }
  public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }
  public boolean isDeleted() {
    return deleted;
  }
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
  
}
