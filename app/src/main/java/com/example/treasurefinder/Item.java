package com.example.treasurefinder;

public class Item {
    String name;
    double price;
    String description;
    String id;

    public Item( String name, double price, String description, String id) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
