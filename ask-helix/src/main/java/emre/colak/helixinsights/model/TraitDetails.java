package emre.colak.helixinsights.model;

import java.util.ArrayList;
import java.util.List;

public class TraitDetails {

  public static class UserResult {
    public String title;
    public String body;
  }
  
  public static class AboutScience {
    public String title;
    public String body;
  }
  
  public static class GeneticImpact {
    public String title;
    public String body;
    public String legend;
  }
  
  public static class GenesEnvironment {
    public String title;
    public String body;
    public Integer genes;
  }
  
  public static class GlobalFrequency {
    public static class Bin {
      public String label;
      public boolean userResult;
      public Integer frequency;
    }
    
    public String title;
    public String body;
    public List<Bin> bins = new ArrayList<>(); 
  }
  
  public UserResult userResult;
  public AboutScience aboutScience;
  public GeneticImpact geneticImpact;
  public GenesEnvironment genesEnvironment;
  public GlobalFrequency globalFrequency;
  
}
