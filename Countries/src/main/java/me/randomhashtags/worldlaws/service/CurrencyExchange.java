package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.country.WLCurrency;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum CurrencyExchange {
    ;

    private static final HashMap<WLCurrency, HashMap<WLCurrency, Double>> EXCHANGE_RATES = new HashMap<>();

    static {
        for(WLCurrency currency : WLCurrency.values()) {
            EXCHANGE_RATES.put(currency, new HashMap<>());
        }
    }

    public static JSONObjectTranslatable getResponse(ServerRequest request) {
        final HashMap<String, String> query = request.getQuery();
        if(query != null && query.containsKey("from") && query.containsKey("to")) {
            final String from = query.get("from"), to = query.get("to");
            final WLCurrency fromCurrency = WLCurrency.valueOf(from), toCurrency = WLCurrency.valueOf(to);
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            final double value = get(fromCurrency, toCurrency);
            json.put("value", value);
            return json;
        }
        return null;
    }

    public static double get(WLCurrency from, WLCurrency to) {
        if(from == to) {
            return 1;
        } else if(EXCHANGE_RATES.get(from).containsKey(to)) {
            return EXCHANGE_RATES.get(from).get(to);
        } else {
            return update(from, to);
        }
    }

    private static double update(WLCurrency from, WLCurrency to) {
        final String url = "https://www.xe.com/currencyconverter/convert/?Amount=1&From=" + from.name() + "&To=" + to.name();
        final Document doc = Jsoupable.getStaticDocument(Folder.OTHER, url, false);
        double value = 0;
        if(doc != null) {
            final Elements form = doc.selectFirst("div main form").select("div p");
            final String[] values = form.text().split(" = ");
            final String[] toValues = values[1].split(" ");
            final double toValue = Double.parseDouble(toValues[0]);
            value = toValue;
            EXCHANGE_RATES.get(from).put(to, toValue);

            final double fromValue = Double.parseDouble(values[2].split(" ")[0]);
            EXCHANGE_RATES.get(to).put(from, fromValue);
        }
        return value;
    }

    public static void clear() {
        EXCHANGE_RATES.clear();
    }
}
