package me.randomhashtags.worldlaws.stream;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.settings.Settings;

import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public enum ParallelStream {
    ;

    private static final int MAXIMUM_PARALLEL_THREADS;

    static {
        MAXIMUM_PARALLEL_THREADS = Settings.Performance.getMaximumParallelThreads();
    }

    public static void stream(Collection<?> items, Consumer<? super Object> iterator) {
        stream(items.spliterator(), iterator);
    }
    public static void stream(Spliterator<?> items, Consumer<? super Object> iterator) {
        final ForkJoinPool pool = new ForkJoinPool(MAXIMUM_PARALLEL_THREADS);
        final Consumer<? super Object> test = (Consumer<Object>) o -> {
            try {
                iterator.accept(o);
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        };
        try {
            pool.submit(() -> StreamSupport.stream(items, true).forEach(test)).get();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
}
