package me.randomhashtags.worldlaws;

import java.util.HashSet;

public interface StoryController extends Jsoupable {
    HashSet<Story> refresh();
}
