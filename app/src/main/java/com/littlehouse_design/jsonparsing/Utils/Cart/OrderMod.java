package com.littlehouse_design.jsonparsing.Utils.Cart;

/**
 * Created by johnkonderla on 2/14/17.
 */

public class OrderMod {

    String name;
    float price;

    public OrderMod(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }
}
