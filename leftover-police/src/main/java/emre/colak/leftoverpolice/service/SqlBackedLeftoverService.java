package emre.colak.leftoverpolice.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import emre.colak.leftoverpolice.model.Leftover;

public class SqlBackedLeftoverService implements ILeftoverService {

  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Error loading Postgres driver", e);
    }
  }
  
  private static final String TABLE_NAME = "leftovers";
  
  private static final String DEFAULT_HOST = "127.0.0.1";
  private static final int DEFAULT_PORT = 5432;
  
  private final String username;
  private final String password;
  
  private final String url;
    
  public SqlBackedLeftoverService(String db, String username, String password) {
    this(DEFAULT_HOST, db, username, password);
  }
  
  public SqlBackedLeftoverService(String host, String db, String username, String password) {
    this(host, DEFAULT_PORT, db, username, password);
  }
  
  public SqlBackedLeftoverService(String host, int port, String db, String username, String password) {
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
      PreparedStatement ps = conn.prepareStatement("INSERT INTO " + TABLE_NAME + " (id, name, source, box_color) values (?,?,?,?)");
      int i = 1;
      ps.setString(i++, leftover.getId());
      ps.setString(i++, leftover.getName());
      ps.setString(i++, leftover.getSource());
      ps.setString(i++, leftover.getBoxColor());
      ps.executeUpdate();
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public void delete(String id) {
    // Does not really delete. Marks it deleted
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      PreparedStatement ps = conn.prepareStatement("UPDATE " + TABLE_NAME + " SET is_deleted = true");
      ps.executeUpdate();
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public Iterable<Leftover> list() {
    return select(null);
  }

  @Override
  public Iterable<Leftover> listSortedByDateAdded(SortDir sortDir) {
    Objects.requireNonNull(sortDir);
    return select(sortDir);
  }
  
  private Iterable<Leftover> select(SortDir sortDir) {
    ArrayList<Leftover> result = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME);
      if (sortDir != null) {
        sb.append(" ORDER BY ").append(sortDir.name());
      }
      PreparedStatement ps = conn.prepareStatement(sb.toString());
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.add(fromResultSet(rs));
      }
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
    return result;
  }

  private static Leftover fromResultSet(ResultSet rs) throws SQLException {
    Leftover lo = new Leftover();
    lo.setId(rs.getString("id"));
    lo.setName(rs.getString("name"));
    lo.setSource(rs.getString("source"));
    lo.setBoxColor(rs.getString("box_color"));
    lo.setDateAdded(rs.getLong("date_added"));
    lo.setDeleted(rs.getBoolean("is_deleted"));
    return lo;
  }
}
