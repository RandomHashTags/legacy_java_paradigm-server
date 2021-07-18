package me.randomhashtags.worldlaws.location.constitutions;

import me.randomhashtags.worldlaws.location.ConstitutionArticle;
import me.randomhashtags.worldlaws.location.ConstitutionSection;
import me.randomhashtags.worldlaws.location.WLConstitution;

import java.util.Arrays;
import java.util.List;

public enum UnitedStatesConstitution implements WLConstitution {
    INSTANCE;

    @Override
    public String getURL() {
        return "https://constitution.congress.gov/constitution/";
    }

    @Override
    public String getWikipediaURL() {
        return "https://en.wikipedia.org/wiki/Constitution_of_the_United_States";
    }

    @Override
    public List<ConstitutionArticle> getSummary() {
        return Arrays.asList(
                new ConstitutionArticle("Preamble", (String) null),
                new ConstitutionArticle("Article I", Arrays.asList(
                    new ConstitutionSection("Section 1", null),
                    new ConstitutionSection("Section 2", null),
                    new ConstitutionSection("Section 3", null),
                    new ConstitutionSection("Section 4", null),
                    new ConstitutionSection("Section 5", null),
                    new ConstitutionSection("Section 6", null),
                    new ConstitutionSection("Section 7", null),
                    new ConstitutionSection("Section 8", null),
                    new ConstitutionSection("Section 9", null),
                    new ConstitutionSection("Section 10", null)
                )),
                new ConstitutionArticle("Article II", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null),
                        new ConstitutionSection("Section 4", null)
                )),
                new ConstitutionArticle("Article III", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null)
                )),
                new ConstitutionArticle("Article IV", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null),
                        new ConstitutionSection("Section 4", null)
                )),
                new ConstitutionArticle("Article V", (String) null),
                new ConstitutionArticle("Article VI", (String) null),
                new ConstitutionArticle("Article VII", (String) null),
                new ConstitutionArticle("First Amendment", "Congress shall make no law respecting an establishment of religion, or prohibiting the exercise of freedom of speech, freedom of press, or the people's right to peacefully assemble."),
                new ConstitutionArticle("Second Amendment", (String) null),
                new ConstitutionArticle("Third Amendment", (String) null),
                new ConstitutionArticle("Fourth Amendment", (String) null),
                new ConstitutionArticle("Fifth Amendment", (String) null),
                new ConstitutionArticle("Sixth Amendment", (String) null),
                new ConstitutionArticle("Seventh Amendment", (String) null),
                new ConstitutionArticle("Eighth Amendment", (String) null),
                new ConstitutionArticle("Ninth Amendment", (String) null),
                new ConstitutionArticle("Tenth Amendment", (String) null),
                new ConstitutionArticle("Eleventh Amendment", (String) null),
                new ConstitutionArticle("Twelfth Amendment", (String) null),
                new ConstitutionArticle("Thirteenth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null)
                )),
                new ConstitutionArticle("Fourteenth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null),
                        new ConstitutionSection("Section 4", null),
                        new ConstitutionSection("Section 5", null)
                )),
                new ConstitutionArticle("Fifteenth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null)
                )),
                new ConstitutionArticle("Sixteenth Amendment", (String) null),
                new ConstitutionArticle("Seventeenth Amendment", (String) null),
                new ConstitutionArticle("Eighteenth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null)
                )),
                new ConstitutionArticle("Nineteenth Amendment", (String) null),
                new ConstitutionArticle("Twentieth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null),
                        new ConstitutionSection("Section 4", null),
                        new ConstitutionSection("Section 5", null),
                        new ConstitutionSection("Section 6", null)
                )),
                new ConstitutionArticle("Twenty-First Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null)
                )),
                new ConstitutionArticle("Twenty-Second Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null)
                )),
                new ConstitutionArticle("Twenty-Third Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null)
                )),
                new ConstitutionArticle("Twenty-Fourth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null)
                )),
                new ConstitutionArticle("Twenty-Fifth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null),
                        new ConstitutionSection("Section 3", null),
                        new ConstitutionSection("Section 4", null)
                )),
                new ConstitutionArticle("Twenty-Sixth Amendment", Arrays.asList(
                        new ConstitutionSection("Section 1", null),
                        new ConstitutionSection("Section 2", null)
                )),
                new ConstitutionArticle("Twenty-Seventh Amendment", (String) null)
        );
    }
}
