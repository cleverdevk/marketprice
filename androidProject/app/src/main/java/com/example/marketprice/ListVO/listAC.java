package com.example.marketprice.ListVO;

import java.io.Serializable;

public class listAC implements Serializable {

    private String title;
    private String date;
    private String detail;
    private String total;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getTotal() { return total; }
    public void setTotal(String total) { this.total = total; }


}
