package me.randomhashtags.worldlaws.generators;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;

public enum LegatumProsperityIndexGenerator implements Jsonable, Jsoupable {
    INSTANCE;

    public String get() {
        final String html = getLocalFileString(Folder.OTHER, "prosperityIndex", "html");
        if(html == null) {
            return null;
        }
        final Document doc = Jsoup.parse(html);
        final Elements tables = doc.select("div.x-tree-view div.x-grid-item-container table.x-grid-item");
        tables.remove(tables.last());

        final LocalDate now = LocalDate.now();
        final int year = now.getYear();
        final String accessDate = now.getDayOfMonth() + " " + now.getMonth().name() + " " + year;
        final StringBuilder builder = new StringBuilder();
        final String prefix = "=== " + year + "<ref>{{cite web|title=The Legatum Prosperity Index™|url=https://www.prosperity.com/rankings|publisher=Legatum Institute Foundation|access-date=" + accessDate + "}}</ref> === \n" +
                "{|class= \"wikitable sortable mw-collapsible\" style=\"border: 1px solid #CCC; padding: 2px; margin: 1px; text-align:center;\"\n" +
                "!Country\n" +
                "!Rank\n" +
                "!Average Score\n" +
                "!Safety & Security\n" +
                "!Personal Freedom\n" +
                "!Governance\n" +
                "!Social Capital\n" +
                "!Investment Environment\n" +
                "!Enterprise Conditions\n" +
                "!Market Access & Infrastructure\n" +
                "!Economic Quality\n" +
                "!Living Conditions\n" +
                "!Health\n" +
                "!Education\n" +
                "!Natural Environment\n" +
                "|-\n";
        builder.append(prefix);
        int rank = 1;
        for(Element table : tables) {
            final Elements tds = table.selectFirst("tbody tr").select("td");
            final String country = tds.get(2).text();
            final int[] valueTDs = { 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
            final String header = "|style=\"text-align:left;\" | {{Flag|" + country + "}}\n";
            String valueString = "|'''" + rank + "'''";
            for(int valueTD : valueTDs) {
                final String value = "\n|" + tds.get(valueTD).text();
                valueString = valueString.concat(value);
            }
            builder.append(header);
            builder.append(valueString);
            builder.append("\n|-\n");
            rank += 1;
        }
        builder.append("|}");
        return builder.toString();
    }
}
