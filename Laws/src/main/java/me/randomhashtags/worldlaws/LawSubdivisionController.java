package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SubdivisionLegal;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteChapter;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.recode.SubdivisionLegislationType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class LawSubdivisionController implements Jsoupable {
    protected static final HashMap<LawSubdivisionController, JSONObjectTranslatable> INDEX_BUILDERS = new HashMap<>();
    protected static final HashMap<LawSubdivisionController, HashMap<String, JSONObjectTranslatable>> TABLE_OF_CHAPTERS_JSON = new HashMap<>();
    protected static final HashMap<LawSubdivisionController, HashMap<String, JSONObjectTranslatable>> STATUTES_JSON = new HashMap<>();

    protected final String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    protected final HashMap<String, JSONObjectTranslatable> statutes;

    public LawSubdivisionController(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;
        statutes = new HashMap<>();
    }

    public String prefixZeros(String input, int amount) {
        final StringBuilder builder = new StringBuilder(input);
        for(int i = input.length(); i < amount; i++) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    public void iterateThroughIndexTable(Elements table) {
        iterateThroughTable(null, table, SubdivisionLegislationType.INDEX, 0, null);
    }
    public void iterateThroughIndexTable(Elements table, int skipInterval) {
        iterateThroughTable(null, table, SubdivisionLegislationType.INDEX, skipInterval, null);
    }
    public void iterateThroughChapterTable(String title, Elements table) {
        iterateThroughTable(title, table, SubdivisionLegislationType.CHAPTER, 0, null);
    }
    public void iterateThroughStatuteTable(String path, Elements table) {
        iterateThroughTable(path, table, SubdivisionLegislationType.STATUTE, 0, null);
    }
    private void iterateThroughTable(String targetTitle, Elements table, SubdivisionLegislationType type, int skipInterval, List<String> values) {
        SubdivisionLegal previousChapter = null;
        int index = 1;
        final boolean usesValues = values != null;

        if(skipInterval > 0) {
            final List<Element> list = new ArrayList<>();
            for(Element e : table) {
                if(index % skipInterval != 0) {
                    list.add(e);
                }
                index++;
            }
            table = new Elements(list);
        }
        index = 0;

        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        final boolean isIndex = type == SubdivisionLegislationType.INDEX;
        for(Element chapter : table) {
            String text = chapter.text();
            if(index % 2 == 0) {
                final String[] spaces = text.split(" ");
                final String target = usesValues ? values.get(index) : spaces.length > 1 ? spaces[1] : text;
                final int fromRoman = RomanNumeral.fromRoman(target);
                text = fromRoman != 0 ? Integer.toString(fromRoman) : target;
                previousChapter = isIndex ? new SubdivisionStatuteIndex(text) : new SubdivisionStatuteChapter(text);
            } else {
                final String title = text.startsWith("- ") ? text.split("- ")[1] : text;
                previousChapter.setTitle(title);
                json.put(previousChapter.getID(), previousChapter.toJSONObject());
            }
            index++;
        }
        switch (type) {
            case INDEX:
                INDEX_BUILDERS.put(this, json);
                break;
            case CHAPTER:
                TABLE_OF_CHAPTERS_JSON.putIfAbsent(this, new HashMap<>());
                TABLE_OF_CHAPTERS_JSON.get(this).put(targetTitle, json);
                break;
            case STATUTE:
                STATUTES_JSON.putIfAbsent(this, new HashMap<>());
                STATUTES_JSON.get(this).put(targetTitle, json);
                break;
            default:
                break;
        }
    }
    public void iterateIndexTable(Elements table, boolean isIndex) {
        iterateIndexTable(table, isIndex, false);
    }
    public void iterateIndexTable(Elements table, boolean isIndex, boolean skipFirst) {
        iterateIndexTable(table, isIndex, skipFirst, null);
    }
    public void iterateIndexTable(Elements table, boolean isIndex, boolean skipFirst, Elements titles) {
        final JSONObjectTranslatable builder = getStringBuilder(table, isIndex, skipFirst, titles);
        INDEX_BUILDERS.put(this, builder);
    }
    public void iterateChapterTable(String chapterTitle, Elements table, boolean skipFirst, Elements titles) {
        final JSONObjectTranslatable builder = getStringBuilder(table, false, skipFirst, titles);
        TABLE_OF_CHAPTERS_JSON.putIfAbsent(this, new HashMap<>());
        TABLE_OF_CHAPTERS_JSON.get(this).put(chapterTitle, builder);
    }
    private JSONObjectTranslatable getStringBuilder(Elements table, boolean isIndex, boolean skipFirst, Elements titles) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        final boolean isCustomTitle = titles != null;
        boolean isFirst = true;
        int index = 0, maxRows = 0;
        String previousCustomTitle = null;
        for(Element chapter : table) {
            if(skipFirst && isFirst) {
                isFirst = false;
            } else {
                final String text = chapter.text();
                final boolean containsHyphen = text.contains(" - ");
                final String[] values = containsHyphen ? text.split(" - ") : text.split(" ");
                final String backendID, title;
                if(isCustomTitle) {
                    backendID = values[0];

                    final Element customTitle = titles.get(index);
                    title = customTitle.text();
                    final String rowspan = customTitle.attr("rowspan");
                    if(rowspan != null && !rowspan.isEmpty()) {
                        final int rows = Integer.parseInt(rowspan);
                        if(previousCustomTitle != null && previousCustomTitle.equals(title)) {
                            maxRows -= 1;
                            if(maxRows != 0) {
                                index -= 1;
                            }
                            previousCustomTitle = maxRows == 0 ? null : previousCustomTitle;
                        } else {
                            if(rows != 1) {
                                maxRows = rows-1;
                                index -= 1;
                                previousCustomTitle = title;
                            } else {
                                previousCustomTitle = null;
                            }
                        }
                    }
                    index++;
                } else if(containsHyphen) {
                    backendID = values[0];
                    title = values[1];
                } else {
                    backendID = values[1];
                    title = text.split(backendID)[1];
                }
                final SubdivisionLegal legal = isIndex ? new SubdivisionStatuteIndex(backendID) : new SubdivisionStatuteChapter(backendID);
                legal.setTitle(title);
                final String id = legal.getID();
                json.put(id, legal.toJSONObject());
                json.addTranslatedKey(id);
            }
        }
        return json;
    }

    public final String getIndexesURL() {
        return indexesURL;
    }
    public final String getTableOfChaptersURL() {
        return tableOfChaptersURL;
    }
    public final String getStatutesListURL() {
        return statutesListURL;
    }
    public final String getStatuteURL() {
        return statuteURL;
    }

    public final JSONObjectTranslatable getIndexesJSON() {
        if(!INDEX_BUILDERS.containsKey(this)) {
            final List<SubdivisionStatuteIndex> indexes = getIndexes();
        }
        return INDEX_BUILDERS.getOrDefault(this, null);
    }
    public abstract List<SubdivisionStatuteIndex> getIndexes();
    public final JSONObjectTranslatable getTableOfChapters(String title) {
        if(!TABLE_OF_CHAPTERS_JSON.containsKey(this) || !TABLE_OF_CHAPTERS_JSON.get(this).containsKey(title)) {
            loadTableOfChapters(title);
        }
        return TABLE_OF_CHAPTERS_JSON.get(this).get(title);
    }
    public abstract void loadTableOfChapters(String title);
    public final JSONObjectTranslatable getStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        if(!STATUTES_JSON.containsKey(this) || !STATUTES_JSON.get(this).containsKey(path)) {
            loadStatuteList(title, chapter);
        }
        final HashMap<String, JSONObjectTranslatable> map = STATUTES_JSON.get(this);
        return map.getOrDefault(path, map.getOrDefault(chapter, map.getOrDefault(title, null)));
    }
    public abstract void loadStatuteList(String title, String chapter);
    public abstract SubdivisionStatute loadStatute(String title, String chapter, String section);
    public final JSONObjectTranslatable getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        if(!statutes.containsKey(path)) {
            final SubdivisionStatute statute = loadStatute(title, chapter, section);
            if(statute != null) {
                statutes.put(path, statute.toJSONObject());
            }
        }
        return statutes.get(path);
    }
}
