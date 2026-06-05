package org.orderbooking.confirmation;

import org.orderbooking.history.TradeHistoryManager;
import org.orderbooking.model.Trade;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TradeConfirmer {


    private final Random random =
            new Random();

    private final TradeHistoryManager
            historyManager;

    public TradeConfirmer(
            TradeHistoryManager historyManager) {

        this.historyManager = historyManager;
    }

    public CompletableFuture<Boolean> confirmTrade(
            Trade trade,
            ExecutorService executor) {

        /*
         * CompletableFuture is used because
         * trade confirmations should execute
         * asynchronously without blocking the
         * matching engine.
         *
         * Calling future.get() immediately
         * after submission would serialize
         * execution and defeat concurrency.
         *
         * Main.java waits for all confirmation
         * tasks using CompletableFuture
         * .allOf(...).join().
         */
        return CompletableFuture

                .supplyAsync(() -> {

                    try {

                        Thread.sleep(500);

                        /*
                         * Simulate ~10%
                         * confirmation failures.
                         */
                        if(random.nextInt(10) == 0) {

                            throw new RuntimeException(
                                    "Confirmation Failed"
                            );
                        }

                        return true;

                    } catch (InterruptedException e) {

                        Thread.currentThread()
                                .interrupt();

                        throw new RuntimeException(e);
                    }

                }, executor)

                .exceptionally(ex -> {

                    historyManager.recordFailure();

                    System.out.println(
                            "FAILED -> "
                                    + trade
                    );

                    return false;
                })

                .thenApply(result -> {

                    if (result) {

                        historyManager
                                .recordSuccess(
                                        trade
                                );

                        System.out.println(
                                "CONFIRMED -> "
                                        + trade
                        );
                    }

                    return result;
                });
    }


}
