package emre.colak.leftoverpolice.lambda;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class GetNamesHandler implements RequestHandler<Object, List<String>> {

  @Override
  public List<String> handleRequest(Object input, Context context) {
    return Utils.dbService().listNames();
  }

}
