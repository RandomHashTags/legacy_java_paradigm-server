package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.IntStream;

public final class BandwidthTester implements UserServer, RestAPI {

    public static void main(String[] args) {
        new BandwidthTester();
    }

    private BandwidthTester() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
        WLLogger.log(Level.INFO, "How many requests per second should I simulate? (enter an integer value)");
        final int amount = Integer.parseInt(getUserInput());
        simulateRequestsPerSecond(amount);
        //simulatePayload();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    private void simulateRequestsPerSecond(int amount) {
        final Timer timer = new Timer();
        final HashMap<String, String> headers = new HashMap<>();
        final String uuid = "***REMOVED***";
        headers.put("***REMOVED***", uuid);
        headers.put("***REMOVED***", "BandwidthTester");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                IntStream.range(0, amount).parallel().forEach(i -> {
                    makeRequest(headers, i, amount);
                });
            }
        }, 0, 1_000);
    }

    private void simulatePayload() {
        final int max = 1_000;
        IntStream.range(1, max).parallel().forEach(integer -> {
            makeRequest(null, integer, max);
        });
    }
    private void makeRequest(HashMap<String, String> headers, int number, int max) {
        final long started = System.currentTimeMillis();
        requestJSONObject("http://localhost:0/v1/home", false, RequestMethod.GET, headers, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                WLLogger.log(Level.INFO, "BandwidthTester - completed request #" + number + " out of " + max + " (took " + (System.currentTimeMillis()-started) + "ms)");
            }
        });
    }
}
