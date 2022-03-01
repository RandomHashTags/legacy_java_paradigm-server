package me.randomhashtags.worldlaws.stream;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.settings.Settings;

import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class CompletableFutures<T> {
    private static final int MAXIMUM_PARALLEL_THREADS = Settings.Performance.getMaximumParallelThreads();

    public void stream(Collection<? super T> items, Consumer<? super T> action) {
        final Stream<? super T> a = items.stream();
        stream(a, action, MAXIMUM_PARALLEL_THREADS);
    }

    public void stream(Spliterator<? super T> items, Consumer<? super T> action) {
        final Stream<? super T> a = StreamSupport.stream(items, false);
        stream(a, action, MAXIMUM_PARALLEL_THREADS);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void stream(Stream<? super T> items, Consumer<? super T> action, int numberOfThreads) {
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        final CompletableFuture[] futures = items.map(homie -> CompletableFuture.runAsync(() -> {
            try {
                final T t = (T) homie;
                action.accept(t);
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }, executor))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        executor.shutdown();
    }
}
