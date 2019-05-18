package com.example.marketprice.Accounts;

public class AccountingListItem {
    private String title, start_date, end_date;
    private String no;

    public AccountingListItem()
    {

    }
    public AccountingListItem(String title, String  no)
    {
        this.no = no;
        this.title = title;
        this.start_date = "";
        this.end_date = "";
    }
    public AccountingListItem(String title, String start_date, String end_date, String no)
    {
        this.no = no;
        this.title = title;
        this.start_date  = start_date;
        this.end_date = end_date;
    }
    public String getTitle(){
        return title;
    }
    public String getStartDate(){
        return start_date;
    }
    public String getEndDate(){
        return end_date;
    }
}
