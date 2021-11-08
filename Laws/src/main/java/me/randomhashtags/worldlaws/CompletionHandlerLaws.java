package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.recode.TestStatute;
import me.randomhashtags.worldlaws.recode.TestStatuteChapter;
import me.randomhashtags.worldlaws.recode.TestStatuteIndex;
import me.randomhashtags.worldlaws.recode.TestStatuteStatute;

import java.util.HashSet;

public interface CompletionHandlerLaws {
    default void handleIndexes(HashSet<? extends TestStatuteIndex> indexes) { }
    default void handleTableOfChapters(HashSet<? extends TestStatuteChapter> chapters) { }
    default void handleStatutes(HashSet<? extends TestStatuteStatute> statutes) { }
    default void handleStatute(TestStatute statute) { }
}
