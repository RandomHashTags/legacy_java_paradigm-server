package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

import java.util.Collection;

public interface CountryRankingService extends CountryService {
    default Elements getRankingDocumentElements(String url, String targetElements) {
        return getRankingDocumentElements(url, targetElements, -1);
    }
    default Elements getRankingDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.RANKINGS, url, targetElements, index);
    }

    String getRankedJSON();

    default void getRankedJSON(CompletionHandler handler) {
        final String rankedJSON = getRankedJSON();
        if(rankedJSON != null) {
            handler.handle(rankedJSON);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    handler.handle(getRankedJSON());
                }
            });
        }
    }

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
