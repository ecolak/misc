package ec.self.whistlelang.sql;

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
        if (dir == null) {
            dir = OrderByDir.ASC;
        }
        this.dir = dir;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public OrderByDir getDir() {
        return this.dir;
    }
}
