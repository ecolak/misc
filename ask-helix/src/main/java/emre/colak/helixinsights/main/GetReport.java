package emre.colak.helixinsights.main;

import emre.colak.helixinsights.model.Report;
import emre.colak.helixinsights.service.SingleInsightsService;

public class GetReport {

  public static void main(String[] args) {
    Report report = SingleInsightsService.INSTANCE.getReport();
    System.out.println(report.traitNames());
  } 

}
