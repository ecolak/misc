package emre.colak.leftoverpolice.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import emre.colak.leftoverpolice.model.Leftover;

public class PostgresBackedLeftoverService implements ILeftoverService {

  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Error loading Postgres driver", e);
    }
  }
  
  private static final String TABLE_NAME = "leftovers";
  private static final String COL_ID = "id";
  private static final String COL_NAME = "name";
  private static final String COL_SOURCE = "source";
  private static final String COL_BOX_COLOR = "box_color";
  private static final String COL_DATE_ADDED = "date_added";
  private static final String COL_IS_DELETED = "is_deleted";
  
  private static final String DEFAULT_HOST = "127.0.0.1";
  private static final int DEFAULT_PORT = 5432;
  
  private final String username;
  private final String password;
  
  private final String url;
    
  public PostgresBackedLeftoverService(String db, String username, String password) {
    this(DEFAULT_HOST, db, username, password);
  }
  
  public PostgresBackedLeftoverService(String host, String db, String username, String password) {
    this(host, DEFAULT_PORT, db, username, password);
  }
  
  public PostgresBackedLeftoverService(String host, int port, String db, String username, String password) {
    Objects.requireNonNull(host);
    Objects.requireNonNull(db);
    Objects.requireNonNull(username);
    
    this.username = username;
    this.password = password;
    this.url = String.format("jdbc:postgresql://%s:%d/%s", host, port, db);
  }

  @Override
  public void create(Leftover leftover) {
    Objects.requireNonNull(leftover);
    
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_NAME)
          .append(" (").append(COL_NAME).append(",").append(COL_SOURCE).append(",")
          .append(COL_BOX_COLOR).append(") values (?,?,?)");
      PreparedStatement ps = conn.prepareStatement(sb.toString());
      int i = 1;
      ps.setString(i++, leftover.getName());
      ps.setString(i++, leftover.getSource());
      ps.setString(i++, leftover.getBoxColor());
      ps.executeUpdate();
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public void delete(int id) {
    // Does not really delete. Marks it deleted
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET ")
          .append(COL_IS_DELETED).append(" = true WHERE ").append(COL_ID).append(" = ?");
      PreparedStatement ps = conn.prepareStatement(sb.toString());
      ps.setInt(1, id);
      ps.executeUpdate();
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }
  
  @Override
  public int deleteAll() {
 // Does not really delete. Marks it deleted
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET ")
          .append(COL_IS_DELETED).append(" = true");
      return conn.prepareStatement(sb.toString()).executeUpdate();
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public List<Leftover> list() {
    return select(null, null);
  }

  @Override
  public List<Leftover> listSortedByDateAdded(SortDir sortDir) {
    Objects.requireNonNull(sortDir);
    return select(Arrays.asList(COL_DATE_ADDED), sortDir);
  }
  
  @Override
  public List<String> listNames() {
    List<String> result = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      PreparedStatement ps = conn.prepareStatement(buildSQL(Arrays.asList(COL_NAME), null, null));
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(rs.getString(COL_NAME));
      }
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
    return result;
  }
  
  private List<Leftover> select(List<String> sortColumns, SortDir sortDir) {
    List<Leftover> result = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      PreparedStatement ps = conn.prepareStatement(buildSQL(null, sortColumns, sortDir));
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(fromResultSet(rs));
      }
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
    return result;
  }
  
  private static String buildSQL(List<String> selectColumns, List<String> sortColumns, SortDir sortDir) {
    StringBuilder sb = new StringBuilder("SELECT ");
    if (selectColumns == null || selectColumns.isEmpty()) {
      sb.append("*");
    } else {
      sb.append(selectColumns.stream().collect(Collectors.joining(", ")));
    }
    sb.append(" FROM ").append(TABLE_NAME).append(" WHERE ").append(COL_IS_DELETED).append(" = false");
    if (sortDir != null && sortColumns != null && !sortColumns.isEmpty()) {
      sb.append(" ORDER BY ")
        .append(sortColumns.stream().collect(Collectors.joining(",")))
        .append(" ").append(sortDir.name());
    }
    return sb.toString();
  }

  private static Leftover fromResultSet(ResultSet rs) throws SQLException {
    Leftover lo = new Leftover();
    lo.setId(rs.getInt(COL_ID));
    lo.setName(rs.getString(COL_NAME));
    lo.setSource(rs.getString(COL_SOURCE));
    lo.setBoxColor(rs.getString(COL_BOX_COLOR));
    lo.setDateAdded(rs.getTimestamp(COL_DATE_ADDED));
    lo.setDeleted(rs.getBoolean(COL_IS_DELETED));
    return lo;
  }

  @Override
  public List<Leftover> searchByName(String name) {
    Objects.requireNonNull(name);
    List<Leftover> result = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME)
          .append(" WHERE ").append(COL_IS_DELETED).append(" = false AND ")
          .append(COL_NAME).append(" = ?");
      PreparedStatement ps = conn.prepareStatement(sb.toString());
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(fromResultSet(rs));
      }
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
    return result;
  }
}
