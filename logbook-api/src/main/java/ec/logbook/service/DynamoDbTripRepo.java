package ec.logbook.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import ec.logbookapi.model.Trip;

public class DynamoDbTripRepo implements ITripRepo {

  private static final String TABLE_NAME = "logbook-trips";
  
  private static final String ATTR_USER_ID = "userId"; // partition key
  private static final String ATTR_TRIP_ID = "tripId"; // sort key 
  private static final String ATTR_ORIGIN = "origin";
  private static final String ATTR_DESTINATION = "destination";
  private static final String ATTR_START_DATE = "startDate";
  private static final String ATTR_START_TIME = "startTime";
  private static final String ATTR_END_DATE = "endDate";
  private static final String ATTR_END_TIME = "endTime";
  private static final String ATTR_DISTANCE = "distance";
  private static final String ATTR_DUTIES = "duties";
  private static final String ATTR_VESSEL_NAME = "vesselName";
  private static final String ATTR_CONDITIONS = "conditions";
  private static final String ATTR_OTHER_DETAILS = "otherDetails";
  private static final String ATTR_CREATED_AT = "createdAt";
  
  private DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
  
  @Override
  public void create(Trip trip) {
    Objects.requireNonNull(trip);
    Table table = dynamoDB.getTable(TABLE_NAME);
    System.out.println("Writing trip to DynamoDB: " + trip);
    PutItemOutcome out = table.putItem(tripToItem(trip));
    System.out.println("Write successful. Put item result: " + out.getPutItemResult()); 
  }
  
  @Override
  public List<Trip> getByUserId(String userId) {
    List<Trip> result = new LinkedList<>();
    ItemCollection<QueryOutcome> items = dynamoDB.getTable(TABLE_NAME).query(ATTR_USER_ID, userId);
    for (Iterator<Item> iter = items.iterator(); iter.hasNext(); ) {
      Item item = iter.next();
      result.add(itemToTrip(item));
    }
    return result;
  }
  
  @Override
  public Trip getByUserIdAndTripId(String userId, String tripId) throws TripNotFoundException {
    Item item = dynamoDB.getTable(TABLE_NAME).getItem(ATTR_USER_ID, userId, ATTR_TRIP_ID, tripId);
    if (item != null) {
      return itemToTrip(item);
    }
    throw new TripNotFoundException();
  }

  @Override
  public List<Trip> list() {
    List<Trip> result = new LinkedList<>();
    ItemCollection<ScanOutcome> items = dynamoDB.getTable(TABLE_NAME).scan();
    for (Iterator<Item> iter = items.iterator(); iter.hasNext(); ) {
      Item item = iter.next();
      result.add(itemToTrip(item));
    }
    return result;
  }

  @Override
  public void deleteByUserId(String userId) {
    Objects.requireNonNull(userId);
    dynamoDB.getTable(TABLE_NAME).deleteItem(ATTR_USER_ID, userId);
    
  }
  
  @Override
  public void deleteByUserIdAndTripId(String userId, String tripId) {
    Objects.requireNonNull(userId);
    Objects.requireNonNull(tripId);
    dynamoDB.getTable(TABLE_NAME).deleteItem(ATTR_USER_ID, userId, ATTR_TRIP_ID, tripId);
  }

  public static Item tripToItem(Trip t) {
    Item item = new Item().withPrimaryKey(ATTR_USER_ID, t.getUserId(), ATTR_TRIP_ID, t.getId());
    setStringField(item, ATTR_ORIGIN, t.getOrigin());
    setStringField(item, ATTR_DESTINATION, t.getDestination());
    setStringField(item, ATTR_START_DATE, t.getStartDate());
    setStringField(item, ATTR_END_DATE, t.getEndDate());
    setStringField(item, ATTR_START_TIME, t.getStartTime());
    setStringField(item, ATTR_END_TIME, t.getEndTime());
    setStringField(item, ATTR_DUTIES, t.getDuties());
    setStringField(item, ATTR_VESSEL_NAME, t.getVesselName());
    setStringField(item, ATTR_CONDITIONS, t.getConditions());
    setStringField(item, ATTR_OTHER_DETAILS, t.getOtherDetails());
    item.withInt(ATTR_DISTANCE, t.getDistance());
    item.withNumber(ATTR_CREATED_AT, t.getCreatedAt());
 
    return item;
  }
  
  private static void setStringField(Item item, String fieldName, String fieldValue) {
    if (fieldValue != null && fieldValue.trim().length() > 0) {
      item.withString(fieldName, fieldValue);
    }
  }
  
  public static Trip itemToTrip(Item item) {
    Trip t = new Trip();
    t.setId(item.getString(ATTR_TRIP_ID));
    //t.setUserId(item.getString(ATTR_USER_ID));
    t.setOrigin(item.getString(ATTR_ORIGIN));
    t.setDestination(item.getString(ATTR_DESTINATION));
    t.setStartDate(item.getString(ATTR_START_DATE));
    t.setEndDate(item.getString(ATTR_END_DATE));
    t.setStartTime(item.getString(ATTR_START_TIME));
    t.setEndTime(item.getString(ATTR_END_TIME));
    t.setDistance(item.getInt(ATTR_DISTANCE));
    t.setDuties(item.getString(ATTR_DUTIES));
    t.setVesselName(item.getString(ATTR_VESSEL_NAME));
    t.setConditions(item.getString(ATTR_CONDITIONS));
    t.setOtherDetails(item.getString(ATTR_OTHER_DETAILS));
    t.setCreatedAt(item.getNumber(ATTR_CREATED_AT).longValue());
    return t;
  }
}
