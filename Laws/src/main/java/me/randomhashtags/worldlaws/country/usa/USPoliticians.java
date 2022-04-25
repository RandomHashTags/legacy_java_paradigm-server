package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.country.PoliticianController;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.people.PoliticalParty;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ConcurrentHashMap;

public enum USPoliticians implements PoliticianController {
    INSTANCE;

    private static final ConcurrentHashMap<String, JSONObjectTranslatable> CACHE = new ConcurrentHashMap<>();

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    public JSONObjectTranslatable get(Element elements, String profileSlug) {
        if(!CACHE.containsKey(profileSlug)) {
            final JSONObjectTranslatable string = getFromBill(profileSlug, elements);
            CACHE.put(profileSlug, string);
        }
        return CACHE.get(profileSlug);
    }
    private JSONObjectTranslatable getFromBill(String profileSlug, Element element) {
        final WLCountry country = getCountry();
        final String fileName = profileSlug.substring("members/".length()).replace("/", "-");
        final Folder folder = Folder.LAWS_COUNTRY_MEMBERS;
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", country.getBackendID()));
        final JSONObject json = getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                final String text = element.text();
                final String[] describingValues = text.split(" \\[")[1].split("]")[0].split("-");
                final PoliticalParty party = PoliticalParty.fromAbbreviation(describingValues[0]);
                String governedTerritory = describingValues[1];
                final SovereignStateSubdivision subdivision = country.valueOfSovereignStateSubdivision(governedTerritory);
                if(subdivision != null) {
                    governedTerritory = subdivision.getName();
                }
                final String district = describingValues.length > 2 ? describingValues[2] : null;
                final String congressURLPrefix = "https://www.congress.gov";
                final String url = congressURLPrefix + profileSlug;
                final Document doc = Jsoupable.getStaticDocument(Folder.LAWS_COUNTRY_MEMBERS, url, false);
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
                return politician.toJSONObject();
            }
        });

        final USPolitician politician = new USPolitician(json);
        return politician.toJSONObject();
    }
}
