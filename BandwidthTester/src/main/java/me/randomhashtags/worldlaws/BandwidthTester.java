package me.randomhashtags.worldlaws;

import java.util.*;
import java.util.stream.IntStream;

public final class BandwidthTester implements UserServer, RestAPI {

    private static final Random RANDOM = new Random();
    private static final List<String> REQUESTS = Arrays.asList(
            "privacyPolicy",
            "transparency",
            "",
            "developer"
    );

    public static void main(String[] args) {
        new BandwidthTester();
    }

    private BandwidthTester() {
        load();
        //test();
    }


    /*private void test() {
        final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
        postData.put("responseBodyV2", new JSONObject());
        postData.put("notificationType", "immediate");
        postData.put("subtype", "urgent");

        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("***REMOVED***", "***REMOVED***");
        headers.put("***REMOVED***", "Java");
        headers.put("***REMOVED***", "-1");
        final JSONObject json = postJSONObject("http://localhost:0/v1/premium/verify/apple/v2", postData, true, headers);
        WLLogger.logInfo("BandwidthTester;test;json=" + (json != null ? json.toString() : "null"));
    }*/
    private void test() {
        final String test = request("https://***REMOVED***", null, null);
        WLLogger.logInfo("BandwidthTester;test=" + test);
    }
    private void load() {
        WLLogger.logInfo("How many requests per second should I simulate? (enter an integer value)");
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
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        final String uuid = "***REMOVED***";
        //headers.put("***REMOVED***", uuid);
        //headers.put("***REMOVED***", "BandwidthTester");
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
    private void makeRequest(LinkedHashMap<String, String> headers, int number, int max) {
        final long started = System.currentTimeMillis();
        final String target = REQUESTS.get(RANDOM.nextInt(REQUESTS.size()));
        try {
            final String json = WLUtilities.makeRequest("https://***REMOVED***/" + target);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        WLLogger.logInfo("BandwidthTester - completed request #" + number + " out of " + max + " (took " + WLUtilities.getElapsedTime(started) + ")");
    }
}
