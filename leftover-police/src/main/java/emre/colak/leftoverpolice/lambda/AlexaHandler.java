package emre.colak.leftoverpolice.lambda;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import emre.colak.leftoverpolice.lambda.AlexaResponse.OutputSpeech;
import emre.colak.leftoverpolice.lambda.AlexaResponse.Response;
import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService;

public class AlexaHandler implements RequestHandler<Map<String, Object>, AlexaResponse>{

  @Override
  public AlexaResponse handleRequest(Map<String, Object> input, Context context) {
    System.out.println("Input coming from Alexa: " + input);
    
    ILeftoverService s = Utils.dbService();
    List<Leftover> leftovers = s.list();
    String ssml = leftovers.isEmpty() ? "Nothing" : leftoversToSSML(leftovers);
    System.out.println("ssml: " + ssml);
    
    OutputSpeech os = new OutputSpeech();
    os.type = "SSML"; // Change to PlainText if you don't want SSML;
    os.ssml = "<speak>" + ssml + "</speak>";
    
    Response r = new Response();
    r.outputSpeech = os;
    r.shouldEndSession = true;
    
    AlexaResponse ar = new AlexaResponse();
    ar.response = r;
    
    return ar;
  }
  
  private static String leftoverToString(Leftover lo) {
    StringBuilder sb = new StringBuilder();
    sb.append(lo.getName());
    String source = lo.getSource();
    if (source != null) {
      sb.append(" from ").append(source);
    }
    return sb.toString();
  }
  
  private static String leftoversToSSML(List<Leftover> list) {
    if (list.size() == 1) {
      return leftoverToString(list.get(0));
    }
    StringBuilder sb = new StringBuilder();
    int i = 1;
    for (Iterator<Leftover> iter = list.iterator(); iter.hasNext(); i++) {
      sb.append("<p>").append(i).append("<break strength=\"medium\"/>")
        .append(leftoverToString(iter.next()))
        .append("</p>");
    }
    return sb.toString();
  }
}
