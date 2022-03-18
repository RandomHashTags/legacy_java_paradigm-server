package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface NewsService extends RestAPI {
    JSONObjectTranslatable getResponseJSON(String target);
}
