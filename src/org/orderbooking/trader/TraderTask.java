package org.orderbooking.trader;

import org.orderbooking.model.Order;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TraderTask implements Runnable {

    private String traderName;
    private List<Order> orders;
    private BlockingQueue<Order> orderQueue;

    public TraderTask(String traderName,
                      List<Order> orders,
                      BlockingQueue<Order> orderQueue) {

        this.traderName = traderName;
        this.orders = orders;
        this.orderQueue = orderQueue;
    }

    @Override
    public void run() {

        for (Order order : orders) {

            try {

                orderQueue.put(order);

                System.out.println(
                        traderName +
                                " submitted -> " +
                                order
                );

                Thread.sleep(100);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}