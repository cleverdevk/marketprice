package com.example.marketprice.Accounts;

public interface MyDialogListener {
    void onPositiveClicked(String when, String what, int howMuch);

    void onNegativeClicked();
}
