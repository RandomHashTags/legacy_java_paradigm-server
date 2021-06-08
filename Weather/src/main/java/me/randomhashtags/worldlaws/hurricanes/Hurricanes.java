package me.randomhashtags.worldlaws.hurricanes;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;

import java.util.HashMap;

public enum Hurricanes implements Jsoupable {
    INSTANCE;

    private HashMap<Integer, String> atlanticSeasonsJSON;

    Hurricanes() {
        atlanticSeasonsJSON = new HashMap<>();
    }

    public void getAtlanticSeason(int year, CompletionHandler handler) {
        if(atlanticSeasonsJSON.containsKey(year)) {
            handler.handle(atlanticSeasonsJSON.get(year));
        } else {
            refreshAtlanticSeason(year, handler);
        }
    }
    public void refreshAtlanticSeason(int year, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = "https://en.wikipedia.org/wiki/" + year + "_Atlantic_hurricane_season";
        /*final Document doc = getDocument(url);
        if(doc != null) {
            final StringBuilder builder = new StringBuilder("[");
            final Elements elements = doc.getAllElements(), hurricanes = doc.select("h3");
            final Elements cleanedElements = new Elements(elements);
            cleanedElements.removeIf(element -> {
                final boolean isDiv = element.tagName().equals("div") && !element.attr("role").isEmpty();
                if(isDiv) {
                    element.remove();
                }
                return false;
            });
            final Elements infoboxes = cleanedElements.select("h3 + table.infobox");
            hurricanes.removeIf(h -> {
                final String text = h.text().toLowerCase();
                return !(text.contains("tropical") || text.contains("storm") || text.contains("hurricane"));
            });
            final List<Integer> hurricaneIndexes = new ArrayList<>();
            final HashMap<Integer, String> hurricaneInfobox = new HashMap<>();
            int targetIndex = 0;
            for(Element hurricane : hurricanes) {
                final int index = elements.indexOf(hurricane);
                hurricaneIndexes.add(index);

                final Element infobox = infoboxes.get(targetIndex);
                final Elements values = infobox.select("tbody tr td");
                String value = "";
                boolean isFirst = true;
                for(Element element : values) {
                    final String text = element.text();
                    value = value.concat(isFirst ? "" : "|").concat(text);
                    isFirst = false;
                }
                hurricaneInfobox.put(targetIndex, value);
                targetIndex += 1;
            }

            final Elements paragraphs = elements.select("h3 ~ p");
            for(int i = 1; i <= 7; i++) {
                paragraphs.remove(0);
            }

            final HashMap<Integer, String> hurricaneParagraphs = new HashMap<>();
            for(Element element : paragraphs) {
                final int index = elements.indexOf(element);
                final String text = element.text();
                int hurricaneParagraph = 0;
                inner: for(Integer value : hurricaneIndexes) {
                    if(index > value) {
                        hurricaneParagraph += 1;
                    } else {
                        final String previous = hurricaneParagraphs.getOrDefault(hurricaneParagraph, "");
                        final String string = previous.concat((previous.isEmpty() ? "" : "\\n") + text);
                        hurricaneParagraphs.put(hurricaneParagraph, string);
                        break inner;
                    }
                }
            }
            boolean isFirst = true;
            targetIndex = 0;
            for(Map.Entry<Integer, String> map : hurricaneParagraphs.entrySet()) {
                final Integer key = map.getKey();
                final String name = hurricanes.get(key).text().split("\\[")[0], description = map.getValue();
                final String[] values = hurricaneInfobox.get(targetIndex).split("\\|");
                final String peak = values[3];
                final String[] duration = values[2].split(" – ");
                if(!duration[0].toLowerCase().startsWith("current") && !duration[1].toLowerCase().contains("present")) {
                    final String[] startValues = duration[0].split(" "), endValues = duration[1].split(" ");
                    final Month startMonth = Month.valueOf(startValues[0].toUpperCase()), endMonth = Month.valueOf(endValues[0].toUpperCase());
                    final int startDay = Integer.parseInt(startValues[1]), endDay = Integer.parseInt(endValues[1]);
                    final EventDate startDate = new EventDate(startMonth, startDay, year), endDate = new EventDate(endMonth, endDay, year);
                    final Hurricane hurricane = new Hurricane(name, description, peak, startDate, endDate);
                    builder.append(isFirst ? "" : ",").append(hurricane.toString());
                    isFirst = false;
                    targetIndex += 1;
                }
            }

            builder.append("]");
            final String string = builder.toString();
            atlanticSeasonsJSON.put(year, string);
            WLLogger.log(Level.INFO, "Hurricanes - refreshed " + year + " atlantic hurricanes (took " + (System.currentTimeMillis()-started) + "ms)");
            if(handler != null) {
                handler.handle(string);
            }
        }*/
    }
}
