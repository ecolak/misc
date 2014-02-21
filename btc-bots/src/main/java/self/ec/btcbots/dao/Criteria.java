package self.ec.btcbots.dao;

import java.util.ArrayList;
import java.util.List;


public class Criteria {

	public static enum Operator {
		EQ("="), NEQ("!="), GT(">"), GTE(">="), LT("<"), LTE("<=");
		
		private final String symbol;
		Operator(String symbol) {
			this.symbol = symbol;
		}
		
		public String getSymbol() { return symbol; }
	}
	
	public static class Condition {
		private String columnName;
		private Object columnValue;
		private Operator operator = Operator.EQ;
		
		public Condition(String columnName, Object columnValue) {
			this.columnName = columnName;
			this.columnValue = columnValue;
		}
		
		public Condition(String columnName, Object columnValue, Operator operator) {
			this.columnName = columnName;
			this.columnValue = columnValue;
			this.operator = operator;
		}

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public Object getColumnValue() {
			return columnValue;
		}

		public void setColumnValue(Object columnValue) {
			this.columnValue = columnValue;
		}

		public Operator getOperator() {
			return operator;
		}

		public void setOperator(Operator operator) {
			this.operator = operator;
		}
	}
	
	private List<Condition> conditions = new ArrayList<Condition>();
	private int page;
	private int pageSize;
	private boolean paged;
	private String orderBy;
	
	public Criteria() {}
	
	public Criteria addColumn(String columnName, Object columnValue) {
		return addColumn(columnName, columnValue, Operator.EQ);
	}
	
	public Criteria addColumn(String columnName, Object columnValue, Operator operator) {
		conditions.add(new Condition(columnName, columnValue, operator));
		return this;
	}
	
	public Criteria setPagination(int page, int pageSize) {
		this.page = page;
		this.pageSize = pageSize;
		this.paged = true;
		return this;
	}
	
	public Criteria setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public List<Condition> getCriteria() {
		return conditions;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public boolean isPaged() {
		return paged;
	}
	
}