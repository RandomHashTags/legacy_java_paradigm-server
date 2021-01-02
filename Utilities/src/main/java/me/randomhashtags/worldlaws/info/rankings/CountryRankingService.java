package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;

import java.util.Collection;

public interface CountryRankingService extends CountryService {
    CountryInfo getInfo();
    void getRankedJSON(CompletionHandler handler);
    void getValue(String countryBackendID, CompletionHandler handler);

    default String toRankedJSON(Collection<CountryRankingInfoValue> values) {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CountryRankingInfoValue value : values) {
            builder.append(isFirst ? "" : ",").append("\"").append(value.country.replace(" ", "")).append("\":").append(value.worldRank);
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
}
