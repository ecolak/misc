package emre.colak.leftoverpolice.lambda.request.intent;

import java.util.ArrayList;
import java.util.List;

public class Intent {
  private String name;
  private ConfirmationStatus confirmationStatus;
  private List<Slot> slots = new ArrayList<>();
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public ConfirmationStatus getConfirmationStatus() {
    return confirmationStatus;
  }
  public void setConfirmationStatus(ConfirmationStatus confirmationStatus) {
    this.confirmationStatus = confirmationStatus;
  }
  public List<Slot> getSlots() {
    return slots;
  }
  public void setSlots(List<Slot> slots) {
    this.slots = slots;
  } 
}
