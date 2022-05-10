package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public enum WLCurrency {
    AED("United Arab Emirates Dirham"),
    AFN("Afghan Afghani"),
    ALL("Albanian Lek"),
    AMD("Armenian Dram"),
    ANG("Netherlands Antillean Guilder"),
    AOA("Angolan Kwanza"),
    ARS("Argentine Peso"),
    AUD("Australian Dollar"),
    AWG("Aruban Florin"),
    AZN("Azerbaijani Manat"),

    BAM("Bosnia and Herzegovina Convertible Mark"),
    BBD("Barbados Dollar"),
    BDT("Bangladeshi Taka"),
    BGN("Bulgarian Lev"),
    BHD("Bahraini Dinar", 3),
    BIF("Burundian Franc", 0),
    BMD("Bermudian Dollar"),
    BND("Brunei Dollar"),
    BOB("Boliviano"),
    BOV("Bolician Mvdol"),
    BRL("Brazilian Real"),
    BSD("Bahamian Dollar"),
    BTN("Bhutanese Ngultrum"),
    BWP("Botswana Pula"),
    BYN("Belarusian Ruble"),
    BZD("Belize Dollar"),

    CAD("Canadian Dollar"),
    CDF("Congolese Franc"),
    CHE("WIR Euro"),
    CHF("Swiss Franc"),
    CHW("WIR Franc"),
    CLF("Unidad de Fomento", 4),
    CLP("Chilean Peso", 0),
    CNY("Chinese Yuan"),
    COP("Colombian Peso"),
    COU("Unidad de Valor Real"),
    CRC("Costa Rican Colon"),
    CUC("Cuban Convertible Peso"),
    CUP("Cuban Peso"),
    CVE("Cape Verdean Escudo"),
    CZK("Czech Koruna"),

    DJF("Djiboutian Franc", 0),
    DKK("Danish Krone"),
    DOP("Dominican Peso"),
    DZD("Algerian Dinar"),

    EGP("Egyptian Pound"),
    ENR("Eritrean Nakfa"),
    ERN("Eritrean Nakfa"),
    ETB("Ethiopian Birr"),
    EUR("Euro"),

    FJD("Fiji Dollar"),
    FKP("Falkland Islands Pound"),

    GBP("Pound Sterling"),
    GEL("Georgian Lari"),
    GHS("Ghanaian Cedi"),
    GIP("Gibraltar Pound"),
    GMD("Gambian Dalasi"),
    GNF("Guinean Franc", 0),
    GTQ("Guatemalan Quetzal"),
    GYD("Guyanese Dollar"),

    HKD("Hong Kong Dollar"),
    HNL("Honduran Lempira"),
    HRK("Croatian Kuna"),
    HTG("Haitian Gourde"),
    HUF("Hungarian Forint"),

    IDR("Indonesian Rupiah"),
    ILS("Israeli New Shekel"),
    INR("Indian Rupee"),
    IQD("Iraqi Dinar", 3),
    IRR("Iranian Rial"),
    ISK("Icelandic Króna", 0),

    JMD("Jamaican Dollar"),
    JOD("Jordanian Dinar"),
    JPY("Japanese Yen", 0),

    KES("Kenyan Shilling"),
    KGS("Kyrgyzstani Som"),
    KHR("Cambodian Riel"),
    KMF("Comoro Franc", 0),
    KPW("North Korean Won"),
    KRW("South Korean Won", 0),
    KWD("Kuwaiti Dinar", 3),
    KYD("Cayman Islands Dollar"),
    KZT("Kazakhstani Tenge"),

    LAK("Lao Kip"),
    LBP("Lebanese Pound"),
    LKR("Sri Lankan Rupee"),
    LRD("Liberian Dollar"),
    LSL("Lesotho Loti"),
    LYD("Libyan Dinar"),

    MAD("Moroccan Dirham"),
    MDL("Moldovan Leu"),
    MGA("Malagasy Ariary"),
    MKD("Macedonian Denar"),
    MMK("Myanmar Kyat"),
    MNT("Mongolian Tögrög"),
    MOP("Macanese Pataca"),
    MRU("Mauritania Ouguiya"),
    MUR("Mauritian Rupee"),
    MVR("Maldivian Rufiyaa"),
    MWK("Malawian Kwacha"),
    MXN("Mexican Peso"),
    MXV("Mexican Unidad de Inversion"),
    MYR("Malaysian Ringgit"),
    MZN("Mozambican Metical"),

    NAD("Namibian Dollar"),
    NGN("Nigerian Naira"),
    NIO("Nicaraguan Córdoba"),
    NOK("Norwegian Krone"),
    NPR("Nepalese Rupee"),
    NZD("New Zealand Dollar"),

    OMR("Omani Rial", 3),

    PAB("Panamanian Balboa"),
    PEN("Peruvian Sol"),
    PGK("Papua New Guinean kina"),
    PHP("Philippine Peso"),
    PKR("Pakistani Rupee"),
    PLN("Polish Złoty"),
    PYG("Paraguayan Guaraní", 0),

    QAR("Qatari Riyal"),

    RON("Romanian Leu", 0),
    RSD("Serbian Dinar"),
    RUB("Russian Ruble"),
    RWF("Rwandan Franc"),

    SAR("Saudi Riyal"),
    SBD("Solomon Islands Dollar"),
    SCR("Seychelles Rupee"),
    SDG("Sudanese Pound"),
    SEK("Swedish Krona"),
    SGD("Singapore Dollar"),
    SHP("Saint Helena Pound"),
    SLL("Sierra Leonean Leone"),
    SOS("Somali Shilling"),
    SRD("Surinamese Dollar"),
    SSP("South Sudanese Pound"),
    STN("São Tomé and Príncipe Dobra"),
    SVC("Salvadoran Colón"),
    SYP("Syrian Pound"),
    SZL("Swazi Lilangeni"),

    THB("Thai Baht"),
    TJS("Tajikistani Somoni"),
    TMT("Turkmenistan Manat"),
    TND("Tunisian Dinar", 3),
    TOP("Tongan Pa'anga"),
    TRY("Turkish Lira"),
    TTD("Trinidad and Tobago Dollar"),
    TWD("New Taiwan Dollar"),
    TZS("Tanzanian Shilling"),

    UAH("Ukrainian Hryvnia"),
    UGX("Ugandan Shilling", 0),
    USD("United States Dollar"),
    USN("United States Dollar (next day)"),
    UYI("Uruguay Peso en Unidades Indexadas", 0),
    UYU("Uruguayan Peso"),
    UYW("Unidad Previsional", 4),
    UZS("Uzbekistan Som"),

    VED("Venezuelan Bolívar Digital"),
    VES("Venezuelan Bolívar Soberano"),
    VND("Vietnamese đồng", 0),
    VUV("Vanuatu Vatu", 0),

    WST("Samoan Tala"),

    XAF("CFA Franc BEAC", 0),
    XAG("Silver", -1),
    XAU("Gold", -1),
    XBA("European Composite Unit", -1),
    XBB("European Monetary Unit", -1),
    XBC("European Unit of Account 9", -1),
    XBD("European Unit of Account 17", -1),
    XCD("East Caribbean Dollar"),
    XDR("Special drawing rights", -1),
    XOF("CFA Franc CVEAO", 0),
    XPD("Palladium", -1),
    XPF("CFP Franc", 0),
    XPT("Platinum", -1),
    XSU("SUCRE", -1),
    XTS("Code reserved for testing", -1),
    XUA("ABD Unit of Account", -1),
    XXX("No currency", -1),

    YER("Yemeni Rial"),

    ZAR("South African Rand"),
    ZMW("Zambian Kwacha"),
    ZWL("Zimbabwean Dollar"),

    BDS(false, "Barbados Dollar"),
    CKD(false, "Cook Islands Dollar"),
    CNH(false, "Chinese Yuan (offshore)"),
    CNT(false, "Chinese Yuan (offshore)"),
    FOK(false, "Faroese Króna"),
    GGP(false, "Guernsey Pound"),
    IMP(false, "Island of Man Pound"),
    JEP(false, "Jersey Pound"),
    KID(false, "Kiribati Dollar"),
    NIS(false, "Israeli New shekel"),
    NTD(false, "New Taiwan Dollar"),
    PRB(false, "Transnistrian Ruble"),
    RMB(false, "Chinese Yuan"),
    SLS(false, "Somaliland Shilling"),
    TVD(false, "Tuvalu Dollar"),
    ZWB(false, "Zimbabwean bonds")
    ;

    private final boolean official;
    private final String name;
    private final int decimalPoints;

    WLCurrency(String name) {
        this(name, 2);
    }
    WLCurrency(boolean official, String name) {
        this(official, name, 2);
    }

    WLCurrency(String name, int decimalPoints) {
        this(true, name, decimalPoints);
    }
    WLCurrency(boolean official, String name, int decimalPoints) {
        this.official = official;
        this.name = name;
        this.decimalPoints = decimalPoints;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("identifier", name());
        json.put("name", name);
        if(!official) {
            json.put("official", false);
        }
        if(decimalPoints != 2) {
            json.put("decimalPoints", decimalPoints);
        }
        return json;
    }
}
