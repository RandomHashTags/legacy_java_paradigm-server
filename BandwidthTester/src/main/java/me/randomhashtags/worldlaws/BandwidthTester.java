package me.randomhashtags.worldlaws;

import org.json.JSONObject;

import java.util.*;
import java.util.stream.IntStream;

public final class BandwidthTester implements UserServer, RestAPI {

    private static final Random RANDOM = new Random();
    private static final List<String> REQUESTS = Arrays.asList(
            "home"
    );

    public static void main(String[] args) {
        new BandwidthTester();
    }

    private BandwidthTester() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
        WLLogger.logInfo("How many requests per second should I simulate? (enter an integer value)");
        final int amount = Integer.parseInt(getUserInput());
        simulateRequestsPerSecond(amount);
        //simulatePayload();
    }

    @Override
    public TargetServer getTargetServer() {
        return null;
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
        final String target = REQUESTS.get(RANDOM.nextInt(REQUESTS.size()));
        final JSONObject json = requestJSONObject("http://localhost:0/v1/" + target, false, RequestMethod.GET, headers);
        WLLogger.logInfo("BandwidthTester - completed request #" + number + " out of " + max + " (took " + WLUtilities.getElapsedTime(started) + ")");
    }
}
