package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.recent.AppleSoftwareUpdates;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum RecentEvents {
    INSTANCE;

    private final RecentEventController[] events = new RecentEventController[] {
            AppleSoftwareUpdates.INSTANCE
    };

    public void refresh(CompletionHandler handler) {
        final LocalDate now = LocalDate.now();
        final int max = events.length;
        final HashMap<RecentEventType, HashSet<String>> values = new HashMap<>();
        final AtomicInteger completion = new AtomicInteger(0);
        Arrays.stream(events).parallel().forEach(event -> {
            event.refresh(now, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(object != null) {
                        final RecentEventType type = event.getType();
                        values.putIfAbsent(type, new HashSet<>());
                        values.get(type).add(object.toString());
                    }
                    if(completion.addAndGet(1) == max) {
                        String string = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirstType = true;
                            for(Map.Entry<RecentEventType, HashSet<String>> map : values.entrySet()) {
                                final RecentEventType type = map.getKey();
                                final HashSet<String> set = map.getValue();
                                if(!set.isEmpty()) {
                                    builder.append(isFirstType ? "" : ",").append("\"").append(type.getName()).append("\":{");
                                    isFirstType = false;
                                    boolean isFirst = true;
                                    for(String value : set) {
                                        builder.append(isFirst ? "" : ",").append(value);
                                        isFirst = false;
                                    }
                                }
                                builder.append("}");
                            }
                            builder.append("}");
                            string = builder.toString();
                        }
                        handler.handle(string);
                    }
                }
            });
        });
    }
}
