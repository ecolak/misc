package self.ec.argume.model;

import java.util.List;

public class ResultList<T> {

	private List<T> objects;
	private int page;
	private int pageSize;
	private int totalPages;
	private long total;
	
	public ResultList() {}
	
	public ResultList(List<T> objects, int page, int pageSize, long total) {
		this.objects = objects;
		this.page = page;
		this.total = total;
		setTotalPages();
	}

	public List<T> getObjects() {
		return objects;
	}

	public void setObjects(List<T> objects) {
		this.objects = objects;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		setTotalPages();
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
		setTotalPages();
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	
	private void setTotalPages() {
		if (pageSize > 0) {
			this.totalPages = (int) Math.ceil(total / (float) pageSize); 
		}
	}
}
