package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public enum CountryRankings implements CountryRankingService {
    ADULT_HIV_PREVALENCE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_HIV/AIDS_adult_prevalence_rate",
            "%",
            NumberType.FLOAT,
            -1
    ),
    CO2_EMISSIONS(
            "https://en.wikipedia.org/wiki/List_of_countries_by_carbon_dioxide_emissions",
            " Mt/year",
            NumberType.FLOAT,
            2017
    ),
    CANNABIS_USE(
            "https://en.wikipedia.org/wiki/Annual_cannabis_use_by_country",
            "%",
            NumberType.FLOAT,
            -1
    ),
    CIVILIAN_FIREARMS(
            "https://en.wikipedia.org/wiki/Estimated_number_of_civilian_guns_per_capita_by_country",
            " per 100 persons",
            NumberType.FLOAT,
            2017
    ),
    CLIMATE_CHANGE_PERFORMANCE_INDEX(
            "https://en.wikipedia.org/wiki/Climate_Change_Performance_Index",
            " score",
            NumberType.FLOAT,
            2021
    ),
    CORRUPTION_PERCEPTION_INDEX(
            "https://en.wikipedia.org/wiki/Corruption_Perceptions_Index",
            " score",
            NumberType.FLOAT,
            2020
    ),
    DEMOCRACY_INDEX(
            "https://en.wikipedia.org/wiki/Democracy_Index",
            " score",
            NumberType.FLOAT,
            2020
    ),
    DIVORCE_RATE(
            "https://en.wikipedia.org/wiki/Divorce_demography",
            "%",
            NumberType.FLOAT,
            -1
    ),
    ECONOMIC_FREEDOM_INDEX(
            "https://en.wikipedia.org/wiki/Index_of_Economic_Freedom",
            " score",
            NumberType.FLOAT,
            2021
    ),
    EDUCATION_INDEX(
            "https://en.wikipedia.org/wiki/Education_Index",
            " index",
            NumberType.FLOAT,
            2019
    ),
    ELECTRICITY_CONSUMPTION(
            "https://en.wikipedia.org/wiki/List_of_countries_by_electricity_consumption",
            " kW*h/yr",
            NumberType.LONG,
            -1
    ),
    FRAGILE_STATE_INDEX(
            "https://en.wikipedia.org/wiki/List_of_countries_by_Fragile_States_Index",
            " score",
            NumberType.FLOAT,
            2021
    ),
    FREEDOM_RANKINGS(
            "https://en.wikipedia.org/wiki/Freedom_in_the_World",
            "pts",
            NumberType.INTEGER,
            2021
    ),
    GLOBAL_PEACE_INDEX(
            "https://en.wikipedia.org/wiki/Global_Peace_Index",
            " score",
            NumberType.FLOAT,
            2021
    ),
    GLOBAL_TERRORISM_INDEX(
            "https://en.wikipedia.org/wiki/Global_Terrorism_Index",
            " score",
            NumberType.FLOAT,
            2020
    ),
    HOMICIDE_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_intentional_homicide_rate",
            " per 100,000",
            NumberType.FLOAT,
            -1
    ),
    HUMAN_DEVELOPMENT_INDEX(
            "https://en.wikipedia.org/wiki/List_of_countries_by_Human_Development_Index",
            " HDI",
            NumberType.FLOAT,
            2020
    ),
    INCARCERATION_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_incarceration_rate",
            " per 100,000",
            NumberType.INTEGER,
            2020
    ),
    INFANT_MORTALITY_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_infant_and_under-five_mortality_rates",
            " per 1,000 live births",
            NumberType.FLOAT,
            2019
    ),
    INFLATION_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_inflation_rate",
            "%",
            NumberType.FLOAT,
            -1
    ),
    LEGATUM_PROSPERITY_INDEX(
            "https://en.wikipedia.org/wiki/Legatum_Prosperity_Index",
            " score",
            NumberType.FLOAT,
            2021
    ),
    LIFE_EXPECTANCY(
            "https://en.wikipedia.org/wiki/List_of_countries_by_life_expectancy",
            " years",
            NumberType.FLOAT,
            2020
    ),
    MATERNAL_MORTALITY_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_maternal_mortality_ratio",
            " per 100,000 live births",
            NumberType.INTEGER,
            2017
    ),
    NATURAL_DISASTER_RISK(
            "https://en.wikipedia.org/wiki/List_of_countries_by_natural_disaster_risk",
            "% risk",
            NumberType.FLOAT,
            2016
    ),
    OBESITY_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_obesity_rate",
            "%",
            NumberType.FLOAT,
            2016
    ),
    POPULATION(
            "https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population",
            "",
            NumberType.LONG,
            -1
    ),
    PRESS_FREEDOM_INDEX(
            "https://rsf.org/en/ranking/%year%",
            "Reporters Without Borders",
            " score",
            NumberType.FLOAT,
            2021
    ),
    QUALITY_OF_LIFE_INDEX(
            "https://en.wikipedia.org/wiki/Where-to-be-born_Index",
            " score",
            NumberType.FLOAT,
            2013
    ),
    QUALITY_OF_NATIONALITY_INDEX(
            "https://en.wikipedia.org/wiki/The_Quality_of_Nationality_Index",
            "% score",
            NumberType.FLOAT,
            2018
    ),
    SOCIAL_PROGRESS_INDEX(
            "https://en.wikipedia.org/wiki/Social_Progress_Index",
            " score",
            NumberType.FLOAT,
            2020
    ),
    SUICIDE_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_suicide_rate",
            " per 100,000",
            NumberType.FLOAT,
            2019
    ),
    UNEMPLOYMENT_RATE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_unemployment_rate",
            "%",
            NumberType.FLOAT,
            -1
    ),
    WORLD_GIVING_INDEX(
            "https://en.wikipedia.org/wiki/World_Giving_Index",
            null,
            NumberType.FLOAT,
            2018
    ),
    WORLD_HAPPINESS_REPORT(
            "https://en.wikipedia.org/wiki/World_Happiness_Report",
            " score",
            NumberType.FLOAT,
            2020
    ),
    ;

    // TODO: UPDATE!!
    // EMPLOYMENT_RATE, // https://en.wikipedia.org/wiki/List_of_countries_by_employment_rate
    // EXPORTS, // https://en.wikipedia.org/wiki/List_of_countries_by_exports
    // HEALTH_CARE_COST, // https://en.wikipedia.org/wiki/List_of_countries_by_total_health_expenditure_per_capita
    // HOME_OWNERSHIP, // https://en.wikipedia.org/wiki/List_of_countries_by_home_ownership_rate
    // IMPORTS, // https://en.wikipedia.org/wiki/List_of_countries_by_imports
    // INTERNET_CONNECTION_SPEEDS, // https://en.wikipedia.org/wiki/List_of_countries_by_Internet_connection_speeds
    // NUMBER_OF_BILLIONAIRES, // https://en.wikipedia.org/wiki/List_of_countries_by_number_of_billionaires
    // NUMBER_OF_MILLIONAIRES, // https://en.wikipedia.org/wiki/List_of_countries_by_the_number_of_millionaires
    // TARIFF_RATE, // https://en.wikipedia.org/wiki/List_of_countries_by_tariff_rate

    private final String url, siteName, suffix;
    private final NumberType valueType;
    private final int yearOfData;
    private String rankedJSON;

    CountryRankings(String url, String suffix, NumberType valueType, int yearOfData) {
        this(url, null, suffix, valueType, yearOfData);
    }
    CountryRankings(String url, String siteName, String suffix, NumberType valueType, int yearOfData) {
        this.url = url;
        this.siteName = siteName;
        this.suffix = suffix;
        this.valueType = valueType;
        this.yearOfData = yearOfData;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.valueOf("RANKING_" + name());
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getSiteName() {
        return siteName != null ? siteName : url.split("/wiki/")[1].replace("_", " ");
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public NumberType getValueType() {
        return valueType;
    }

    @Override
    public int getYearOfData() {
        return yearOfData;
    }

    @Override
    public String getRankedJSON() {
        return rankedJSON;
    }
    @Override
    public void setRankedJSON(String rankedJSON) {
        this.rankedJSON = rankedJSON;
    }

    @Override
    public JSONObject loadData() {
        final JSONObject json = new JSONObject();
        loadJSONData(json);
        return json;
    }
    private void loadJSONData(JSONObject json) {
        switch (this) {
            case ADULT_HIV_PREVALENCE:
                loadAdultHIVPrevalence(json);
                break;
            case CO2_EMISSIONS:
                loadCO2Emissions(json);
                break;
            case CANNABIS_USE:
                loadCannabisUse(json);
                break;
            case CIVILIAN_FIREARMS:
                loadCivilianFirearms(json);
                break;
            case CLIMATE_CHANGE_PERFORMANCE_INDEX:
                loadClimateChangePerformanceIndex(json);
                break;
            case CORRUPTION_PERCEPTION_INDEX:
                loadCorruptionPerceptionIndex(json);
                break;
            case DEMOCRACY_INDEX:
                loadDemocracyIndex(json);
                break;
            case DIVORCE_RATE:
                loadDivorceRate(json);
                break;
            case ECONOMIC_FREEDOM_INDEX:
                loadEconomicFreedomIndex(json);
                break;
            case EDUCATION_INDEX:
                loadEducationIndex(json);
                break;
            case ELECTRICITY_CONSUMPTION:
                loadElectricityConsumption(json);
                break;
            case FRAGILE_STATE_INDEX:
                loadFragileStateIndex(json);
                break;
            case FREEDOM_RANKINGS:
                loadFreedomRankings(json);
                break;
            case GLOBAL_PEACE_INDEX:
                loadGlobalPeaceIndex(json);
                break;
            case GLOBAL_TERRORISM_INDEX:
                loadGlobalTerrorismIndex(json);
                break;
            case HOMICIDE_RATE:
                loadHomicideRate(json);
                break;
            case HUMAN_DEVELOPMENT_INDEX:
                loadHumanDevelopmentIndex(json);
                break;
            case INCARCERATION_RATE:
                loadIncarcerationRate(json);
                break;
            case INFANT_MORTALITY_RATE:
                loadInfantMortalityRate(json);
                break;
            case INFLATION_RATE:
                loadInflationRate(json);
                break;
            case LEGATUM_PROSPERITY_INDEX:
                loadLegatumProsperityIndex(json);
                break;
            case LIFE_EXPECTANCY:
                loadLifeExpectancy(json);
                break;
            case MATERNAL_MORTALITY_RATE:
                loadMaternalMortalityRate(json);
                break;
            case NATURAL_DISASTER_RISK:
                loadNaturalDisasterRisk(json);
                break;
            case OBESITY_RATE:
                loadObesityRate(json);
                break;
            case POPULATION:
                loadPopulation(json);
                break;
            case PRESS_FREEDOM_INDEX:
                loadPressFreedomIndex(json);
                break;
            case QUALITY_OF_LIFE_INDEX:
                loadQualityOfLifeIndex(json);
                break;
            case QUALITY_OF_NATIONALITY_INDEX:
                loadQualityOfNationalityIndex(json);
                break;
            case SOCIAL_PROGRESS_INDEX:
                loadSocialProgressIndex(json);
                break;
            case SUICIDE_RATE:
                loadSuicideRate(json);
                break;
            case UNEMPLOYMENT_RATE:
                loadUnemploymentRate(json);
                break;
            case WORLD_GIVING_INDEX:
                loadWorldGivingIndex(json);
                break;
            case WORLD_HAPPINESS_REPORT:
                loadWorldHappinessReport(json);
                break;
            default:
                break;
        }
    }

    private void loadAdultHIVPrevalence(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            final String percentString = tds.get(1).text();
            return percentString.equals("-") ? -1 : Float.parseFloat(percentString.split("%")[0]);
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "").replace(",", "");
            final int yearOfData = Integer.parseInt(tds.get(4).text().split("\\[")[0]);
            final String percentString = tds.get(1).text();
            final float percent = percentString.equals("-") ? -1 : Float.parseFloat(percentString.split("%")[0]);
            final int defcon = percent >= 10.00 ? 1 : percent >= 8.00 ? 2 : percent >= 6.00 ? 3 : percent >= 4.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, percent, true);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadCO2Emissions(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.remove(0);
        elements.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Float.parseFloat(tds.get(3).text().replace(",", ""));
        }));
        final int maxWorldRank = elements.size();
        int worldRank = maxWorldRank;
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final Elements links = tds.select("a");
            final float emissions = Float.parseFloat(tds.get(3).text().replace(",", ""));
            final float percentOfTheWorld = Float.parseFloat(tds.get(4).text().replace("%", ""));
            final int defcon = emissions >= 1000.00 ? 1 : emissions >= 750.00 ? 2 : emissions >= 500.00 ? 3 : emissions >= 250.00 ? 4 : 5;
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(percentOfTheWorld, NumberType.FLOAT, "Percent of the World", "%"));
            for(Element link : links) {
                final String country = link.text().toLowerCase().replace(" ", "");
                final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, emissions, false);
                value.setOtherValues(values);
                value.setMaxWorldRank(maxWorldRank);
                json.put(country, value.toJSONObject());
            }
            worldRank -= 1;
        }
    }
    private void loadCannabisUse(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> Float.parseFloat(element.select("td").get(1).text())));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "");
            final String yearOfDataString = tds.get(2).text();
            final int yearOfData = yearOfDataString.isEmpty() ? -1 : Integer.parseInt(yearOfDataString);
            final float percent = Float.parseFloat(tds.get(1).text());
            final int defcon = percent >= 15.00 ? 1 : percent >= 12.00 ? 2 : percent >= 9.00 ? 3 : percent >= 6.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, percent, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadCivilianFirearms(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String estimate = tds.get(6).text().replace(",", ""), registeredFirearms = tds.get(8).text().replace(",", ""), unregisteredFirearms = tds.get(9).text().replace(",", "");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final int estimateInCivilianPossession = estimate.matches("[0-9]+") ? Integer.parseInt(estimate) : -1;
            final int registered = registeredFirearms.matches("[0-9]+") ? Integer.parseInt(registeredFirearms) : -1;
            final int unregistered = unregisteredFirearms.matches("[0-9]+") ? Integer.parseInt(unregisteredFirearms) : -1;
            final String country = tds.get(1).text().toLowerCase().replace(" ", "");
            final float estimatePer100 = Float.parseFloat(tds.get(2).text());
            final int defcon = -1;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, estimatePer100, true);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(estimateInCivilianPossession, NumberType.INTEGER, "Estimate in Civilian Possession", null));
            values.add(new CountryRankingInfoValueOther(registered, NumberType.INTEGER, "Registered", null));
            values.add(new CountryRankingInfoValueOther(unregistered, NumberType.INTEGER, "Unregistered", null));
            value.setOtherValues(values);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadClimateChangePerformanceIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        trs.remove(0);
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score < 40 ? 1 : score < 50 ? 2 : score < 60 ? 3 : score < 70 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadCorruptionPerceptionIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 1).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).select("a[href]").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score < 40 ? 1 : score < 60 ? 2 : score < 75 ? 3 : score < 90 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadDemocracyIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 5).select("tbody tr");
        trs.remove(0);
        trs.removeIf(tr -> {
            final Elements tds = tr.select("td");
            return tds.size() < 11;
        });

        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(2).selectFirst("a[href]").text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(4).text());
            final float electoralProcessAndPluralism = Float.parseFloat(tds.get(6).text());
            final float functioningOfGovernment = Float.parseFloat(tds.get(7).text());
            final float politicalParticipation = Float.parseFloat(tds.get(8).text());
            final float politicalCulture = Float.parseFloat(tds.get(9).text());
            final float civilLiberties = Float.parseFloat(tds.get(10).text());
            final int defcon = score <= 4.00 ? 1 : score <= 6.00 ? 2 : score <= 7.00 ? 3 : score <= 8.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(electoralProcessAndPluralism, NumberType.FLOAT, "Electoral process and pluralism", " score"));
            values.add(new CountryRankingInfoValueOther(functioningOfGovernment, NumberType.FLOAT, "Functioning of government", " score"));
            values.add(new CountryRankingInfoValueOther(politicalParticipation, NumberType.FLOAT, "Political participation", " score"));
            values.add(new CountryRankingInfoValueOther(politicalCulture, NumberType.FLOAT, "Political culture", " score"));
            values.add(new CountryRankingInfoValueOther(civilLiberties, NumberType.FLOAT, "Civil liberties", " score"));
            value.setOtherValues(values);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadDivorceRate(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        elements.removeIf(element -> element.select("td").get(5).text().isEmpty());
        elements.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Float.parseFloat(tds.get(5).text());
        }));
        final int maxWorldRank = elements.size();
        int worldRank = maxWorldRank;
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float percent = Float.parseFloat(tds.get(5).text());
            final int yearOfData = Integer.parseInt(tds.get(6).text().split("\\(")[1].split("\\)")[0]);
            final int defcon = percent >= 50.00 ? 1 : percent >= 40.00 ? 2 : percent >= 30.00 ? 3 : percent >= 20.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, percent, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadEconomicFreedomIndex(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final float percent = Float.parseFloat(tds.get(2).text());
            final int defcon = percent < 60.00 ? 1 : percent < 65.00 ? 2 : percent < 70.00 ? 3 : percent < 80.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, percent, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadEducationIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final int maxWorldRank = trs.size();
        final List<CountryRankingInfoValue> values = new ArrayList<>();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").split("\\(")[0];
            final float index = Float.parseFloat(tds.get(tds.size()-1).text());
            final int defcon = index < 0.500 ? 1 : index < 0.600 ? 2 : index < 0.700 ? 3 : index < 0.800 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, 0, -1, index, false);
            value.country = country;
            value.setMaxWorldRank(maxWorldRank);
            values.add(value);
        }
        values.sort(Comparator.comparingDouble(value -> value.getValue().floatValue()));
        int worldRank = maxWorldRank;
        for(CountryRankingInfoValue value : values) {
            value.setWorldRank(worldRank);
            json.put(value.country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadElectricityConsumption(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-content-ltr div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).text().toLowerCase().replace(" ", "");
            final long consumption = Long.parseLong(tds.get(2).text().split("\\[")[0].replace(",", "").split("\\.")[0]);
            final String[] yearValues = tds.get(3).text().split(" ");
            final int yearOfData = Integer.parseInt(yearValues[0]);
            final boolean isEstimate = yearValues.length > 1;
            final int defcon = -1;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, consumption, isEstimate);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadFragileStateIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final int maxWorldRank = trs.size();
        int lastWorldRank = 0;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String zero = tds.get(0).text(), one = tds.get(1).text(), two = tds.get(2).text();
            final boolean hasRank = zero.matches("[0-9]+");
            final int worldRank = hasRank ? Integer.parseInt(zero) : lastWorldRank;
            final String country = (hasRank ? one : zero).toLowerCase().split("\\[")[0].replace(" ", "");
            final float score = Float.parseFloat(hasRank ? two : one);
            final int defcon = score >= 90.00 ? 1 : score >= 80.00 ? 2 : score >= 70.00 ? 3 : score >= 60.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            lastWorldRank = worldRank;
        }
    }
    private void loadFreedomRankings(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            final int max = tds.size();
            return Integer.parseInt(tds.get(max-1).text());
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace("*", "").replace(" ", "");
            final int max = tds.size(), points = Integer.parseInt(tds.get(max-1).text()), civilRightsScore = Integer.parseInt(tds.get(max-3).text()), politicalRightsScore = Integer.parseInt(tds.get(max-4).text());
            final int defcon = points <= 30 ? 1 : points <= 35 ? 2 : points <= 60 ? 3 : points <= 70 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, points, false);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(politicalRightsScore, NumberType.INTEGER, "Political Rights Score", "pts"));
            values.add(new CountryRankingInfoValueOther(civilRightsScore, NumberType.INTEGER, "Civil Liberties Score", "pts"));
            value.setOtherValues(values);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadGlobalPeaceIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 1).select("tbody tr");
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score >= 2.75 ? 1 : score >= 2.50 ? 2 : score >= 2.25 ? 3 : score >= 2.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadGlobalTerrorismIndex(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 4).select("tbody tr");
        elements.remove(0);
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text().replace("=", ""));
            final String country = tds.get(1).text().toLowerCase().replace(" ", "").replace(",", "").replace("people'srepublicof", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score >= 8 ? 1 : score >= 6 ? 2 : score >= 4 ? 3 : score >= 2 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadHomicideRate(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 1).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        elements.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Float.parseFloat(tds.get(3).text());
        }));
        final int maxWorldRank = elements.size();
        int worldRank = maxWorldRank;
        for(Element element : elements) {
            final Elements headers = element.select("th");
            final boolean hasHeader = !headers.isEmpty();
            final Elements tds = element.select("td");
            final String country = (hasHeader ? headers : tds).get(0).text().toLowerCase().replace(" ", "").replace("*", "").replace(" ", "");
            final int yearOfData = Integer.parseInt(tds.get(hasHeader ? 4 : 5).text());
            final float rate = Float.parseFloat(tds.get(hasHeader ? 2 : 3).text());
            final int defcon = rate > 20.00 ? 1 : rate > 10 ? 2 : rate > 5 ? 3 : rate > 2.50 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, rate, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadHumanDevelopmentIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        trs.removeIf(element -> element.select("th").isEmpty() || element.select("td").get(0).text().equals("—"));
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = element.selectFirst("th").text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score < 0.450 ? 1 : score < 0.550 ? 2 : score < 0.70 ? 3 : score < 0.850 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadIncarcerationRate(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.sortable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(tr -> {
           final Elements tds = tr.select("td");
           return tds.size() < 3 || tds.get(1).text().equalsIgnoreCase("n/a") || tds.get(2).text().equalsIgnoreCase("n/a");
        });
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Integer.parseInt(tds.get(3).text().replace(",", ""));
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").split("\\[")[0].split(" \\(")[0].replace("*", "").replace(" ", "");
            final int ratePer100K = Integer.parseInt(tds.get(3).text().replace(",", ""));
            final int prisonPopulation = Integer.parseInt(tds.get(2).text().replace(",", ""));
            final int defcon = ratePer100K > 500 ? 1 : ratePer100K > 350 ? 2 : ratePer100K > 250 ? 3 : ratePer100K > 150 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, ratePer100K, false);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(prisonPopulation, NumberType.INTEGER, "Prison Population", " people"));
            value.setOtherValues(values);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadInfantMortalityRate(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.sortable", 0).select("tbody tr");
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Float.parseFloat(tds.get(1).text());
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "").replace("*", "").replace(" ", "");
            final float mortalityRate = Float.parseFloat(tds.get(1).text());
            final int defcon = mortalityRate >= 80.00 ? 1 : mortalityRate >= 60.00 ? 2 : mortalityRate >= 40.00 ? 3 : mortalityRate >= 20.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, mortalityRate, true);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadInflationRate(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            String percentString = tds.get(1).text().replace(",", "");
            final boolean isPositive = percentString.split("\\.")[0].matches("[0-9]+");
            if(!isPositive) {
                percentString = percentString.substring(1);
            }
            return Float.parseFloat(percentString) * (isPositive ? 1 : -1);
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace(",", "");
            final String yearOfDataString = tds.get(2).text();
            final boolean isEstimate = yearOfDataString.contains(" est");
            final String yearOfDataValue = yearOfDataString.split(" est")[0].split(" \\[")[0].split("\\[")[0];
            final String[] yearOfDataStringArray = yearOfDataValue.split(" ");
            final int count = yearOfDataStringArray.length, yearOfData = Integer.parseInt(count == 1 ? yearOfDataValue : yearOfDataStringArray[0].matches("[0-9]+") ? yearOfDataStringArray[0] : yearOfDataStringArray[1]);
            String percentString = tds.get(1).text().replace(",", "");
            final boolean isPositive = percentString.split("\\.")[0].matches("[0-9]+");
            if(!isPositive) {
                percentString = percentString.substring(1);
            }
            final float percent = Float.parseFloat(percentString) * (isPositive ? 1 : -1);
            final int defcon = percent > 10.00 ? 1 : percent > 8.00 ? 2 : percent > 6.00 ? 3 : percent > 4.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, percent, isEstimate);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadLegatumProsperityIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score < 40 ? 1 : score < 45 ? 2 : score < 55 ? 3 : score < 65 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadLifeExpectancy(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(0);
        elements.removeIf(element -> {
            final Elements tds = element.select("td");
            return tds.isEmpty() || tds.get(0).text().equals("—");
        });
        int lastWorldRank = 1;
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = lastWorldRank;
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", ""), femaleString = tds.get(3).text(), maleString = tds.get(2).text();
            final float average = Float.parseFloat(tds.get(1).text()), female = Float.parseFloat(femaleString), male = Float.parseFloat(maleString);
            final int defcon = average <= 60.00 ? 1 : average <= 65.00 ? 2 : average <= 70.00 ? 3 : average <= 75.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, average, true);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(female, NumberType.FLOAT, "Female Life Expectancy", " years"));
            values.add(new CountryRankingInfoValueOther(male, NumberType.FLOAT, "Male Life Expectancy", " years"));
            value.setOtherValues(values);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            lastWorldRank += 1;
        }
    }
    private void loadMaternalMortalityRate(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.remove(elements.size()-1);
        final int maxWorldRank = elements.size();
        int previousWorldRank = -1;
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final boolean hasWorldRank = tds.size() == 3;
            final String country = tds.get(hasWorldRank ? 1 : 0).text().toLowerCase().split("\\(")[0].replace(" ", "");
            final int worldRank = hasWorldRank ? Integer.parseInt(tds.get(0).text()) : previousWorldRank;
            previousWorldRank = worldRank;
            final int ratio = Integer.parseInt(tds.get(hasWorldRank ? 2 : 1).text().split("\\[")[0].replace(",", ""));
            final int defcon = ratio >= 350 ? 1 : ratio >= 280 ? 2 : ratio >= 140 ? 3 : ratio >= 70 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, ratio, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadNaturalDisasterRisk(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Float.parseFloat(tds.get(2).text().replace("%", ""));
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(1).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text().replace("%", ""));
            final int defcon = score >= 10.30 ? 1 : score >= 7.10 ? 2 : score >= 5.50 ? 3 : score >= 3.40 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadObesityRate(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace("federatedstatesof", "");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final float percent = Float.parseFloat(tds.get(2).text());
            final int defcon = percent >= 50.00 ? 1 : percent >= 40.00 ? 2 : percent >= 30.00 ? 3 : percent >= 20.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, percent, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadPopulation(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        elements.removeIf(element -> {
            final Elements ths = element.select("th");
            return ths.isEmpty() || ths.get(0).text().equals("–");
        });
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(element.selectFirst("th").text());
            final String country = tds.get(0).text().split("\\[")[0].toLowerCase().replace(" ", "");
            final long population = Long.parseLong(tds.get(2).text().replace(",", ""));
            final String[] dateValues = tds.get(4).text().split(" ");
            final int yearOfData = Integer.parseInt(dateValues[dateValues.length-1]);
            final int defcon = -1;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, population, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadPressFreedomIndex(JSONObject json) {
        final String url = this.url.replace("%year%", Integer.toString(yearOfData));
        final Elements elements = getRankingDocumentElements(url, "body div.wrapper-page section.main-wrapper div.region section.block section.ranking-map__panel ul.ranking-map__countries-list li a");
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements spans = element.select("span");
            final int worldRank = Integer.parseInt(spans.get(0).text());
            final String country = spans.get(1).text().toLowerCase().replace(" ", "").replace(",", "");
            final float score = Float.parseFloat(spans.get(2).text());
            final int defcon = score >= 70 ? 1 : score >= 50 ? 2 : score >= 35 ? 3 : score >= 20 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private String loadPressFreedomIndexLegacy() {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(element -> element.select("td").get(1).text().equals("N/A"));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        final StringBuilder builder = new StringBuilder("[");
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace(",", "").split("\\[")[0];
            final float score = Float.parseFloat(tds.get(1).text().split("\\)")[1]);
            final int defcon = score >= 70 ? 1 : score >= 50 ? 2 : score >= 35 ? 3 : score >= 15 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.country = country;
            builder.append(worldRank == maxWorldRank ? "" : ",").append(value.toServerJSON());
            worldRank -= 1;
        }
        builder.append("]");
        return builder.toString();
    }
    private void loadQualityOfLifeIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).text().toLowerCase().split("\\[")[0].replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score <= 4.00 ? 1 : score <= 5.00 ? 2 : score <= 6.00 ? 3 : score <= 7.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadQualityOfNationalityIndex(JSONObject json) {
        final Elements elements = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        elements.remove(0);
        final int maxWorldRank = elements.size();
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text().split("%")[0]);
            final int defcon = score < 20 ? 1 : score < 40 ? 2 : score < 60 ? 3 : score < 80.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadSocialProgressIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 1).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score < 60.00 ? 1 : score < 70.00 ? 2 : score < 80.00 ? 3 : score < 90.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadSuicideRate(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(tr -> tr.select("td").size() < 4 || tr.hasAttr("style") && tr.attr("style").equals("font-weight:bold"));
        trs.sort(Comparator.comparingDouble(tr -> {
            final Elements tds = tr.select("td");
            return Float.parseFloat(tds.get(1).text());
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "").replace("*", "").replace(" ", "");
            final float ratePer100_000 = Float.parseFloat(tds.get(1).text());
            final float ratePer100_000male = Float.parseFloat(tds.get(2).text());
            final float ratePer100_000female = Float.parseFloat(tds.get(3).text());
            final int defcon = ratePer100_000 > 20.00 ? 1 : ratePer100_000 > 15.00 ? 2 : ratePer100_000 > 10.00 ? 3 : ratePer100_000 > 5.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, ratePer100_000, true);
            final List<CountryRankingInfoValueOther> values = new ArrayList<>();
            values.add(new CountryRankingInfoValueOther(ratePer100_000male, NumberType.FLOAT, "Male Suicide Rate", " per 100,000"));
            values.add(new CountryRankingInfoValueOther(ratePer100_000female, NumberType.FLOAT, "Female Suicide Rate", " per 100,000"));
            value.setOtherValues(values);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadUnemploymentRate(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.sort(Comparator.comparing(element -> {
            final Elements tds = element.select("td");
            return Float.parseFloat(tds.get(1).text().split("\\[")[0]);
        }));
        final int maxWorldRank = trs.size();
        int worldRank = maxWorldRank;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "").replace("*", "").replace(" ", "");
            final String[] yearOfDataString = tds.get(2).text().split(" ");
            final int yearOfData = Integer.parseInt(removeReferences(yearOfDataString[yearOfDataString.length-1]));
            final float rate = Float.parseFloat(removeReferences(tds.get(1).text()));
            final int defcon = rate >= 25.00 ? 1 : rate >= 20.00 ? 2 : rate >= 15.00 ? 3 : rate >= 10.00 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, yearOfData, rate, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
            worldRank -= 1;
        }
    }
    private void loadWorldGivingIndex(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> {
            final String text = row.select("td").get(1).text();
            return text.equals("n/a");
        });
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final int worldRank = Integer.parseInt(tds.get(1).text());
            final int defcon = worldRank >= 100 ? 1 : worldRank >= 80 ? 2 : worldRank >= 60 ? 3 : worldRank >= 40 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, -1, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
    private void loadWorldHappinessReport(JSONObject json) {
        final Elements trs = getRankingDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final int maxWorldRank = trs.size();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text());
            final String country = tds.get(1).select("a").get(0).text().toLowerCase().replace(" ", "");
            final float score = Float.parseFloat(tds.get(2).text());
            final int defcon = score < 4 ? 1 : score < 5 ? 2 : score < 6 ? 3 : score < 7 ? 4 : 5;
            final CountryRankingInfoValue value = new CountryRankingInfoValue(defcon, worldRank, -1, score, false);
            value.setMaxWorldRank(maxWorldRank);
            json.put(country, value.toJSONObject());
        }
    }
}
