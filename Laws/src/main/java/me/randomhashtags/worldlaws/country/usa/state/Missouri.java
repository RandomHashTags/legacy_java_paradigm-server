package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteChapter;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Missouri extends LawSubdivisionController {
    public static final Missouri INSTANCE = new Missouri(
            "http://revisor.mo.gov/main/home.aspx",
            "http://revisor.mo.gov/main/home.aspx",
            "https://revisor.mo.gov/main/OneChapter.aspx?chapter=%chapter%",
            "https://revisor.mo.gov/main/OneSection.aspx?section=%chapter%.%section%"
    );

    Missouri(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            setupTableOfChapters(doc);
            final Elements table = doc.select("div.lr-fl-s-all summary span + span");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = "Title " + values[0], value = values[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    private void setupTableOfChapters(Document doc) {
        final Elements div = doc.select("div.lr-fl-s-all"), chapterList = div.select("summary span"), chapterTitles = div.select("details div a[href]");
        int index = 0, chapterIndex = 0;
        TABLE_OF_CHAPTERS_JSON.putIfAbsent(this, new HashMap<>());
        for(Element element : chapterList) {
            if(index % 2 == 0) {
                final String text = element.text();
                final String[] range = text.split(" ")[1].split(" ")[1].split("‑");
                final int min = Integer.parseInt(range[0]), max = Integer.parseInt(range[1]);
                final JSONObjectTranslatable json = new JSONObjectTranslatable();
                for(int i = min; i <= max; i++) {
                    final String chapterText = chapterTitles.get(chapterIndex).text();
                    final String[] values = chapterText.split(" ");
                    final int targetChapter = Integer.parseInt(values[2]);
                    if(i == targetChapter) {
                        final String title = values[3], chapter = Integer.toString(i);
                        final SubdivisionStatuteChapter stateChapter = new SubdivisionStatuteChapter(chapter);
                        stateChapter.setTitle(title);
                        final String id = stateChapter.getID();
                        json.put(id, stateChapter.toJSONObject(), true);
                        chapterIndex++;
                    }
                }
                TABLE_OF_CHAPTERS_JSON.get(this).put(Integer.toString(index+1), json);
            }
            index++;
        }
    }
    @Override
    public void loadTableOfChapters(String title) {
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
        if(doc != null) {
            final int substring = 1+chapter.length();
            final Elements elements = doc.select("div.lr-fl-s-all table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : elements) {
                final String text = element.text();
                if(text.startsWith(chapter + ".")) {
                    final String separator = "   ";
                    final String[] values = text.split(separator);
                    final String statute = values[0], value = text.split(statute + separator)[1], statuteValue = statute.substring(substring);
                    final String correctedStatute = statuteValue.substring(statuteValue.startsWith("00") ? 2 : statuteValue.startsWith("0") ? 1 : 0);
                    list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                    list.add(new Element(value).appendChild(new TextNode(value)));
                }
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public SubdivisionStatute loadStatute(String title, String chapter, String section) {
        section = prefixZeros(section, 3);
        final String url = statuteURL.replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements div = doc.select("div.norm p.norm");
            final String topic = div.select("span.bold").get(0).text();
            final StringBuilder description = new StringBuilder();
            boolean isFirst = true;
            for(Element element : div) {
                description.append(isFirst ? "" : "\n").append(element.text());
                isFirst = false;
            }
            return new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
        }
        return null;
    }
}
