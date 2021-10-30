package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.news.NewsCBS;
import me.randomhashtags.worldlaws.stories.Story;
import org.apache.logging.log4j.Level;

import java.util.HashSet;

public final class Stories {

    public static void main(String[] args) {
        NewsCBS.INSTANCE.refresh(new CompletionHandler() {
            @Override
            public void handleStories(HashSet<Story> stories) {
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(Story story : stories) {
                    builder.append(isFirst ? "" : ",").append(story.toPreStory());
                    isFirst = false;
                }
                builder.append("}");
                WLLogger.log(Level.INFO, "Stories;test;builder=" + builder.toString());
            }
        });
    }
}
