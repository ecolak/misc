package ec.self.whistlelang.sql;

/**
 * Created by emre on 12/12/16.
 */

public class OrderBy {

    private final String columnName;
    private final OrderByDir dir;

    public OrderBy(String columnName) {
        this(columnName, null);
    }

    public OrderBy(String columnName, OrderByDir dir) {
        if (columnName == null) {
            throw new NullPointerException();
        }
        this.columnName = columnName;
        this.dir = dir == null ? OrderByDir.ASC : dir;
    }

    public String getColumnName() {
        return columnName;
    }

    public OrderByDir getDir() {
        return dir;
    }
}
