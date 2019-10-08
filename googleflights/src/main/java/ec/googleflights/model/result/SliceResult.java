package ec.googleflights.model.result;

import java.util.List;

public class SliceResult {

  public CheapestPrice cheapestPrice;
  public int dominatedCount;
  public int dominatedFlightsCount;
  public int dominatedTrainsCount;
  public List<ResultRecord> resultItem;
  public List<ResultRecord> suggestedItem;
  public Summary summary;
}
