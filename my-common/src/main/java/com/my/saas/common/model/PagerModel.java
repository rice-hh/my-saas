package com.my.saas.common.model;

import java.io.Serializable;

public class PagerModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private int total;

    private int totalPages;

    private int pageNumber;

    private Object data;

    public PagerModel() {

    }

    public PagerModel(int total, Object data, int page, int rows) {
        this.total = total;
        this.data = data;
        this.pageNumber = page;
        this.totalPages = (total % rows == 0) ? (total / rows) : (total / rows + 1);
    }


    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
