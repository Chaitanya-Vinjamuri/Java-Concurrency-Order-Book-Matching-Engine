package org.orderbooking.trader;

public class NaiveTrader implements Runnable {

    @Override
    public void run() {

        System.out.println(
                Thread.currentThread().getName()
                        + " submitted order"
        );
    }

    public static void main(String[] args) {

        Runnable trader =
                new NaiveTrader();

        Thread thread =
                new Thread(trader);

        /*
         * start() creates a new thread.
         * run() executes on current thread.
         */
        thread.start();
    }
}