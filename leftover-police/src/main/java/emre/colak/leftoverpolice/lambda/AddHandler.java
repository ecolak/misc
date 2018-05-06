package emre.colak.leftoverpolice.lambda;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService;
import emre.colak.leftoverpolice.service.SqlBackedLeftoverService; 

public class AddHandler implements RequestHandler<AddRequest, String> {

  @Override
  public String handleRequest(AddRequest request, Context context) {
    System.out.println(String.format("Adding leftover %s from %s with box color %s", 
        request.getName(), request.getSource(), request.getBoxColor()));
    
    /*ILeftoverService service = new SqlBackedLeftoverService("","","");
    Leftover lo = new Leftover(UUID.randomUUID().toString());
    lo.setName(request.getName());
    lo.setSource(request.getSource());
    lo.setBoxColor(request.getBoxColor());
    service.create(lo);*/
    
    return "Success";
  } 
}
