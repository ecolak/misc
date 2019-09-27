package emre.colak.leftoverpolice.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import emre.colak.leftoverpolice.lambda.AlexaResponse.OutputSpeech;
import emre.colak.leftoverpolice.lambda.AlexaResponse.Response;
import emre.colak.leftoverpolice.lambda.request.IRequest;

public class AlexaHandler implements RequestHandler<AlexaRequest, AlexaResponse>{

  @Override
  public AlexaResponse handleRequest(AlexaRequest alexaRequest, Context context) {
    System.out.println("Request coming from Alexa: " + alexaRequest);
    
    IRequest request = AlexaRequest.getTypedRequest(alexaRequest.request);
    System.out.println("inferred request: " + request);
    
    OutputSpeech os = new OutputSpeech();
    os.type = "SSML"; // Change to PlainText if you don't want SSML;
    String ssml = null;
    try {
      ssml = request.respondWithSSML(); 
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    System.out.println("produced ssml: " + ssml);
    os.ssml = "<speak>" + ssml + "</speak>";
    
    Response r = new Response();
    r.outputSpeech = os;
    r.shouldEndSession = true;
    
    AlexaResponse ar = new AlexaResponse();
    ar.response = r;
    
    return ar;
  }
  
}
