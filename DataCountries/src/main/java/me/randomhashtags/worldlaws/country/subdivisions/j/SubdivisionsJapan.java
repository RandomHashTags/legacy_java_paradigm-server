package me.randomhashtags.worldlaws.country.subdivisions.j;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsJapan implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Japan | https://en.wikipedia.org/wiki/Prefectures_of_Japan
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
            case HOKKAIDO: return "Hokkaidō";
            case HYOGO: return "Hyōgo";
            case KOCHI: return "Kōchi";
            case KYOTO: return "Kyōto";
            case OITA: return "Ōita";
            case OSAKA: return "Ōsaka";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case HOKKAIDO:
            case TOKYO:
                return "";
            default:
                return null;
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            case AICHI: return "0/02/Flag_of_Aichi_Prefecture.svg";
            case AKITA: return "8/84/Flag_of_Akita_Prefecture.svg";
            case AOMORI: return "3/30/Flag_of_Aomori_Prefecture.svg";
            case CHIBA: return "0/0a/Flag_of_Chiba_Prefecture.svg";
            case EHIME: return "2/2d/Flag_of_Ehime_Prefecture.svg";
            case FUKUI: return "5/56/Flag_of_Fukui_Prefecture.svg";
            case FUKUOKA: return "7/71/Flag_of_Fukuoka_Prefecture.svg";
            case FUKUSHIMA: return "4/4b/Flag_of_Fukushima_Prefecture.svg";
            case GIFU: return "3/3e/Flag_of_Gifu_Prefecture.svg";
            case GUNMA: return "b/ba/Flag_of_Gunma_Prefecture.svg";
            case HIROSHIMA: return "e/ed/Flag_of_Hiroshima_Prefecture.svg";
            case HOKKAIDO: return "2/22/Flag_of_Hokkaido_Prefecture.svg";
            case HYOGO: return "7/74/Flag_of_Hyogo_Prefecture.svg";
            case IBARAKI: return "a/a8/Flag_of_Ibaraki_Prefecture.svg";
            case ISHIKAWA: return "6/6a/Flag_of_Ishikawa_Prefecture.svg";
            case IWATE: return "a/a9/Flag_of_Iwate_Prefecture.svg";
            case KAGAWA: return "2/29/Flag_of_Kagawa_Prefecture.svg";
            case KAGOSHIMA: return "c/c5/Flag_of_Kagoshima_Prefecture.svg";
            case KANAGAWA: return "a/a7/Flag_of_Kanagawa_Prefecture.svg";
            case KOCHI: return "5/50/Flag_of_Kochi_Prefecture.svg";
            case KUMAMOTO: return "f/f7/Flag_of_Kumamoto_Prefecture.svg";
            case KYOTO: return "0/06/Flag_of_Kyoto_Prefecture.svg";
            case MIE: return "8/8c/Flag_of_Mie_Prefecture.svg";
            case MIYAGI: return "c/c7/Flag_of_Miyagi_Prefecture.svg";
            case MIYAZAKI: return "0/0b/Flag_of_Miyazaki_Prefecture.svg";
            case NAGANO: return "f/f0/Flag_of_Nagano_Prefecture.svg";
            case NAGASAKI: return "6/65/Flag_of_Nagasaki_Prefecture.svg";
            case NARA: return "0/00/Flag_of_Nara_Prefecture.svg";
            case NIIGATA: return "c/cb/Flag_of_Niigata_Prefecture.svg";
            case OITA: return "c/c8/Flag_of_Oita_Prefecture.svg";
            case OKAYAMA: return "3/33/Flag_of_Okayama_Prefecture.svg";
            case OKINAWA: return "2/2f/Flag_of_Okinawa_Prefecture.svg";
            case OSAKA: return "5/5a/Flag_of_Osaka_Prefecture.svg";
            case SAGA: return "1/18/Flag_of_Saga_Prefecture.svg";
            case SAITAMA: return "c/cd/Flag_of_Saitama_Prefecture.svg";
            case SHIGA: return "9/99/Flag_of_Shiga_Prefecture.svg";
            case SHIMANE: return "e/e8/Flag_of_Shimane_Prefecture.svg";
            case SHIZUOKA: return "9/92/Flag_of_Shizuoka_Prefecture.svg";
            case TOCHIGI: return "d/d5/Flag_of_Tochigi_Prefecture.svg";
            case TOKUSHIMA: return "a/ac/Flag_of_Tokushima_Prefecture.svg";
            case TOKYO: return "1/15/Flag_of_Tokyo_Metropolis.svg";
            case TOTTORI: return "1/1c/Flag_of_Tottori_Prefecture.svg";
            case TOYAMA: return "1/1d/Flag_of_Toyama_Prefecture.svg";
            case WAKAYAMA: return "6/6e/Flag_of_Wakayama_Prefecture.svg";
            case YAMAGATA: return "a/a1/Flag_of_Yamagata_Prefecture.svg";
            case YAMAGUCHI: return "b/b9/Flag_of_Yamaguchi_Prefecture.svg";
            case YAMANASHI: return "0/00/Flag_of_Yamanashi_Prefecture.svg";
            default: return null;
        }
    }
}
