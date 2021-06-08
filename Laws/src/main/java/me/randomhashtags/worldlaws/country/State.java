package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.RomanNumeral;
import me.randomhashtags.worldlaws.SkipInterval;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public interface State extends Jsoupable {
    default String prefixZeros(String input, int amount) {
        final StringBuilder builder = new StringBuilder(input);
        for(int i = input.length(); i < amount; i++) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    default void iterateThroughChapterTable(Elements table, StringBuilder builder, boolean isIndex) {
        iterateThroughChapterTable(table, builder, isIndex, SkipInterval.NONE);
    }
    default void iterateThroughChapterTable(Elements table, StringBuilder builder, boolean isIndex, int skipInterval) {
        iterateThroughChapterTable(table, builder, isIndex, skipInterval, null);
    }
    default void iterateThroughChapterTable(Elements table, StringBuilder builder, boolean isIndex, SkipInterval skipInterval) {
        switch (skipInterval) {
            case ONLY_FIRST:
                table.remove(0);
                break;
            default:
                break;
        }
        iterateThroughChapterTable(table, builder, isIndex, skipInterval.getValue(), null);
    }
    default void iterateThroughChapterTable(Elements table, StringBuilder builder, boolean isIndex, int skipInterval, List<String> values) {
        StateLegal previousChapter = null;
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

        for(Element chapter : table) {
            String text = LocalServer.fixEscapeValues(chapter.text());
            if(index % 2 == 0) {
                final String[] spaces = text.split(" ");
                final String target = usesValues ? LocalServer.fixEscapeValues(values.get(index)) : spaces.length > 1 ? spaces[1] : text;
                final int fromRoman = RomanNumeral.fromRoman(target);
                text = fromRoman != 0 ? Integer.toString(fromRoman) : target;
                previousChapter = isIndex ? new StateIndex(text) : new StateChapter(text);
            } else {
                final String title = text.startsWith("- ") ? text.split("- ")[1] : text;
                previousChapter.setTitle(title);
                builder.append(isFirst ? "" : ",").append(previousChapter.toString());
                isFirst = false;
            }
            index++;
        }
    }
    default void iterateIndexTable(Elements table, StringBuilder builder, boolean isIndex) {
        iterateIndexTable(table, builder, isIndex, false);
    }
    default void iterateIndexTable(Elements table, StringBuilder builder, boolean isIndex, boolean skipFirst) {
        iterateIndexTable(table, builder, isIndex, skipFirst, null);
    }
    default void iterateIndexTable(Elements table, StringBuilder builder, boolean isIndex, boolean skipFirst, Elements titles) {
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
                final StateLegal legal = isIndex ? new StateIndex(backendID) : new StateChapter(backendID);
                legal.setTitle(LocalServer.fixEscapeValues(title));
                builder.append(isFirstElement ? "" : ",").append(legal.toString());
                isFirstElement = false;
            }
        }
    }

    String getIndexesURL();
    String getTableOfChaptersURL();
    String getStatutesListURL();
    String getStatuteURL();
    String getIndexesJSON();
    String getTableOfChaptersJSON();
    List<StateIndex> getIndexes();
    String getTableOfChapters(String title);
    String getStatuteList(String title, String chapter);
    String getStatute(String title, String chapter, String section);
}
