package com.littlehouse_design.jsonparsing.Utils.Cart;

/**
 * Created by johnkonderla on 1/14/17.
 */

public class Order {
    private int id;
    private String orderName;
    private String transDate;
    private String pickupTime;
    private String accountNumb;
    private int subTotal;
    private int taxableAmount;
    private int tax;
    private int total;
    private int status;

    public Order(){

    }

    public int getId() {
        return id;
    }

    public String getOrderName() {
        return orderName;
    }

    public int getStatus() {
        return status;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public int getTax() {
        return tax;
    }

    public int getTaxableAmount() {
        return taxableAmount;
    }

    public int getTotal() {
        return total;
    }

    public String getAccountNumb() {
        return accountNumb;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setAccountNumb(String accountNumb) {
        this.accountNumb = accountNumb;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public void setTaxableAmount(int taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }
}
