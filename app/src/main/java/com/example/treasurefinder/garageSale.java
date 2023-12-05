package com.example.treasurefinder;

import java.util.Arrays;

public class garageSale {
    String title;
    String date;

    String address;
    String hours;
    String TUID;
    String[] items;

    public garageSale(String title, String address, String date, String hours, String TUID, String[] items) {
        this.title = title;
        this.address = address;
        this.date = date;
        this.hours = hours;
        this.TUID = TUID;
        this.items = items;
    }

    @Override
    public String toString() {
        return "garageSale{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", hours='" + hours + '\'' +
                ", TUID='" + TUID + '\'' +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
