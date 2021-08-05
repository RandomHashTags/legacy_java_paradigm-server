package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.people.PoliticalParty;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum USPoliticians implements Jsonable {
    INSTANCE;

    private static final HashMap<String, String> CACHE = new HashMap<>();

    public void get(Element elements, String profileSlug, CompletionHandler handler) {
        if(CACHE.containsKey(profileSlug)) {
            handler.handleString(CACHE.get(profileSlug));
        } else {
            final CompletionHandler completion = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    CACHE.put(profileSlug, string);
                    handler.handleString(string);
                }
            };
            getFromBill(profileSlug, elements, completion);
        }
    }
    private void getFromBill(String profileSlug, Element element, CompletionHandler handler) {
        final String fileName = profileSlug.substring("members/".length()).replace("/", "-");
        getJSONObject(Folder.LAWS_USA_MEMBERS, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String text = element.text();
                final String[] describingValues = text.split(" \\[")[1].split("]")[0].split("-");
                final PoliticalParty party = PoliticalParty.fromAbbreviation(describingValues[0]);
                String governedTerritory = describingValues[1];
                final SovereignStateSubdivision subdivision = WLCountry.UNITED_STATES.valueOfSovereignStateSubdivision(governedTerritory);
                if(subdivision != null) {
                    governedTerritory = subdivision.getName();
                }
                final String district = describingValues.length > 2 ? describingValues[2] : null;

                final String congressURLPrefix = "https://www.congress.gov";
                final String url = congressURLPrefix + profileSlug;
                final Document doc = Jsoupable.getStaticDocument(Folder.LAWS_USA_MEMBERS, url, false);
                String imageURL = null, website = null;
                HumanName name = null;
                if(doc != null) {
                    final String tag = doc.select("h1.legDetail").get(0).text().split("\\(")[0].replace("Representative ", "").replace("Senator ", "");
                    final String[] values = tag.split(" ");
                    final int length = values.length;
                    final boolean hasMiddleName = length == 3, hasExtension = length == 4;
                    final String firstName = values[0];
                    final String middleName = hasMiddleName ? values[1].replace("\\.", "") : null;
                    final String lastName = hasMiddleName || hasExtension ? values[2] : values[1];
                    name = new HumanName(firstName, middleName, lastName);

                    final Elements images = doc.select("div.overview_wrapper div.overview div.overview-member-row div.overview-member-column-picture a[href] img");
                    imageURL = images.isEmpty() ? null : images.get(0).attr("src").substring("/img/member/".length());

                    final Elements trs = doc.select("div.overview-member-column-profile table.standard01").get(0).select("tbody tr td");
                    final Elements links = trs.select("a");
                    if(!links.isEmpty()) {
                        website = links.get(0).attr("href");
                    }
                }
                final USPolitician politician = new USPolitician(name, governedTerritory, district, party, imageURL, url.substring("https://www.congress.gov/member/".length()), website);
                final String string = politician.toServerJSON();
                handler.handleString(string);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                final USPolitician politician = new USPolitician(json);
                handler.handleString(politician.toJSON());
            }
        });
    }
}
