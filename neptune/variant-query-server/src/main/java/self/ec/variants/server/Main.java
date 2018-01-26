package self.ec.variants.server;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

public class Main {

  private static final String VARIANTS_PARQUET = "/Users/emre/tmp/variants.parquet";
  private static final String VARIANTS_TABLE = "variants";
  private static final int MAX_RESULT_SIZE = 100;
  
  private static class JsonTransformer implements ResponseTransformer {
    private final ObjectMapper om = new ObjectMapper();
    
    @Override
    public String render(Object o) throws Exception {
      return om.writeValueAsString(o);
    }
  }

  public static void main(String[] args) {
    String master = args.length > 0 ? args[0] : "local";
    System.out.println("Spark master url: " + master);
    
    SparkSession spark = SparkSession.builder().master(master).appName("Variant Query Server").getOrCreate();
    Dataset<Row> df = spark.read().parquet(VARIANTS_PARQUET);
    df.createOrReplaceTempView(VARIANTS_TABLE);
    
    System.out.println("Done connecting to Spark SQL");

    get("/health", (req, res) -> "");

    post("/query", (req, res) -> {
      String query = req.body();
      if (query.trim().length() == 0) {
        throw new IllegalArgumentException("Blank query not allowed");
      }

      Dataset<Row> rows = spark.sql(query);
      long count = rows.count();
      System.out.println("Result size: " + count);

      if (count > MAX_RESULT_SIZE) {
        throw new IllegalArgumentException("Result size is too big");
      }
      
      return dataSetToResponse(rows);
    }, new JsonTransformer());
    
    after((request, response) -> {
      response.type("application/json");
    });
    
    exception(IllegalArgumentException.class, (exception, request, response) -> {
      response.status(400);
      response.body(exception.getMessage());
    });
  }

  private static List<Map<String, Object>> dataSetToResponse(Dataset<Row> dataSet) {
    List<Row> result = dataSet.collectAsList();
    String[] columns = dataSet.columns();
    List<Map<String, Object>> list = new ArrayList<>();
    for (Row row : result) {
      Map<String, Object> map = new HashMap<>();
      for (String col : columns) {
        map.put(col, row.get(row.fieldIndex(col)));
      }
      list.add(map);
    }
    return list;
  }
}
