package com.littlehouse_design.jsonparsing.Utils.Cart;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by johnkonderla on 1/14/17.
 */

public class Cart {

    private Order order;
    private ArrayList<OrderItem> orderItems;
    private static final String LOG_TAG = Cart.class.getSimpleName();

    public Cart(Cursor orderCursor, Cursor itemCursor) {
        order = new Order();
        order.setId(orderCursor.getInt(0));
        order.setSubTotal(0);
        int subTotal = 0;
        orderItems = new ArrayList<>();

        if(itemCursor.moveToFirst()) {
            do{
                OrderItem orderItem = new OrderItem();
                orderItem.setId(itemCursor.getInt(0));
                orderItem.setOrderId(itemCursor.getInt(1));
                orderItem.setItemName(itemCursor.getString(2));
                orderItem.setItemNumb(itemCursor.getString(3));
                orderItem.setItemPrice(itemCursor.getInt(4));
                subTotal = subTotal + itemCursor.getInt(4);
                //Log.d(LOG_TAG,Integer.toString(subTotal));
                orderItem.setQuantity(1);
                orderItems.add(orderItem);
            } while(itemCursor.moveToNext());
        }
        order.setSubTotal(subTotal);

        //itemCursor.close();
        //orderCursor.close();


    }
    public Cart(Cursor orderCursor) {
        order = new Order();
        order.setId(orderCursor.getInt(0));
        order.setSubTotal(0);
        orderItems = new ArrayList<>();
    }
    public Cart() {

    }

    public Order getOrder() {
        return order;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    public int getOrderItemsCount() {
        return orderItems.size();
    }

    public void removeOrderItem(int i) {
        orderItems.remove(i);
    }
}
