package me.randomhashtags.worldlaws.country.subdivisions.j;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsJapan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Japan
    AICHI,
    AKITA,
    AOMORI,
    CHIBA,
    EHIME,
    FUKUI,
    FUKUOKA,
    FUKUSHIMA,
    GIFU,
    GUNMA,
    HIROSHIMA,
    HOKKAIDO,
    HYOGO,
    IBARAKI,
    ISHIKAWA,
    IWATE,
    KAGAWA,
    KAGOSHIMA,
    KANAGAWA,
    KOCHI,
    KUMAMOTO,
    KYOTO,
    MIE,
    MIYAGI,
    MIYAZAKI,
    NAGANO,
    NAGASAKI,
    NARA,
    NIIGATA,
    OITA,
    OKAYAMA,
    OKINAWA,
    OSAKA,
    SAGA,
    SAITAMA,
    SHIGA,
    SHIMANE,
    SHIZUOKA,
    TOCHIGI,
    TOKUSHIMA,
    TOKYO,
    TOTTORI,
    TOYAMA,
    WAKAYAMA,
    YAMAGATA,
    YAMAGUCHI,
    YAMANASHI,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.JAPAN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PREFECTURES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case HYOGO: return "Hyōgo";
            case KOCHI: return "Kōchi";
            case OITA: return "Ōita";
            default: return null;
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
