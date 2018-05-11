package emre.colak.leftoverpolice.lambda.request;

import java.util.ArrayList;
import java.util.List;

public class Intent {

  public static class Slot {
    public static enum ConfirmationStatus {
      NONE, CONFIRMED, DENIED
    }
    
    private String name;
    private String value;
    private ConfirmationStatus confirmationStatus;
    private List<Object> resolutions = new ArrayList<>(); // TODO: Create a Resolution object
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getValue() {
      return value;
    }
    public void setValue(String value) {
      this.value = value;
    }
    public ConfirmationStatus getConfirmationStatus() {
      return confirmationStatus;
    }
    public void setConfirmationStatus(ConfirmationStatus confirmationStatus) {
      this.confirmationStatus = confirmationStatus;
    }
    public List<Object> getResolutions() {
      return resolutions;
    }
    public void setResolutions(List<Object> resolutions) {
      this.resolutions = resolutions;
    }
  }
  
  private String name;
  private String confirmationStatus;
  private List<Slot> slots = new ArrayList<>();
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getConfirmationStatus() {
    return confirmationStatus;
  }
  public void setConfirmationStatus(String confirmationStatus) {
    this.confirmationStatus = confirmationStatus;
  }
  public List<Slot> getSlots() {
    return slots;
  }
  public void setSlots(List<Slot> slots) {
    this.slots = slots;
  } 
}
