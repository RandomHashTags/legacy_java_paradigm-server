package me.randomhashtags.worldlaws.stream;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class CompletableFutures<T> {
    private static final int MAXIMUM_PARALLEL_THREADS = Settings.Performance.getMaximumParallelThreads();

    public void stream(JSONArray array, Consumer<? super T> action) {
        final List<Object> list = new ArrayList<>();
        for(Object obj : array) {
            list.add(obj);
        }
        stream(list, action);
    }
    public void stream(Collection<? super T> items, Consumer<? super T> action) {
        final Stream<? super T> a = items.stream();
        stream(items.size(), a, action);
    }

    private void stream(int itemsSize, Stream<? super T> items, Consumer<? super T> action) {
        if(itemsSize <= 0) {
            itemsSize = 32;
        }
        final int threadCount = Math.min(itemsSize, MAXIMUM_PARALLEL_THREADS);
        stream(items, action, threadCount);
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
        }, executor)
                        .exceptionally(throwable -> {
                            WLUtilities.saveException(throwable);
                            return null;
                        }))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        executor.shutdown();
    }
}
