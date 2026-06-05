package org.orderbooking.engine;

import org.orderbooking.confirmation.TradeConfirmer;
import org.orderbooking.model.Order;
import org.orderbooking.model.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MatchingEngine implements Runnable {


    private final BlockingQueue<Order> orderQueue;

    /*
     * volatile is sufficient because we only need
     * visibility of marketOpen changes across threads.
     *
     * Main thread writes the value while the
     * matching engine reads it.
     *
     * volatile is NOT suitable for counters because
     * increment operations are not atomic.
     */
    private volatile boolean marketOpen = true;

    private final List<Order> buyOrders;
    private final List<Order> sellOrders;

    /*
     * ReentrantLock chosen because tryLock()
     * allows timeout and backoff.
     *
     * synchronized would block indefinitely
     * if another thread held the lock.
     *
     * This prevents the matching engine from
     * waiting forever.
     */
    private final ReentrantLock lock;

    private final TradeConfirmer tradeConfirmer;
    private final ExecutorService confirmationExecutor;

    private final List<CompletableFuture<Boolean>>
            confirmationFutures;

    public MatchingEngine(
            BlockingQueue<Order> orderQueue,
            TradeConfirmer tradeConfirmer,
            ExecutorService confirmationExecutor) {

        this.orderQueue = orderQueue;
        this.tradeConfirmer = tradeConfirmer;
        this.confirmationExecutor = confirmationExecutor;

        this.buyOrders = new ArrayList<>();
        this.sellOrders = new ArrayList<>();

        this.confirmationFutures =
                new ArrayList<>();

        this.lock = new ReentrantLock();
    }

    public void closeMarket() {

        marketOpen = false;
    }

    private void processOrder(Order order) {

        try {

            if (lock.tryLock(
                    50,
                    TimeUnit.MILLISECONDS)) {

                try {

                    if (order.getSide()
                            == Order.Side.BUY) {

                        buyOrders.add(order);

                    } else {

                        sellOrders.add(order);
                    }

                    System.out.println(
                            "Engine received -> "
                                    + order
                    );

                } finally {

                    lock.unlock();
                }
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void tryMatch() {

        Order matchedBuy = null;
        Order matchedSell = null;

        for (Order buy : buyOrders) {

            for (Order sell : sellOrders) {

                if (buy.getPrice()
                        >= sell.getPrice()) {

                    matchedBuy = buy;
                    matchedSell = sell;

                    break;
                }
            }

            if (matchedBuy != null) {

                break;
            }
        }

        if (matchedBuy != null) {

            buyOrders.remove(matchedBuy);
            sellOrders.remove(matchedSell);

            Trade trade = new Trade(
                    matchedBuy,
                    matchedSell,
                    matchedSell.getPrice()
            );

            System.out.println(
                    "MATCH FOUND -> "
                            + trade
            );

            confirmationFutures.add(

                    tradeConfirmer.confirmTrade(
                            trade,
                            confirmationExecutor
                    )
            );
        }
    }

    public List<CompletableFuture<Boolean>>
    getConfirmationFutures() {

        return confirmationFutures;
    }

    public int getRemainingBuyOrders() {

        return buyOrders.size();
    }

    public int getRemainingSellOrders() {

        return sellOrders.size();
    }

    @Override
    public void run() {

        while (marketOpen
                || !orderQueue.isEmpty()) {

            try {

                Order order =
                        orderQueue.poll(
                                200,
                                TimeUnit.MILLISECONDS
                        );

                if (order != null) {

                    processOrder(order);

                    tryMatch();
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        System.out.println(
                "Matching Engine Stopped"
        );
    }


}
