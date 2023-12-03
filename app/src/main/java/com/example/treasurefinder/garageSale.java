package com.example.treasurefinder;

public class garageSale {
    String title;
    String host;
    String address;
    String hours;
    int TUID;

    public garageSale(String title, String host, String address, String hours, int TUID) {
        this.title = title;
        this.host = host;
        this.address = address;
        this.hours = hours;
        this.TUID = TUID;
    }

    @Override
    public String toString() {
        return "garageSale{" +
                "title='" + title + '\'' +
                ", host='" + host + '\'' +
                ", address='" + address + '\'' +
                ", hours='" + hours + '\'' +
                ", TUID=" + TUID +
                '}';
    }
}
