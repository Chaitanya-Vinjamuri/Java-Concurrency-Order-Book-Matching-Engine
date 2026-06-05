package org.orderbooking.model;

public class Order {

    private String traderName;
    private Side side;
    private double price;
    private int quantity;

    public Order(String traderName,
                  Side side,
                 double price,
                 int quantity) {

        this.traderName = traderName;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public String getTraderName() {
        return traderName;
    }

    public Side getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {

        return traderName +
                " " +
                side +
                " " +
                price +
                " " +
                quantity;
    }

    public enum Side {
        BUY,
        SELL
    }
}