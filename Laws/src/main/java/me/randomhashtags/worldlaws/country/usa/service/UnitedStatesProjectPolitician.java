package me.randomhashtags.worldlaws.country.usa.service;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.people.PoliticalParty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Month;

public final class UnitedStatesProjectPolitician {
    private final HumanName name;
    private final EventDate birthday;
    private final Terms terms;
    private final URLs urls;

    public UnitedStatesProjectPolitician(JSONObject json) {
        final JSONObject nameJSON = json.getJSONObject("name");
        final String firstName = nameJSON.getString("first");
        final String middleName = nameJSON.optString("middle", null);
        final String lastName = nameJSON.getString("last");
        name = new HumanName(firstName, middleName, lastName);

        final JSONObject bio = json.getJSONObject("bio");
        birthday = getEventDateFrom(bio.getString("birthday"));

        terms = new Terms(json.getJSONArray("terms"));
        urls = new URLs(json.getJSONObject("id"));
    }

    public HumanName getName() {
        return name;
    }
    public EventDate getBirthday() {
        return birthday;
    }
    public Terms getTerms() {
        return terms;
    }
    public URLs getURLs() {
        return urls;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("name", name.toJSONObject());
        json.put("birthday", birthday.toJSONObject());
        json.put("terms", terms);
        json.put("urls", urls);
        return json;
    }

    private EventDate getEventDateFrom(String input) {
        final String[] birthdayValues = input.split("-");
        return new EventDate(Month.of(Integer.parseInt(birthdayValues[1])), Integer.parseInt(birthdayValues[2]), Integer.parseInt(birthdayValues[0]));
    }

    private final class Terms extends JSONArrayTranslatable {
        private Terms(JSONArray array) {
            for(Object obj : array) {
                final JSONObject json = (JSONObject) obj;
                final Term term = new Term(json);
                put(term.toJSONObject());
            }
        }
    }
    private final class Term {
        private final String type, state, website, office;
        private final EventDate start, end;
        private final PoliticalParty party;

        private Term(JSONObject json) {
            type = json.getString("type");
            start = getEventDateFrom(json.getString("start"));
            end = getEventDateFrom(json.getString("end"));
            state = json.getString("state");
            party = PoliticalParty.valueOf(json.getString("party").toUpperCase());
            website = json.has("url") ? json.getString("url") : null;
            office = json.has("office") ? json.getString("office") : null;
        }

        public JSONObjectTranslatable toJSONObject() {
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            json.put("type", type);
            json.put("start", start.toJSONObject());
            json.put("end", end.toJSONObject());
            json.put("state", state);
            json.put("party", party.getName());
            json.put("website", website);
            json.put("office", office);
            return json;
        }
    }

    private final class URLs extends JSONObjectTranslatable {
        private URLs(JSONObject json) {
            final String wikipediaID = json.optString("wikipedia", null);
            if(wikipediaID != null) {
                put("wikipediaURL", "https://en.wikipedia.org/wiki/" + wikipediaID);
            }
        }
    }
}
