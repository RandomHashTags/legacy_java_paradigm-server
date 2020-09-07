import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLClient;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.ICountry;

public final class Canada implements ICountry {

    // https://laws.justice.gc.ca/eng/AnnualStatutes/index2020.html

    private static Canada INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new Canada();
        INSTANCE.init();
    }

    private void init() {
        LocalServer.start(Country.CANADA, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                client.sendResponse(getResponse(client.getTarget()));
            }
        });
    }

    @Override
    public Country getCountry() {
        return Country.CANADA;
    }

    private String getResponse(String input) {
        final String[] values = input.replace("?", "").split("/");
        switch (values[0]) {
            case "law":
                return "";
            default:
                return "error;input=" + input;
        }
    }
}
