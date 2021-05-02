package me.randomhashtags.worldlaws.country.usa.service.usaproject;

import me.randomhashtags.worldlaws.EventDate;
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
        final String middleName = nameJSON.has("middle") ? nameJSON.getString("middle") : null;
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

    @Override
    public String toString() {
        return "{" +
                "\"name\":" + name.toString() + "," +
                "\"birthday\":" + birthday.toString() + "," +
                "\"terms\":" + terms.toString() + "," +
                "\"urls\":" + urls.toString() +
                "}";
    }

    private EventDate getEventDateFrom(String input) {
        final String[] birthdayValues = input.split("-");
        return new EventDate(Month.of(Integer.parseInt(birthdayValues[1])), Integer.parseInt(birthdayValues[2]), Integer.parseInt(birthdayValues[0]));
    }

    private final class Terms {
        private final String terms;

        private Terms(JSONArray array) {
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(Object obj : array) {
                final JSONObject json = (JSONObject) obj;
                final Term term = new Term(json);
                builder.append(isFirst ? "" : ",").append(term.toString());
                isFirst = false;
            }
            builder.append("]");
            terms = builder.toString();
        }

        @Override
        public String toString() {
            return terms;
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

        @Override
        public String toString() {
            return "{" +
                    "\"type\":\"" + type + "\"," +
                    "\"start\":" + start.toString() + "," +
                    "\"end\":" + end.toString() + "," +
                    "\"state\":\"" + state + "\"," +
                    "\"party\":\"" + party.getName() + "\"," +
                    "\"website\":\"" + website + "\"," +
                    "\"office\":\"" + office + "\"" +
                    "}";
        }
    }

    private final class URLs {
        private final String wikipediaURL;

        private URLs(JSONObject json) {
            wikipediaURL = "https://en.wikipedia.org/wiki/" + json.getString("wikipedia");
        }

        @Override
        public String toString() {
            return "{" +
                    "\"wikipediaURL\":\"" + wikipediaURL + "\"" +
                    "}";
        }
    }
}
