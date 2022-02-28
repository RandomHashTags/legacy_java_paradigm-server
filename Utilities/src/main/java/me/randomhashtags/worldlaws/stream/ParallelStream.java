package me.randomhashtags.worldlaws.stream;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.settings.Settings;

import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public final class ParallelStream<T> {
    private static final int MAXIMUM_PARALLEL_THREADS = Settings.Performance.getMaximumParallelThreads();

    public void stream(Collection<? super T> items, Consumer<? super T> iterator) {
        stream(items.spliterator(), iterator);
    }
    public void stream(Spliterator<? super T> items, Consumer<? super T> iterator) {
        final ForkJoinPool pool = new ForkJoinPool(MAXIMUM_PARALLEL_THREADS);
        final Consumer<? super T> test = o -> {
            try {
                iterator.accept(o);
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        };
        try {
            @SuppressWarnings({ "unchecked" })
            final Consumer<Object> bruh = (Consumer<Object>) test;
            pool.submit(() -> StreamSupport.stream(items, true).forEach(bruh)).get();
            pool.shutdown();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
}
