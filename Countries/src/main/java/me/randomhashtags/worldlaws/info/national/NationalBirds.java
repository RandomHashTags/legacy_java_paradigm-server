package me.randomhashtags.worldlaws.info.national;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.info.CountryNationalService;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.jsoup.select.Elements;

public enum NationalBirds implements CountryNationalService {
    INSTANCE;

    @Override
    public EventSources getSources() {
        final String url = "https://en.wikipedia.org/wiki/List_of_national_birds";
        final EventSource source = new EventSource("Wikipedia: List of national birds", url);
        return new EventSources(source);
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.NATIONAL_BIRDS;
    }

    @Override
    public JSONObjectTranslatable loadData() {
        final Elements elements = getDocumentElements(Folder.COUNTRIES_NATIONAL, "https://en.wikipedia.org/wiki/List_of_national_birds", "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        return getNationalArrayJSON(elements, "National Bird");
    }
}
