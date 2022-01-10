package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PhonesGoogle implements SmartphoneCompany {
    INSTANCE;

    private String modelsList;
    private HashMap<String, String> models;

    @Override
    public String getBackendID() {
        return "google";
    }

    @Override
    public void getSmartphoneListJSON(CompletionHandler handler) {
        if(modelsList != null) {
            handler.handleString(modelsList);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(modelsList);
                }
            });
        }
    }

    @Override
    public void getSmartphoneDetails(String model, CompletionHandler handler) {
        if(models != null) {
            handler.handleString(models.get(model));
        } else {
            models = new HashMap<>();
            refresh(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    handler.handleString(models.get(model));
                }
            });
        }
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        models = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Comparison_of_Google_Pixel_smartphones";
        final Document doc = getDocument(url);
        final StringBuilder builder = new StringBuilder("[");
        if(doc != null) {
            final String brand = getBackendID();
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            final List<Smartphone> smartphones = getPhonesFromTable(tables.get(0), url);
            boolean isFirst = true;
            for(Smartphone smartphone : smartphones) {
                final String model = smartphone.getDetail(SmartphoneDetailType.MODEL);
                final String backendID = smartphone.getDetail(SmartphoneDetailType.MODEL_BACKEND_ID);
                models.put(backendID, smartphone.toString());
                final PreSmartphone preSmartphone = new PreSmartphone(brand, backendID, model);
                builder.append(isFirst ? "" : ",").append(preSmartphone.toString());
                isFirst = false;
            }
        }
        builder.append("]");
        final String string = builder.toString();
        modelsList = string;
        WLLogger.logInfo("PhonesGoogle - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handleString(string);
    }
    private List<Smartphone> getPhonesFromTable(Element table, String url) {
        final String brand = getBackendID();
        final List<Smartphone> smartphones = new ArrayList<>();

        final Elements elements = table.select("tbody tr");
        final EventSource wikipedia = new EventSource("Wikipedia: Comparison of Google Pixel smartphones", url);
        final HashMap<Integer, Smartphone> builders = new HashMap<>();
        final HashMap<Integer, EventSources> sources = new HashMap<>();

        final Elements models = elements.get(0).select("th a");
        final int maxPhones = models.size();
        int index = 0;
        for(Element element : models) {
            final String model = element.text(), wiki = "https://en.wikipedia.org" + element.attr("href");
            final Smartphone builder = new Smartphone();
            builder.setDetail(SmartphoneDetailType.BRAND, brand);
            builder.setDetail(SmartphoneDetailType.MODEL_BACKEND_ID, model.toLowerCase().replace(" ", ""));
            builder.setDetail(SmartphoneDetailType.MODEL, model);
            builders.put(index, builder);
            sources.put(index, new EventSources(new EventSource("Wikipedia: " + model, wiki)));
            index++;
        }

        index = 0;
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.remove(0); // release date
        elements.remove(0); // discontinued date
        final Elements images = elements.get(0).select("td a img");
        for(Element element : images) {
            final Smartphone smartphone = builders.get(index);
            final String model = smartphone.getDetail(SmartphoneDetailType.MODEL_BACKEND_ID);
            if(!(model.equals("pixel3a") || model.equals("pixel3axl"))) {
                final String imageURL = "https:" + element.attr("src");
                smartphone.setDetail(SmartphoneDetailType.IMAGE_URL, imageURL);
                index++;
            } else {
                smartphone.setDetail(SmartphoneDetailType.IMAGE_URL, "https://cdn1.iconfinder.com/data/icons/computer-techologies-outline-part-2/128/ic_question_mark_phone-512.png");
            }
        }

        elements.remove(0);
        index = 0;
        final Elements initialReleaseOSs = elements.get(0).select("td");
        for(Element element : initialReleaseOSs) {
            final int span = element.hasAttr("colspan") ? Integer.parseInt(element.attr("colspan")) : -1;
            final String os = "Android " + element.text();
            if(span == -1) {
                builders.get(index).setDetail(SmartphoneDetailType.OPERATING_SYSTEM_INITIAL, os);
            } else {
                final int max = Math.min(span, maxPhones);
                for(int i = 0; i < max; i++) {
                    final int value = index+i;
                    builders.get(value).setDetail(SmartphoneDetailType.OPERATING_SYSTEM_INITIAL, os);
                }
            }
            index++;
        }

        elements.remove(0);
        index = 0;
        final Elements latestOSs = elements.get(0).select("td");
        latestOSs.remove(0);
        for(Element element : latestOSs) {
            final int span = element.hasAttr("colspan") ? Integer.parseInt(element.attr("colspan")) : -1;
            final String os = "Android " + element.text();
            final EventSource source = new EventSource("Wikipedia: " + os, "https://en.wikipedia.org" + element.select("a").get(0).attr("href"));
            if(span == -1) {
                builders.get(index).setDetail(SmartphoneDetailType.OPERATING_SYSTEM_LATEST, os);
                sources.get(index).add(source);
            } else {
                final int max = Math.min(span, maxPhones);
                for(int i = 0; i < max; i++) {
                    final int value = index+i;
                    builders.get(value).setDetail(SmartphoneDetailType.OPERATING_SYSTEM_LATEST, os);
                    sources.get(value).add(source);
                }
            }
            index++;
        }
        elements.remove(0);
        elements.remove(0); // last updated

        elements.remove(0); // cellular frequencies
        elements.remove(0); // data speeds

        final List<String> dimensions = getRowValues(elements.get(0).select("td")), weights = getRowValues(elements.get(1).select("td"));
        elements.remove(0); // dimensions
        elements.remove(0); // weights

        elements.remove(0); // cpu chipset
        elements.remove(0); // cpu processor
        elements.remove(0); // gpu
        elements.remove(0); // RAM
        elements.remove(0); // Storage

        elements.remove(0); // Memory card reader
        elements.remove(0); // networking
        elements.remove(0); // WLAN/BT
        elements.remove(0); // NFC
        elements.remove(0); // Power
        elements.remove(0); // Face buttons
        elements.remove(0); // Materials
        elements.remove(0); // Ports
        elements.remove(0); // Display


        elements.remove(0); // Rear camera sensor
        elements.remove(0); // Rear camera lens
        elements.remove(0); // Rear camera video
        elements.remove(0); // Rear camera features

        elements.remove(0); // Front camera sensor
        elements.remove(0); // Front camera lens
        elements.remove(0); // Front camera video

        for(Map.Entry<Integer, Smartphone> map : builders.entrySet()) {
            index = map.getKey();
            final EventSources eventSources = sources.get(index);
            eventSources.add(wikipedia);
            final Smartphone smartphone = map.getValue();
            smartphone.setDetail(SmartphoneDetailType.SOURCES, eventSources.toString());
            smartphones.add(smartphone);
        }
        return smartphones;
    }
}
