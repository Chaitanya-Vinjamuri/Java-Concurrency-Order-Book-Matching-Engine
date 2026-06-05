import org.orderbooking.confirmation.TradeConfirmer;
import org.orderbooking.engine.MatchingEngine;
import org.orderbooking.history.TradeHistoryManager;
import org.orderbooking.model.Order;
import org.orderbooking.trader.TraderTask;
import org.orderbooking.util.OrderFileReader;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args)
            throws Exception {

        /*
         * BlockingQueue chosen because it provides
         * thread-safe communication between
         * trader threads and matching engine.
         */
        BlockingQueue<Order> orderQueue =
                new LinkedBlockingQueue<>();

        /*
         * Separate pool for trade confirmations.
         */
        ExecutorService confirmationPool =
                Executors.newFixedThreadPool(2);

        TradeHistoryManager historyManager =
                new TradeHistoryManager();

        TradeConfirmer confirmer =
                new TradeConfirmer(
                        historyManager
                );

        MatchingEngine engine =
                new MatchingEngine(
                        orderQueue,
                        confirmer,
                        confirmationPool
                );

        Thread engineThread =
                new Thread(engine);

        engineThread.start();

        List<Order> orders =
                OrderFileReader.loadOrders(
                        "orders.txt"
                );

        Map<String, List<Order>> traderOrders =
                new HashMap<>();

        for (Order order : orders) {

            traderOrders
                    .computeIfAbsent(
                            order.getTraderName(),
                            k -> new ArrayList<>()
                    )
                    .add(order);
        }

        /*
         * FixedThreadPool chosen because
         * the number of traders is known.
         *
         * CachedThreadPool is not suitable
         * because it can create unnecessary
         * threads and increase memory usage.
         */
        ExecutorService traderPool =
                Executors.newFixedThreadPool(
                        traderOrders.size()
                );

        List<Future<String>> futures =
                new ArrayList<>();

        for (Map.Entry<String, List<Order>> entry
                : traderOrders.entrySet()) {

            futures.add(

                    traderPool.submit(() -> {

                        new TraderTask(
                                entry.getKey(),
                                entry.getValue(),
                                orderQueue
                        ).run();

                        return entry.getKey();
                    })
            );
        }

        /*
         * Wait for all trader threads.
         */
        for (Future<String> future : futures) {

            System.out.println(
                    "Trader Completed -> "
                            + future.get()
            );
        }

        /*
         * No more orders will arrive.
         */
        engine.closeMarket();

        traderPool.shutdown();

        traderPool.awaitTermination(
                5,
                TimeUnit.SECONDS
        );

        /*
         * Wait for matching engine.
         */
        engineThread.join();

        /*
         * Wait for all confirmation
         * futures before summary.
         *
         * Using allOf().join() preserves
         * concurrency. Calling get()
         * immediately after submission
         * would serialize execution.
         */
        CompletableFuture.allOf(

                engine.getConfirmationFutures()
                        .toArray(
                                new CompletableFuture[0]
                        )

        ).join();

        confirmationPool.shutdown();

        confirmationPool.awaitTermination(
                5,
                TimeUnit.SECONDS
        );

        System.out.println(
                "\n===== FINAL SUMMARY ====="
        );

        System.out.println(
                "Orders Submitted : "
                        + orders.size()
        );

        System.out.println(
                "Trades Confirmed : "
                        + historyManager
                        .getTradeHistory()
                        .size()
        );

        System.out.println(
                "Confirmations Success : "
                        + historyManager
                        .getSuccessCount()
        );

        System.out.println(
                "Confirmations Failed : "
                        + historyManager
                        .getFailureCount()
        );

        System.out.println(
                "Unmatched Buy Orders : "
                        + engine.getRemainingBuyOrders()
        );

        System.out.println(
                "Unmatched Sell Orders : "
                        + engine.getRemainingSellOrders()
        );

        System.out.println(
                "\nAPPLICATION COMPLETED"
        );
    }
}