package org.orderbooking.history;

import org.orderbooking.model.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TradeHistoryManager {


    private final List<Trade> tradeHistory =
            new ArrayList<>();

    /*
     * AtomicInteger chosen because
     * multiple confirmation threads
     * update these counters concurrently.
     *
     * volatile is NOT sufficient because
     * increment is not atomic.
     */
    private final AtomicInteger successCount =
            new AtomicInteger();

    private final AtomicInteger failureCount =
            new AtomicInteger();

    /*
     * synchronized protects writes to the
     * shared trade history list.
     *
     * Collections.synchronizedList alone
     * is not sufficient when iteration is
     * involved.
     */
    public void recordSuccess(Trade trade) {

        synchronized (tradeHistory) {

            tradeHistory.add(trade);
        }

        successCount.incrementAndGet();
    }

    public void recordFailure() {

        failureCount.incrementAndGet();
    }

    public List<Trade> getTradeHistory() {

        return tradeHistory;
    }

    public int getSuccessCount() {

        return successCount.get();
    }

    public int getFailureCount() {

        return failureCount.get();
    }


}
