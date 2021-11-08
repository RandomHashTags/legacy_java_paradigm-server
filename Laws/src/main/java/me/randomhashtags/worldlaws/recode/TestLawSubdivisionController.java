package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public abstract class TestLawSubdivisionController implements Jsoupable, Jsonable {
    protected final String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    protected final HashMap<String, String> statutes;

    public TestLawSubdivisionController(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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

    public abstract SovereignStateSubdivision getSubdivision();
    public abstract int getPublishedDataYear();
    public abstract void loadStatute(String index, String chapter, String section, CompletionHandlerLaws handler);

    @SuppressWarnings({"unchecked"})
    public void loadIndexes(CompletionHandlerLaws handler) {
        final Document doc = getDocument(indexesURL);
        final HashSet<? extends TestStatuteAbstract> values = getStatutesAbstract(doc, null, null, SubdivisionLegislationType.INDEX);
        final HashSet<? extends TestStatuteIndex> indexes = (HashSet<? extends TestStatuteIndex>) values;
        handler.handleIndexes(indexes);
    }

    @SuppressWarnings({"unchecked"})
    public void loadTableOfChapters(String index, CompletionHandlerLaws handler) {
        index = index.replace("_", "+").replace(" ", "+");
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", index));
        final HashSet<? extends TestStatuteAbstract> values = getStatutesAbstract(doc, index, null, SubdivisionLegislationType.CHAPTER);
        final HashSet<? extends TestStatuteChapter> chapters = (HashSet<? extends TestStatuteChapter>) values;
        handler.handleTableOfChapters(chapters);
    }

    @SuppressWarnings({"unchecked"})
    public void loadStatutesList(String index, String chapter, CompletionHandlerLaws handler) {
        final Document doc = getDocument(statutesListURL.replace("%index%", index).replace("%chapter%", chapter));
        final HashSet<? extends TestStatuteAbstract> values = getStatutesAbstract(doc, index, chapter, SubdivisionLegislationType.STATUTE);
        final HashSet<? extends TestStatuteStatute> indexes = (HashSet<? extends TestStatuteStatute>) values;
        handler.handleStatutes(indexes);
    }


    public abstract HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type);

    public void getIndexes(CompletionHandler handler) {
        test("indexes", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                loadIndexes(new CompletionHandlerLaws() {
                    @Override
                    public void handleIndexes(HashSet<? extends TestStatuteIndex> indexes) {
                        final String string = getString(indexes);
                        handler.handleString(string);
                    }
                });
            }
        }, handler);
    }
    public void getTableOfChapters(String index, CompletionHandler handler) {
        final String fileName = index + File.separator + "chapters";
        test(fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                loadTableOfChapters(index, new CompletionHandlerLaws() {
                    @Override
                    public void handleTableOfChapters(HashSet<? extends TestStatuteChapter> chapters) {
                        final String string = getString(chapters);
                        handler.handleString(string);
                    }
                });
            }
        }, handler);
    }
    public void getStatutesList(String index, String chapter, CompletionHandler handler) {
        final String fileName = getFileName(index + File.separator + chapter + File.separator + "statutes_list");
        test(fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                loadStatutesList(index, chapter, new CompletionHandlerLaws() {
                    @Override
                    public void handleStatutes(HashSet<? extends TestStatuteStatute> statutes) {
                        final String string = getString(statutes);
                        handler.handleString(string);
                    }
                });
            }
        }, handler);
    }
    public void getStatute(String index, String chapter, String section, CompletionHandler handler) {
        final String fileName = getFileName(index + File.separator + chapter + File.separator + "statutes" + File.separator + section);
        test(fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                loadStatute(index, chapter, section, new CompletionHandlerLaws() {
                    @Override
                    public void handleStatute(TestStatute statute) {
                        final String string = statute != null ? statute.toString() : null;
                        handler.handleString(string);
                    }
                });
            }
        }, handler);
    }
    private String getFileName(String input) {
        return input.replace(" ", "_").replace(",", "_").replace(";", "_").replace(".", "_");
    }


    private String getString(HashSet<? extends TestStatuteAbstract> set) {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(TestStatuteAbstract index : set) {
            builder.append(isFirst ? "" : ",").append(index.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
    private void test(String fileName, CompletionHandler loadHandler, CompletionHandler handler) {
        final SovereignStateSubdivision subdivision = getSubdivision();
        if(subdivision != null) {
            final String countryBackendID = subdivision.getCountry().getBackendID(), backendID = subdivision.getBackendID(), year = Integer.toString(getPublishedDataYear());
            final Folder folder = Folder.LAWS_COUNTRY_SUBDIVISIONS_SUBDIVISION_YEAR;
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID).replace("%subdivision%", backendID).replace("%year%", year));
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadHandler.load(handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    handler.handleString(json.toString());
                }
            });
        } else {
            handler.handleString(null);
        }
    }
}
