package emre.colak.helixinsights.lambda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * {
      "version": "string",
      "sessionAttributes": {
        "key": "value"
      },
      "response": {
        "outputSpeech": {
          "type": "PlainText",
          "text": "Plain text string to speak",
          "ssml": "<speak>SSML text string to speak</speak>"
        },
        "card": {
          "type": "Standard",
          "title": "Title of the card",
          "content": "Content of a simple card",
          "text": "Text content for a standard card",
          "image": {
            "smallImageUrl": "https://url-to-small-card-image...",
            "largeImageUrl": "https://url-to-large-card-image..."
          }
        },
        "reprompt": {
          "outputSpeech": {
            "type": "PlainText",
            "text": "Plain text string to speak",
            "ssml": "<speak>SSML text string to speak</speak>"
          }
        },
        "directives": [
          {
            "type": "InterfaceName.Directive"
            (...properties depend on the directive type)
          }
        ],
        "shouldEndSession": true
      }
    }
 *
 */
public class AlexaResponse {

  public String version = "1.0";
  public Map<String, String> sessionAttributes = new HashMap<>();
  public Response response;
  
  public static class Response {
    public OutputSpeech outputSpeech;
    public Card card;
    public Reprompt reprompt;
    public List<Object> directives;
    public boolean shouldEndSession;
  }
  
  public static class OutputSpeech {
    public String type;
    public String text;
    public String ssml;
  }
  
  public static class Reprompt {
    public OutputSpeech outputSpeech;
  }
  
  public static class Image {
    public String smallImageUrl;
    public String largeImageUrl;
  }
  
  public static class Card {
    public String type;
    public String title;
    public String content;
    public String text;
    public Image image;
  }
  
}
