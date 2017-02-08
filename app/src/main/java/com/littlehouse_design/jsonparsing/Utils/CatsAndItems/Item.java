package com.littlehouse_design.jsonparsing.Utils.CatsAndItems;

/**
 * Created by johnkonderla on 12/25/16.
 */

public class Item {
    private String itemNumber;
    private String itemDescriptor;
    private float price;

    public Item() {

    }
    public Item(String itemNumber, String itemDescriptor, float price) {
        this.itemNumber = itemNumber;
        this.itemDescriptor = itemDescriptor;
        this.price = price;
    }
    public String getItemNumber() {
        return itemNumber;
    }
    public String getItemDescriptor() {
        return itemDescriptor;
    }
    public float getPrice() {
        return price;
    }
    public void setItemDescriptor(String itemDescriptor) {
        this.itemDescriptor = itemDescriptor;
    }
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }
    public void setPrice(float price) {
        this.price = price;
    }
}
