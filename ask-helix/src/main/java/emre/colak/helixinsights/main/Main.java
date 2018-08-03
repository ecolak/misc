package emre.colak.helixinsights.main;

import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.service.DefaultInsightsService;
import emre.colak.helixinsights.service.InsightsService;

public class Main {

  public static void main(String[] args) {
    InsightsService is = new DefaultInsightsService();
    Report report = is.getReport();
    System.out.println(report.traitNames());
    
  } 

}
