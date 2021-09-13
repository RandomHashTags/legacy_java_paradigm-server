package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SubdivisionLegal;
import me.randomhashtags.worldlaws.country.SubdivisionLegislationType;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteChapter;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface LawSubdivisionController extends Jsoupable {
    HashMap<LawSubdivisionController, StringBuilder> INDEX_BUILDERS = new HashMap<>();
    HashMap<LawSubdivisionController, HashMap<String, String>> TABLE_OF_CHAPTERS_JSON = new HashMap<>();
    HashMap<LawSubdivisionController, HashMap<String, String>> STATUTES_JSON = new HashMap<>();

    default String prefixZeros(String input, int amount) {
        final StringBuilder builder = new StringBuilder(input);
        for(int i = input.length(); i < amount; i++) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    default void iterateThroughIndexTable(Elements table) {
        iterateThroughTable(null, table, SubdivisionLegislationType.INDEX, 0, null);
    }
    default void iterateThroughIndexTable(Elements table, int skipInterval) {
        iterateThroughTable(null, table, SubdivisionLegislationType.INDEX, skipInterval, null);
    }
    default void iterateThroughChapterTable(String title, Elements table) {
        iterateThroughTable(title, table, SubdivisionLegislationType.CHAPTER, 0, null);
    }
    default void iterateThroughStatuteTable(String path, Elements table) {
        iterateThroughTable(path, table, SubdivisionLegislationType.STATUTE, 0, null);
    }
    private void iterateThroughTable(String targetTitle, Elements table, SubdivisionLegislationType type, int skipInterval, List<String> values) {
        final StringBuilder builder = new StringBuilder("{");
        SubdivisionLegal previousChapter = null;
        int index = 1;
        boolean isFirst = true;
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

        final boolean isIndex = type == SubdivisionLegislationType.INDEX;
        for(Element chapter : table) {
            String text = LocalServer.fixEscapeValues(chapter.text());
            if(index % 2 == 0) {
                final String[] spaces = text.split(" ");
                final String target = usesValues ? LocalServer.fixEscapeValues(values.get(index)) : spaces.length > 1 ? spaces[1] : text;
                final int fromRoman = RomanNumeral.fromRoman(target);
                text = fromRoman != 0 ? Integer.toString(fromRoman) : target;
                previousChapter = isIndex ? new SubdivisionStatuteIndex(text) : new SubdivisionStatuteChapter(text);
            } else {
                final String title = text.startsWith("- ") ? text.split("- ")[1] : text;
                previousChapter.setTitle(title);
                builder.append(isFirst ? "" : ",").append(previousChapter.toString());
                isFirst = false;
            }
            index++;
        }
        builder.append("}");
        switch (type) {
            case INDEX:
                INDEX_BUILDERS.put(this, builder);
                break;
            case CHAPTER:
                TABLE_OF_CHAPTERS_JSON.putIfAbsent(this, new HashMap<>());
                TABLE_OF_CHAPTERS_JSON.get(this).put(targetTitle, builder.toString());
                break;
            case STATUTE:
                STATUTES_JSON.putIfAbsent(this, new HashMap<>());
                STATUTES_JSON.get(this).put(targetTitle, builder.toString());
                break;
            default:
                break;
        }
    }
    default void iterateIndexTable(Elements table, boolean isIndex) {
        iterateIndexTable(table, isIndex, false);
    }
    default void iterateIndexTable(Elements table, boolean isIndex, boolean skipFirst) {
        iterateIndexTable(table, isIndex, skipFirst, null);
    }
    default void iterateIndexTable(Elements table, boolean isIndex, boolean skipFirst, Elements titles) {
        final StringBuilder builder = getStringBuilder(table, isIndex, skipFirst, titles);
        INDEX_BUILDERS.put(this, builder);
    }
    default void iterateChapterTable(String chapterTitle, Elements table, boolean skipFirst, Elements titles) {
        final StringBuilder builder = getStringBuilder(table, false, skipFirst, titles);
        TABLE_OF_CHAPTERS_JSON.putIfAbsent(this, new HashMap<>());
        TABLE_OF_CHAPTERS_JSON.get(this).put(chapterTitle, builder.toString());
    }
    private StringBuilder getStringBuilder(Elements table, boolean isIndex, boolean skipFirst, Elements titles) {
        final StringBuilder builder = new StringBuilder("{");
        final boolean isCustomTitle = titles != null;
        boolean isFirst = true, isFirstElement = true;
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
                legal.setTitle(LocalServer.fixEscapeValues(title));
                builder.append(isFirstElement ? "" : ",").append(legal.toString());
                isFirstElement = false;
            }
        }
        builder.append("}");
        return builder;
    }

    String getIndexesURL();
    String getTableOfChaptersURL();
    String getStatutesListURL();
    String getStatuteURL();
    default String getIndexesJSON() {
        if(!INDEX_BUILDERS.containsKey(this)) {
            getIndexes();
        }
        return INDEX_BUILDERS.containsKey(this) ? INDEX_BUILDERS.get(this).toString() : null;
    }
    List<SubdivisionStatuteIndex> getIndexes();
    default String getTableOfChapters(String title) {
        if(!TABLE_OF_CHAPTERS_JSON.containsKey(this) || !TABLE_OF_CHAPTERS_JSON.get(this).containsKey(title)) {
            loadTableOfChapters(title);
        }
        return TABLE_OF_CHAPTERS_JSON.get(this).get(title);
    }
    void loadTableOfChapters(String title);
    default String getStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        if(!STATUTES_JSON.containsKey(this) || !STATUTES_JSON.get(this).containsKey(path)) {
            loadStatuteList(title, chapter);
        }
        final HashMap<String, String> map = STATUTES_JSON.get(this);
        return map.getOrDefault(path, map.getOrDefault(chapter, map.getOrDefault(title, null)));
    }
    void loadStatuteList(String title, String chapter);
    String getStatute(String title, String chapter, String section);
}
