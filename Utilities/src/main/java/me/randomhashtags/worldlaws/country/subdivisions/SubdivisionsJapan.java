package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
    HYOGO, // Hyōgo
    IBARAKI,
    ISHIKAWA,
    IWATE,
    KAGAWA,
    KAGOSHIMA,
    KANAGAWA,
    KOCHI, // Kōchi
    KUMAMOTO,
    KYOTO,
    MIE,
    MIYAGI,
    MIYAZAKI,
    NAGANO,
    NAGASAKI,
    NARA,
    NIIGATA,
    OITA, //  Ōita
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
    public String getPostalCodeAbbreviation() {
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
