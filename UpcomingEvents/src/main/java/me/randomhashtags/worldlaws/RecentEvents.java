package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.recent.software.AppleSoftwareUpdates;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import me.randomhashtags.worldlaws.recent.software.PlayStation4Updates;
import me.randomhashtags.worldlaws.recent.software.PlayStation5Updates;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum RecentEvents {
    INSTANCE;

    private final RecentEventController[] events = new RecentEventController[] {
            AppleSoftwareUpdates.INSTANCE,
            PlayStation4Updates.INSTANCE,
            PlayStation5Updates.INSTANCE
    };

    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final LocalDate lastWeek = LocalDate.now().minusDays(7);
        final int max = events.length;
        final HashMap<RecentEventType, HashSet<String>> values = new HashMap<>();
        final AtomicInteger completion = new AtomicInteger(0);
        Arrays.stream(events).parallel().forEach(event -> {
            final long eventStarted = System.currentTimeMillis();
            event.refresh(lastWeek, new CompletionHandler() {
                @Override
                public void handleHashSetString(HashSet<String> hashset) {
                    final int amount = hashset != null ? hashset.size() : 0;
                    WLLogger.log(Level.INFO, "RecentEvents - loaded " + amount + " recent events for " + event.getClass().getSimpleName() + " (took " + (System.currentTimeMillis()-eventStarted) + "ms)");
                    if(amount > 0) {
                        final RecentEventType type = event.getType();
                        values.putIfAbsent(type, new HashSet<>());
                        values.get(type).addAll(hashset);
                    }
                    if(completion.addAndGet(1) == max) {
                        String value = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirstType = true;
                            for(Map.Entry<RecentEventType, HashSet<String>> map : values.entrySet()) {
                                final RecentEventType type = map.getKey();
                                final HashSet<String> set = map.getValue();
                                builder.append(isFirstType ? "" : ",").append("\"").append(type.getName()).append("\":{");
                                isFirstType = false;
                                boolean isFirst = true;
                                for(String s : set) {
                                    builder.append(isFirst ? "" : ",").append(s);
                                    isFirst = false;
                                }
                                builder.append("}");
                            }
                            builder.append("}");
                            value = builder.toString();
                        }
                        WLLogger.log(Level.INFO, "RecentEvents - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handleString(value);
                    }
                }
            });
        });
    }
}
