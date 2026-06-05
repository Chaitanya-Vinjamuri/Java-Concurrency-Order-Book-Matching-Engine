package org.orderbooking.model;

public class Trade {

    private Order buyOrder;
    private Order sellOrder;
    private double matchedPrice;

    public Trade(
            Order buyOrder,
            Order sellOrder,
            double matchedPrice) {

        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.matchedPrice = matchedPrice;
    }

    public Order getBuyOrder() {
        return buyOrder;
    }

    public Order getSellOrder() {
        return sellOrder;
    }

    public double getMatchedPrice() {
        return matchedPrice;
    }

    @Override
    public String toString() {

        return "TRADE -> Buyer: "
                + buyOrder.getTraderName()
                + ", Seller: "
                + sellOrder.getTraderName()
                + ", Price: "
                + matchedPrice;
    }
}