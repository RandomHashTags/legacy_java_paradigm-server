package me.randomhashtags.worldlaws;

public interface WLServer extends DataValues {
    TargetServer getServer();
    default void sendRequest(String target, CompletionHandler handler) {
        if(target.equals("ping")) {
            handler.handle(true);
        } else {
            getResponse(target, handler);
        }
    }
    void getResponse(String target, CompletionHandler handler);
}
