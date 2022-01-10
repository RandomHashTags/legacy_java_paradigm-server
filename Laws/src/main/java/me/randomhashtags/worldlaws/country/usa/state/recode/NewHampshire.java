package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.CompletionHandlerLaws;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.RomanNumeral;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class NewHampshire extends TestLawSubdivisionController {
    public static final NewHampshire INSTANCE = new NewHampshire(
            "http://www.gencourt.state.nh.us/rsa/html/nhtoc.htm",
            "http://www.gencourt.state.nh.us/rsa/html/NHTOC/NHTOC-%index%.htm",
            "http://www.gencourt.state.nh.us/rsa/html/NHTOC/NHTOC-%index%-%chapter%.htm",
            "http://www.gencourt.state.nh.us/rsa/html/%index%/%chapter%/%chapter%-%section%.htm"
    );

    NewHampshire(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.NEW_HAMPSHIRE;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public void loadTableOfChapters(String index, CompletionHandlerLaws handler) {
        index = convertIndexToRoman(index);
        super.loadTableOfChapters(index, handler);
    }

    @Override
    public void loadStatutesList(String index, String chapter, CompletionHandlerLaws handler) {
        index = convertIndexToRoman(index);
        super.loadStatutesList(index, chapter, handler);
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = "ul li a[href]";
            final Elements table = doc.select(tableID);
            if(!table.isEmpty()) {
                final String key = isIndex ? "Title " : isChapter ? "Title " : "Section: ";
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    String text = row.text();
                    if(isStatute) {
                        text = text.substring(key.length());
                    }
                    final String[] textValues = text.split(":");
                    final String id;
                    if(isIndex) {
                        final String targetID = textValues[0].substring(key.length());
                        final String[] targetIDValues = targetID.split("-");
                        id = RomanNumeral.fromRoman(targetIDValues[0]) + (targetIDValues.length == 1 ? "" : "-" + targetIDValues[1]);
                    } else if(isChapter) {
                        id = textValues[0].substring(key.length());
                    } else {
                        final String[] sectionValues = textValues[1].split(" ");
                        id = sectionValues[0];
                    }
                    final String title = isIndex || isChapter ? text.substring(textValues[0].length()+2) : text.substring(chapter.length()+id.length()+2);
                    final TestStatuteAbstract statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : isChapter ? new TestStatuteChapter(id, title) : isStatute ? new TestStatuteStatute(id, title) : null;
                    if(statuteAbstract != null) {
                        values.add(statuteAbstract);
                    }
                }
                return values;
            }
        }
        return null;
    }

    private String convertIndexToRoman(String index) {
        final String[] indexValues = index.split("-");
        return RomanNumeral.toRoman(Integer.parseInt(indexValues[0])) + (indexValues.length == 1 ? "" : "-" + indexValues[1]);
    }

    @Override
    public void loadStatute(String index, String chapter, String section, CompletionHandlerLaws handler) {
        index = convertIndexToRoman(index);
        final String url = statuteURL.replace("%index%", index).replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        if(doc != null) {
            final String titleKey = chapter + ":" + section + " ";
            final Elements titles = doc.select("body b"), titleDescriptions = doc.select("body codesect");
            titles.remove(titles.size()-1);
            final TestStatute statute;
            final EventSources sources = new EventSources();
            sources.add(new EventSource("New Hampshire Legislature: Statute Page", url));
            if(titles.size() == 1) {
                String title = titles.get(0).text().substring(titleKey.length());
                if(title.endsWith(" –")) {
                    title = title.substring(0, title.length()-2);
                }
                statute = new TestStatute(title, titleDescriptions.get(0).text(), null, sources);
            } else {
                final List<TestSubdivision> subdivisions = new ArrayList<>();
                statute = new TestStatute("Unknown Title", "Unknown Description", subdivisions, sources);
            }
            handler.handleStatute(statute);
            return;
        }
        handler.handleStatute(null);
    }
}
