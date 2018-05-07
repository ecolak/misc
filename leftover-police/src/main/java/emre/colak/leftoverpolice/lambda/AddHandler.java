package emre.colak.leftoverpolice.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import emre.colak.leftoverpolice.lambda.AddHandler.AddRequest;
import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService; 

public class AddHandler implements RequestHandler<AddRequest, Boolean> {
  
  public static class AddRequest {
    private String name;
    private String boxColor;
    private String source;
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getBoxColor() {
      return boxColor;
    }
    public void setBoxColor(String boxColor) {
      this.boxColor = boxColor;
    }
    public String getSource() {
      return source;
    }
    public void setSource(String source) {
      this.source = source;
    }
  }
  
  @Override
  public Boolean handleRequest(AddRequest request, Context context) {
    System.out.println(String.format("Adding leftover %s from %s with box color %s", 
        request.getName(), request.getSource(), request.getBoxColor()));
    
    ILeftoverService service = Utils.dbService();
    Leftover lo = new Leftover();
    lo.setName(request.getName());
    lo.setSource(request.getSource());
    lo.setBoxColor(request.getBoxColor());
    service.create(lo);
    
    return true;
  } 
}
