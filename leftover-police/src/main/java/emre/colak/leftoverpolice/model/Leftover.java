package emre.colak.leftoverpolice.model;

import java.util.Date;

public class Leftover {

  private int id;
  private String name;
  private String source; // e.g. restaurant name or our cooking
  private String boxColor;
  private Date dateAdded;
  private boolean deleted;
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
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
  public Date getDateAdded() {
    return dateAdded;
  }
  public void setDateAdded(Date dateAdded) {
    this.dateAdded = dateAdded;
  }
  public boolean isDeleted() {
    return deleted;
  }
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
  
}
