package fr.lfremaux.fxsandbox.core.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadPool {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final ExecutorService safePool = Executors.newSingleThreadExecutor();
    private static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

    private static final Set<UUID> activeTimers = new LinkedHashSet<>();

    public static ExecutorService getThreadPool() {
        return threadPool;
    }

    public static ExecutorService getSafePool() {
        return safePool;
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static CompletableFuture<Void> executeWithCallback(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, threadPool);
    }

    public static CompletableFuture<Void> execute(Runnable runnable, long time, TimeUnit unit) {
        return CompletableFuture.runAsync(runnable, threadPool)
                .orTimeout(time, unit)
                .exceptionally(t -> {
                    return null;
                });
    }

    public static <T> CompletableFuture<T> supply(Supplier<T> consumer) {
        return CompletableFuture.supplyAsync(consumer, threadPool);
    }

    public static <T> CompletableFuture<T> supply(Supplier<T> consumer, long time, TimeUnit unit) {
        return CompletableFuture.supplyAsync(consumer, threadPool)
                .orTimeout(time, unit)
                .exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                });
    }

    public static Future<?> submit(Runnable runnable) {
        return threadPool.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return threadPool.submit(callable);
    }

    public static void safeExecute(Runnable runnable) {
        try {
            safePool.execute(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Future<?> safeSubmit(Runnable runnable) {
        try {
            return safePool.submit(runnable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> CompletableFuture<T> futureExecute(Consumer<CompletableFuture<T>> consumer) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        consumer.accept(future);
        return future;
    }

    public static <T> CompletableFuture<T> futureExecute(Consumer<CompletableFuture<T>> consumer, long time, TimeUnit unit) {
        return futureExecute(consumer, null, time, unit);
    }

    public static <T> CompletableFuture<T> futureExecute(Consumer<CompletableFuture<T>> consumer, T defaultValue, long time, TimeUnit unit) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            consumer.accept(future);
        }, threadPool)
                .orTimeout(time, unit)
                .exceptionally(t -> {
                    if (t instanceof TimeoutException) {
                        future.complete(defaultValue);
                    } else {
                        future.completeExceptionally(t);
                        t.printStackTrace();
                    }
                    return null;
                });
        return future;
    }

    public static void executeLater(Runnable runnable, long delay, TimeUnit unit) {
        scheduled.schedule(() -> {
            threadPool.execute(runnable);
        }, delay, unit);
    }

    public static UUID registerTimer(Runnable runnable, long start, long timer, TimeUnit unit) {
        UUID uuid = UUID.randomUUID();
        synchronized (activeTimers) {
            activeTimers.add(uuid);
        }
        executeTimer(uuid, runnable, start, timer, unit);
        return uuid;
    }

    public static UUID registerTimer(Consumer<UUID> runnable, long start, long timer, TimeUnit unit) {
        UUID uuid = UUID.randomUUID();
        synchronized (activeTimers) {
            activeTimers.add(uuid);
        }
        executeTimer(uuid, runnable, start, timer, unit);
        return uuid;
    }

    public static void executeTimer(UUID uuid, Runnable runnable, long start, long timer, TimeUnit unit) {
        synchronized (activeTimers) {
            activeTimers.add(uuid);
        }
        final Recursive<Runnable> recur = new Recursive<>();
        recur.func = () -> {
            synchronized (activeTimers) {
                if (!activeTimers.contains(uuid))
                    return;
            }

            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            executeLater(recur.func, timer, unit);
        };

        executeLater(recur.func, start, unit);
    }

    public static void executeTimer(UUID uuid, Consumer<UUID> runnable, long start, long timer, TimeUnit unit) {
        synchronized (activeTimers) {
            activeTimers.add(uuid);
        }
        final Recursive<Runnable> recur = new Recursive<>();
        recur.func = () -> {
            synchronized (activeTimers) {
                if (!activeTimers.contains(uuid))
                    return;
            }

            try {
                runnable.accept(uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            executeLater(recur.func, timer, unit);
        };

        executeLater(recur.func, start, unit);
    }

    public static void unregisterTimer(UUID uuid) {
        synchronized (activeTimers) {
            activeTimers.remove(uuid);
        }
    }

    private static class Recursive<I> {
        public I func;
    }
}
