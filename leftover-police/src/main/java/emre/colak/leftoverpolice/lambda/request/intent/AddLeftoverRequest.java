package emre.colak.leftoverpolice.lambda.request.intent;

import java.util.Objects;

import emre.colak.leftoverpolice.lambda.Utils;
import emre.colak.leftoverpolice.lambda.request.BaseRequest;
import emre.colak.leftoverpolice.lambda.request.IntentRequest;
import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService;

public class AddLeftoverRequest extends IntentRequest {

  private final String food;
  private final String source;
  private final String boxColor;

  public AddLeftoverRequest(BaseRequest br, String food, String source, String boxColor) {
    super(br);
    this.food = food;
    this.source = source;
    this.boxColor = boxColor;
  }

  public String getFood() {
    return food;
  }

  public String getSource() {
    return source;
  }

  public String getBoxColor() {
    return boxColor;
  }

  @Override
  public String respondWithSSML() {
    ILeftoverService service = Utils.dbService();
    service.create(new Leftover(food, source, boxColor));
    return "OK";
  }

  @Override
  public String toString() {
    return "AddLeftoverRequest [food=" + food + ", source=" + source + ", boxColor=" + boxColor + "]";
  }

  public static AddLeftoverRequest fromQuery(BaseRequest br, String query) {
    Objects.requireNonNull(query);
    String food = null;
    String source = null;
    String boxColor = null;
    int fromIndex = query.indexOf("from");
    if (fromIndex < 0) {
      food = query;
    } else {
      food = query.substring(0, fromIndex).trim();
      int x = food.length() + 6; // 4 for 'from' + 2 spaces
      if (query.length() >= x) {
        source = query.substring(x);  
      }
    }
    return new AddLeftoverRequest(br, food, source, boxColor);
  }
}
