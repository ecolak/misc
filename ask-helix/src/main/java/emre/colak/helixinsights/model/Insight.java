package emre.colak.helixinsights.model;

public enum Insight {
  
  CilantroTaste("Cilantro taste"), 
  RestingHeartRate("Resting heart rate"),
  RiskTakingBehavior("Risk taking behavior"),
  CircadianRythm("Circadian rythm"),
  MuscleComposition("Muscle composition");
  
  String label;
  
  Insight(String label) {
    this.label = label;
  }
  
  public String getLabel() {
    return label;
  }
}
