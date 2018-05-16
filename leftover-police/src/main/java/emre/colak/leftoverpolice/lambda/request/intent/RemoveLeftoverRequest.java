package emre.colak.leftoverpolice.lambda.request.intent;

import java.util.List;
import java.util.Objects;

import emre.colak.leftoverpolice.lambda.Utils;
import emre.colak.leftoverpolice.lambda.request.BaseRequest;
import emre.colak.leftoverpolice.lambda.request.IntentRequest;
import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService;

public class RemoveLeftoverRequest extends IntentRequest {

  private final String food;
  private final String source;
  
  public RemoveLeftoverRequest(BaseRequest br, String food, String source) {
    super(br);
    Objects.requireNonNull(food);
    this.food = food;
    this.source = source;
  }
  
  public String getFood() {
    return food;
  }

  public String getSource() {
    return source;
  }

  @Override
  public String respondWithSSML() {
    ILeftoverService service = Utils.dbService();
    List<Leftover> list = service.searchByName(food);
    System.out.println(list.size() + " leftovers found with name " + food);
    
    if (list.isEmpty()) {
      return "You don't have " + food + (source != null ? (" from " + source) : "");
    } else {
      int idToDelete = list.get(0).getId();
      System.out.println("Deleting leftover with id " + idToDelete);
      service.delete(idToDelete);
      return "OK";
    }
  }

  @Override
  public String toString() {
    return "RemoveLeftoverRequest [food=" + food + ", source=" + source + "]";
  }
}
