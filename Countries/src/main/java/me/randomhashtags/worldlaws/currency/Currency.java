package me.randomhashtags.worldlaws.currency;

public final class Currency {
    private String recordDate, country, currency, exchangeRate;

    public Currency(String recordDate, String country, String currency, String exchangeRate) {
        this.recordDate = recordDate;
        this.country = country;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
    }

    public String getRecordDate() {
        return recordDate;
    }
    public String getCountry() {
        return country;
    }
    public String getCurrency() {
        return currency;
    }
    public String getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public String toString() {
        return "{" +
                "\"recordDate\":\"" + recordDate + "\"," +
                "\"country\":\"" + country + "\"," +
                "\"currency\":\"" + currency + "\"," +
                "\"exchangeRate\":" + exchangeRate +
                "}";
    }
}
