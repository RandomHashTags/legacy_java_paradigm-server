package me.randomhashtags.worldlaws.location;

public enum GovernmentDetails {
    ARGENTINA(
            "https://www.argentina.gob.ar"
    ),
    AUSTRALIA(
            "https://www.australia.gov.au"
    ),
    BAHAMAS(
            "https://www.bahamas.gov.bs"
    ),
    BRAZIL(
            "http://www.brazil.gov.br"
    ),
    CANADA(
            "https://www.canada.ca/en.html"
    ),
    CHINA(
            "https://www.gov.cn"
    ),
    DENMARK(
            "https://denmark.dk"
    ),
    EGYPT(
            "http://egypt.gov.eg/"
    ),
    FIJI(
            "https://www.fiji.gov.fj/Home"
    ),
    FINLAND(
            "https://valtioneuvosto.fi/etusivu"
    ),
    FRANCE(
            "https://www.gouvernement.fr"
    ),
    GERMANY(
            "https://www.bundesregierung.de/breg-de"
    ),
    GREECE(
            "https://www.gov.gr"
    ),
    ICELAND(
            "https://www.government.is"
    ),
    INDIA(
            "https://www.india.gov.in"
    ),
    IRELAND(
            "https://www.gov.ie"
    ),
    ISRAEL(
            "https://www.gov.il/en"
    ),
    ITALY(
            "http://www.italia.it/"
    ),
    JAPAN(
            "https://www.japan.go.jp"
    ),
    JAMAICA(
            "https://www.gov.jm"
    ),
    LUXEMBOURG(
            "https://gouvernement.lu"
    ),
    MEXICO(
            "https://www.gob.mx"
    ),
    NEW_ZEALAND(
            "https://www.govt.nz"
    ),
    NETHERLANDS(
            "https://www.government.nl"
    ),
    NORWAY(
            "https://www.regjeringen.no"
    ),
    RUSSIA(
            "http://government.ru"
    ),
    SINGAPORE(
            "https://www.gov.sg"
    ),
    SWEDEN(
            "https://www.government.se"
    ),
    SWITZERLAND(
            "https://www.admin.ch"
    ),
    UNITED_KINGDOM(
            "https://www.gov.uk"
    ),
    UNITED_STATES(
            "https://www.usa.gov"
            ),
    ZAMBIA(
            "https://zambia.co.zm"
    ),
    ;

    private final String url;

    GovernmentDetails(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "{" +
                "\"url\":\"" + url + "\"" +
                "}";
    }
}
