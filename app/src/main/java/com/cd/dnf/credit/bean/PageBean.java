package com.cd.dnf.credit.bean;

/**
 * Created by jack on 2018/2/2.
 * 数据分页bean
 */

public class PageBean {
    private int total;
    private String data;
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
