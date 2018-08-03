package emre.colak.helixinsights.lambda.request.intent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Slot {

  private String name;
  private String value;
  private ConfirmationStatus confirmationStatus;
  private List<Object> resolutions = new ArrayList<>(); // TODO: Create a Resolution object
  
  public Slot(String name, String value, ConfirmationStatus confirmationStatus) {
    this.name = name;
    this.value = value;
    this.confirmationStatus = confirmationStatus;
  }
  
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
  
  public static Slot byName(Collection<Slot> slots, String slotName) {
    Objects.requireNonNull(slotName);
    for (Slot slot : slots) {
      if (slotName.equals(slot.getName())) {
        return slot;
      }
    }
    return null;
  }
}
