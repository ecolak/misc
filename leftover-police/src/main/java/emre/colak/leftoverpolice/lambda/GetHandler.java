package emre.colak.leftoverpolice.lambda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import emre.colak.leftoverpolice.lambda.GetHandler.GetRequest;
import emre.colak.leftoverpolice.lambda.GetHandler.LeftoverDigest;
import emre.colak.leftoverpolice.model.Leftover;
import emre.colak.leftoverpolice.service.ILeftoverService;
import emre.colak.leftoverpolice.service.SortDir;

public class GetHandler implements RequestHandler<GetRequest, List<LeftoverDigest>> {

  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
  static {
    dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
  }
  
  public static class GetRequest {
    private boolean sortedByDate;
    private boolean sortDesc;
    
    public boolean isSortedByDate() {
      return sortedByDate;
    }
    public void setSortedByDate(boolean sortedByDate) {
      this.sortedByDate = sortedByDate;
    }
    public boolean isSortDesc() {
      return sortDesc;
    }
    public void setSortDesc(boolean sortDesc) {
      this.sortDesc = sortDesc;
    }
  }
  
  public static class LeftoverDigest {
    
    private int id;
    private String name;
    private String source;
    private String boxColor;
    private Date addedAt;
    
    public int getId() {
      return id;
    }
    public void setId(int id) {
      this.id = id;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getSource() {
      return source;
    }
    public void setSource(String source) {
      this.source = source;
    }
    public String getBoxColor() {
      return boxColor;
    }
    public void setBoxColor(String boxColor) {
      this.boxColor = boxColor;
    }
    public void setAddedAt(Date addedAt) {
      this.addedAt = addedAt;
    }
    public String getDateAdded() {
      return dateFormat.format(addedAt);
    }
  }

  @Override
  public List<LeftoverDigest> handleRequest(GetRequest request, Context context) {
    ILeftoverService service = Utils.dbService();
    final List<Leftover> leftovers;
    if (request.isSortedByDate()) {
      leftovers = service.listSortedByDateAdded(request.isSortDesc() ? SortDir.DESC : SortDir.ASC);
    } else {
      leftovers = service.list();
    }
    return leftovers.stream().map(lo -> {
      LeftoverDigest lod = new LeftoverDigest();
      lod.setId(lo.getId());
      lod.setName(lo.getName());
      lod.setSource(lo.getSource());
      lod.setBoxColor(lo.getBoxColor());
      lod.setAddedAt(lo.getDateAdded());
      return lod;
    }).collect(Collectors.toList()); 
  }
}
