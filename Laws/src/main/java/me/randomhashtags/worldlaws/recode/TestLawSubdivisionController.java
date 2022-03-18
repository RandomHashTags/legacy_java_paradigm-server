package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
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
    public abstract TestStatute loadStatute(String index, String chapter, String section);

    @SuppressWarnings({"unchecked"})
    public HashSet<? extends TestStatuteIndex> loadIndexes() {
        final Document doc = getDocument(indexesURL);
        final HashSet<? extends TestStatuteAbstract> values = getStatutesAbstract(doc, null, null, SubdivisionLegislationType.INDEX);
        return (HashSet<? extends TestStatuteIndex>) values;
    }

    @SuppressWarnings({"unchecked"})
    public HashSet<? extends TestStatuteChapter> loadTableOfChapters(String index) {
        index = index.replace("_", "+").replace(" ", "+");
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", index));
        final HashSet<? extends TestStatuteAbstract> values = getStatutesAbstract(doc, index, null, SubdivisionLegislationType.CHAPTER);
        return (HashSet<? extends TestStatuteChapter>) values;
    }

    @SuppressWarnings({"unchecked"})
    public HashSet<? extends TestStatuteStatute> loadStatutesList(String index, String chapter) {
        final Document doc = getDocument(statutesListURL.replace("%index%", index).replace("%chapter%", chapter));
        final HashSet<? extends TestStatuteAbstract> values = getStatutesAbstract(doc, index, chapter, SubdivisionLegislationType.STATUTE);
        return (HashSet<? extends TestStatuteStatute>) values;
    }


    public abstract HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type);

    public JSONObjectTranslatable getIndexes() {
        return test("indexes", new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                final HashSet<? extends TestStatuteIndex> indexes = loadIndexes();
                return getString(indexes);
            }
        });
    }
    public JSONObjectTranslatable getTableOfChapters(String index) {
        final String fileName = index + File.separator + "chapters";
        return test(fileName, new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                final HashSet<? extends TestStatuteChapter> chapters = loadTableOfChapters(index);
                return getString(chapters);
            }
        });
    }
    public JSONObjectTranslatable getStatutesList(String index, String chapter) {
        final String fileName = getFileName(index + File.separator + chapter + File.separator + "statutes_list");
        return test(fileName, new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                final HashSet<? extends TestStatuteStatute> statutes = loadStatutesList(index, chapter);
                return getString(statutes);
            }
        });
    }
    public JSONObjectTranslatable getStatute(String index, String chapter, String section) {
        final String fileName = getFileName(index + File.separator + chapter + File.separator + "statutes" + File.separator + section);
        return test(fileName, new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                final TestStatute statute = loadStatute(index, chapter, section);
                return statute != null ? statute.toString() : null;
            }
        });
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
    private JSONObjectTranslatable test(String fileName, CompletionHandler loadHandler) {
        final SovereignStateSubdivision subdivision = getSubdivision();
        JSONObjectTranslatable json = null;
        if(subdivision != null) {
            final String countryBackendID = subdivision.getCountry().getBackendID(), backendID = subdivision.getBackendID(), year = Integer.toString(getPublishedDataYear());
            final Folder folder = Folder.LAWS_COUNTRY_SUBDIVISIONS_SUBDIVISION_YEAR;
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID).replace("%subdivision%", backendID).replace("%year%", year));
            final JSONObject localJSON = getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public String loadJSONObjectString() {
                    return loadHandler.loadJSONObjectString();
                }
            });
            if(localJSON != null) {
                json = new JSONObjectTranslatable();
                for(String key : localJSON.keySet()) {
                    json.put(key, localJSON.get(key));
                    json.addTranslatedKey(key);
                }
            }
        }
        return json;
    }
}
