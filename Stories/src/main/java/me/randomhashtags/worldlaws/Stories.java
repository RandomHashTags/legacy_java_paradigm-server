package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.news.NewsCBS;

import java.util.HashSet;

public final class Stories {

    public static void main(String[] args) {
        final HashSet<Story> stories = NewsCBS.INSTANCE.refresh();

        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Story story : stories) {
            builder.append(isFirst ? "" : ",").append(story.toPreStory());
            isFirst = false;
        }
        builder.append("}");
        WLLogger.logInfo("Stories;test;builder=" + builder.toString());
    }
}
