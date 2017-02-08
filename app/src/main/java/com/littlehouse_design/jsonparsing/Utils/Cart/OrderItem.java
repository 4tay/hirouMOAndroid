package com.littlehouse_design.jsonparsing.Utils.Cart;

import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;

/**
 * Created by johnkonderla on 1/14/17.
 */

public class OrderItem {
    private int id;
    private int orderId;
    private String itemName;
    private String itemNumb;
    private int itemPrice;
    private int quantity;

    public OrderItem() {

    }
    public OrderItem(Item item) {
        this.itemName = item.getItemDescriptor();
        this.itemNumb = item.getItemNumber();
        this.itemPrice = (int) item.getPrice() * 100;
    }

    public int getId() {
        return id;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemNumb() {
        return itemNumb;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }



    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemNumb(String itemNumb) {
        this.itemNumb = itemNumb;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
