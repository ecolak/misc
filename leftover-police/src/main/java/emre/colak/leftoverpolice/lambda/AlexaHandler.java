package emre.colak.leftoverpolice.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import emre.colak.leftoverpolice.lambda.AlexaResponse.OutputSpeech;
import emre.colak.leftoverpolice.lambda.AlexaResponse.Response;

public class AlexaHandler implements RequestHandler<Map<String, Object>, AlexaResponse>{

  @Override
  public AlexaResponse handleRequest(Map<String, Object> input, Context context) {
    System.out.println("Input coming from Alexa: " + input);
    
    OutputSpeech os = new OutputSpeech();
    os.type = "PlainText";
    os.text = "Nothing yet";
    
    Response r = new Response();
    r.outputSpeech = os;
    r.shouldEndSession = true;
    
    AlexaResponse ar = new AlexaResponse();
    ar.response = r;
    
    return ar;
  }
}
