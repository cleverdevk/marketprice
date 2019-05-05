package com.example.marketprice.Accounts;

import java.util.ArrayList;

public class myGroup {
    public String groupName;
    public ArrayList<String> child;
    public ArrayList<Integer> money;

    myGroup(String name){
        groupName = name;
        child = new ArrayList<String>();
        money =  new ArrayList<Integer>();
    }

    public String getName(String name) {
        return groupName;
    }
}
