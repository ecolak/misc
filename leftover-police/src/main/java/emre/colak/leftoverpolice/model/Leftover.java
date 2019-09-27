package emre.colak.leftoverpolice.model;

import java.util.Date;

public class Leftover {

  private int id;
  private String name;
  private String source; // e.g. restaurant name or our cooking
  private String boxColor;
  private Date dateAdded;
  private boolean deleted;
  
  public Leftover() {}
    
  public Leftover(String name, String source, String boxColor) {
    this((int)System.currentTimeMillis(), name, source, boxColor, new Date());
  }

  public Leftover(int id, String name, String source, String boxColor, Date dateAdded) {
    this.id = id;
    this.name = name;
    this.source = source;
    this.boxColor = boxColor;
    this.dateAdded = dateAdded;
  }

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
