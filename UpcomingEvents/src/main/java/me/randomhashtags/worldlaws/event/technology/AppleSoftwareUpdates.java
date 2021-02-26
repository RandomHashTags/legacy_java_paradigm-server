package me.randomhashtags.worldlaws.event.technology;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.WLLogger;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public enum AppleSoftwareUpdates implements Jsoupable {
    INSTANCE;

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = "https://support.apple.com/en-us/HT201222";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("div div section.section div div.column div.main div div div div div");
        }
        WLLogger.log(Level.INFO, "AppleSoftwareUpdates - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
    }
}
