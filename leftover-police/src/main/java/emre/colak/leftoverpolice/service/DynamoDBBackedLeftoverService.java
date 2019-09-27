package emre.colak.leftoverpolice.service;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import emre.colak.leftoverpolice.model.Leftover;

public class DynamoDBBackedLeftoverService implements ILeftoverService {

  private static final String TABLE_NAME = "leftovers";
  private static final String ATTR_ID = "id";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_SOURCE = "source";
  private static final String ATTR_CREATED_AT = "createdAt";
    
  private DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
  
  @Override
  public void create(Leftover leftover) {
    Objects.requireNonNull(leftover);
    Table table = dynamoDB.getTable(TABLE_NAME);
    System.out.println("Writing leftover to DynamoDB: " + leftover);
    PutItemOutcome out = table.putItem(leftoverToItem(leftover));
    System.out.println("Write successful. Put item result: " + out.getPutItemResult());
  }

  @Override
  public void delete(int id) {
    dynamoDB.getTable(TABLE_NAME).deleteItem(ATTR_ID, String.valueOf(id));
  }

  @Override
  public int deleteAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Leftover> list() {
    List<Leftover> result = new LinkedList<>();
    ItemCollection<ScanOutcome> items = dynamoDB.getTable(TABLE_NAME).scan();
    for (Iterator<Item> iter = items.iterator(); iter.hasNext(); ) {
      Item item = iter.next();
      result.add(itemToLeftover(item));
    }
    return result;
  }

  @Override
  public List<Leftover> listSortedByDateAdded(SortDir sortDir) {
    // TODO: Implement
    return list();
  }

  @Override
  public List<String> listNames() {
    List<String> result = new LinkedList<>();
    ItemCollection<ScanOutcome> items = dynamoDB.getTable(TABLE_NAME).scan();
    for (Iterator<Item> iter = items.iterator(); iter.hasNext(); ) {
      Item item = iter.next();
      result.add(item.getString(ATTR_NAME));
    }
    return result;
  }

  @Override
  public List<Leftover> searchByName(String name) {
    throw new UnsupportedOperationException();
  }

  public static Item leftoverToItem(Leftover lo) {
    Item item = new Item().withPrimaryKey(ATTR_ID, String.valueOf(lo.getId()));
    if (lo.getName() != null) item = item.withString(ATTR_NAME, lo.getName());
    if (lo.getSource() != null) item = item.withString(ATTR_SOURCE, lo.getSource());
    if (lo.getDateAdded() != null) item = item.withNumber(ATTR_CREATED_AT, lo.getDateAdded().getTime());
    return item;
  }
  
  public static Leftover itemToLeftover(Item item) {
    Leftover lo = new Leftover();
    lo.setId(Integer.parseInt(item.getString(ATTR_ID)));
    lo.setName(item.getString(ATTR_NAME));
    lo.setSource(item.getString(ATTR_SOURCE));
    lo.setDateAdded(new Date(item.getNumber(ATTR_CREATED_AT).longValue()));
    return lo;
  }
}
