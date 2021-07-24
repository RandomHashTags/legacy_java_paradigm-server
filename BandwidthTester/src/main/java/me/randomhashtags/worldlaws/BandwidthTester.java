package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public final class BandwidthTester implements RestAPI {

    public static void main(String[] args) {
        new BandwidthTester();
    }

    private BandwidthTester() {
        simulateRequestsPerSecond(1);
        //simulatePayload();
    }

    private void simulateRequestsPerSecond(int amount) {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(int i = 1; i <= amount; i++) {
                    makeRequest(i, amount);
                }
            }
        }, 0, 1_000);
    }

    private void simulatePayload() {
        final int max = 1_000;
        IntStream.range(1, max).parallel().forEach(integer -> {
            makeRequest(integer, max);
        });
    }
    private void makeRequest(int number, int max) {
        final long started = System.currentTimeMillis();
        requestJSONObject("http://localhost:34551/v1/unitedstates/information", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                WLLogger.log(Level.INFO, "BandwidthTester - completed request #" + number + " out of " + max + " (took " + (System.currentTimeMillis()-started) + "ms)");
            }
        });
    }
}
