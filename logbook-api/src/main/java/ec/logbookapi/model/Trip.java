package ec.logbookapi.model;

public class Trip {

  private String id;
  private String origin;
  private String destination;
  private String startDate;
  private String startTime;
  private String endDate;
  private String endTime;
  private Integer distance;
  private String duties;
  private String vesselName;
  private String conditions;
  private String otherDetails;
  private long createdAt;
  
  private String userId; // foreign key

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Integer getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public String getDuties() {
    return duties;
  }

  public void setDuties(String duties) {
    this.duties = duties;
  }

  public String getVesselName() {
    return vesselName;
  }

  public void setVesselName(String vesselName) {
    this.vesselName = vesselName;
  }

  public String getConditions() {
    return conditions;
  }

  public void setConditions(String conditions) {
    this.conditions = conditions;
  }

  public String getOtherDetails() {
    return otherDetails;
  }

  public void setOtherDetails(String otherDetails) {
    this.otherDetails = otherDetails;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }
  
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
