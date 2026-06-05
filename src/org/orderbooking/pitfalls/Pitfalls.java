package org.orderbooking.pitfalls;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Pitfalls {

    private static int raceCounter = 0;

    private static volatile int volatileCounter = 0;

    private static AtomicInteger atomicCounter =
            new AtomicInteger();

    private static final Object lockA =
            new Object();

    private static final Object lockB =
            new Object();

    /*
     * Race Condition Demo
     * Multiple threads update the same variable
     * without synchronization.
     */
    private static void raceConditionDemo()
            throws Exception {

        raceCounter = 0;

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                raceCounter++;
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Race Condition Count = "
                        + raceCounter
        );
    }

    /*
     * Fix for race condition using synchronized.
     */
    private static void raceConditionFixedDemo()
            throws Exception {

        final Object counterLock =
                new Object();

        raceCounter = 0;

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                synchronized (counterLock) {

                    raceCounter++;
                }
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Race Condition Fixed Count = "
                        + raceCounter
        );
    }

    /*
     * volatile only provides visibility.
     * It does NOT make increment atomic.
     */
    private static void volatileDemo()
            throws Exception {

        volatileCounter = 0;

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                volatileCounter++;
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Volatile Counter = "
                        + volatileCounter
        );
    }

    /*
     * AtomicInteger fixes the counter issue
     * because increment operation is atomic.
     */
    private static void atomicDemo()
            throws Exception {

        atomicCounter.set(0);

        Runnable task = () -> {

            for (int i = 0; i < 1000; i++) {

                atomicCounter.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(
                "Atomic Counter = "
                        + atomicCounter.get()
        );
    }

    /*
     * Deadlock Demo
     * Thread 1 locks A then B
     * Thread 2 locks B then A
     */
    private static void deadlockDemo() {

        Thread t1 = new Thread(() -> {

            synchronized (lockA) {

                System.out.println(
                        "Thread 1 acquired Lock A"
                );

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (lockB) {

                    System.out.println(
                            "Thread 1 acquired Lock B"
                    );
                }
            }
        });

        Thread t2 = new Thread(() -> {

            synchronized (lockB) {

                System.out.println(
                        "Thread 2 acquired Lock B"
                );

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (lockA) {

                    System.out.println(
                            "Thread 2 acquired Lock A"
                    );
                }
            }
        });

        t1.start();
        t2.start();
    }

    /*
     * Deadlock Fix
     * Both threads acquire locks
     * in the same order.
     */
    private static void deadlockFixedDemo()
            throws Exception {

        Thread t1 = new Thread(() -> {

            synchronized (lockA) {

                synchronized (lockB) {

                    System.out.println(
                            "Thread1 completed"
                    );
                }
            }
        });

        Thread t2 = new Thread(() -> {

            synchronized (lockA) {

                synchronized (lockB) {

                    System.out.println(
                            "Thread2 completed"
                    );
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(
                "Deadlock Fixed Successfully"
        );
    }

    /*
     * Wrong approach:
     * Calling get() immediately
     * serializes execution.
     */
    private static void getTooEarlyDemo()
            throws Exception {

        long start =
                System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {

            CompletableFuture<Void> future =

                    CompletableFuture.runAsync(() -> {

                        try {

                            Thread.sleep(1000);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    });

            future.get();
        }

        long end =
                System.currentTimeMillis();

        System.out.println(
                "Serialized Time = "
                        + (end - start)
                        + " ms"
        );
    }

    /*
     * Correct approach:
     * allOf().join() preserves
     * concurrency.
     */
    private static void allOfDemo() {

        long start =
                System.currentTimeMillis();

        CompletableFuture<?>[] futures =
                new CompletableFuture[3];

        for (int i = 0; i < 3; i++) {

            futures[i] =

                    CompletableFuture.runAsync(() -> {

                        try {

                            Thread.sleep(1000);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    });
        }

        CompletableFuture
                .allOf(futures)
                .join();

        long end =
                System.currentTimeMillis();

        System.out.println(
                "Parallel Time = "
                        + (end - start)
                        + " ms"
        );
    }

    public static void main(String[] args)
            throws Exception {

        System.out.println(
                "\n===== RACE CONDITION DEMO ====="
        );
        raceConditionDemo();

        System.out.println(
                "\n===== RACE CONDITION FIX ====="
        );
        raceConditionFixedDemo();

        System.out.println(
                "\n===== VOLATILE DEMO ====="
        );
        volatileDemo();

        System.out.println(
                "\n===== ATOMIC INTEGER DEMO ====="
        );
        atomicDemo();

        System.out.println(
                "\n===== DEADLOCK DEMO ====="
        );

        // deadlockDemo(); // Uncomment to observe deadlock

        System.out.println(
                "Deadlock demo available in code. "
                        + "Execution skipped to avoid hanging."
        );

        System.out.println(
                "\n===== DEADLOCK FIX DEMO ====="
        );

        deadlockFixedDemo();

        System.out.println(
                "\n===== GET TOO EARLY DEMO ====="
        );
        getTooEarlyDemo();

        System.out.println(
                "\n===== ALLOF JOIN DEMO ====="
        );
        allOfDemo();
    }
}
