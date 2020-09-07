package me.randomhashtags.worldlaws.event;

public final class EventSources {
    private EventSource[] sources;

    public EventSources(EventSource...sources) {
        this.sources = sources;
    }

    public EventSource[] getSources() {
        return sources;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(EventSource source : sources) {
            builder.append(isFirst ? "" : ",").append(source.toString());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
}
