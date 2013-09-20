package com.abe.olasihaber.model;

public class Result {

	private Object objects;
	private int page;
	private int totalPages;
	
	public Result() {}

	public Result(Object objects, int page, int totalPages) {
		this.objects = objects;
		this.page = page;
		this.totalPages = totalPages;
	}

	public Object getObjects() {
		return objects;
	}

	public void setObjects(Object objects) {
		this.objects = objects;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
}
