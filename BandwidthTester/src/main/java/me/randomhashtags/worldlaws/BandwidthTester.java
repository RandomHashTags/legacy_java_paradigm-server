package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.util.stream.IntStream;

public final class BandwidthTester implements RestAPI {

    public static void main(String[] args) {
        new BandwidthTester();
    }

    private BandwidthTester() {
        testBandwidth();
    }

    private void testBandwidth() {
        final int max = 1000;
        IntStream.range(1, max).parallel().forEach(integer -> {
            makeRequest(integer, max);
        });
    }

    private void makeRequest(int number, int max) {
        final long started = System.currentTimeMillis();
        requestJSONObject("http://localhost:0/v1/countries/countries", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                WLLogger.log(Level.INFO, "BandwidthTester - completed request #" + number + " out of " + max + " (took " + (System.currentTimeMillis()-started) + "ms)");
            }
        });
    }
}
