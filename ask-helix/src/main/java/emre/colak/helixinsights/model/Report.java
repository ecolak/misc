package emre.colak.helixinsights.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Report {

  public static class Summary {
    public List<Trait> traits = new ArrayList<>();
  }
  
  public Summary summary;
  public Map<String, TraitReport> reports = new HashMap<>();
  
  public List<String> traitNames() {
    List<String> result = new ArrayList<>();
    if (summary != null) {
      result = summary.traits.stream().map(t -> t.title).collect(Collectors.toList());
    }
        
    return result;
  }
  
  public TraitReport traitReport(String traitName) {
    if (summary != null) {
      Trait trait = null;
      for (Trait t : summary.traits) {
        if (t.title.equalsIgnoreCase(traitName)) {
          trait = t;
          break;
        }
      }
      
      if (trait != null) {
        return reports.get(trait.id);
      }
    }
    
    return null;
  }
}
