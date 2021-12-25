package me.randomhashtags.worldlaws.country.subdivisions.g;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsGuadeloupe implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Guadeloupe#Governance
    ANSE_BERTRAND,
    BAIE_MANHAULT,
    BAILLIF,
    BASSE_TERRE,
    BOUILLANTE,
    CAPESTERRE_BELLE_EAU,
    CAPESTERRE_DE_MARIE_GALANTE,
    DESHAIES,
    GOURBEYRE,
    GOYAVE,
    GRAND_BOURG,
    LA_DESIRADE,
    LAMENTIN,
    LE_GOSIER,
    LE_MOULE,
    LES_ABYMES,
    MORNE_A_L_EAU,
    PETIT_BOURG,
    PETIT_CANAL,
    POINTE_A_PITRE,
    POINTE_NOIRE,
    PORT_LOUIS,
    SAINT_CLAUDE,
    SAINT_FRANCOIS,
    SAINT_LOUIS,
    SAINTE_ANNE,
    SAINTE_ROSE,
    TERRE_DE_BAS,
    TERRE_DE_HAUT,
    TROIS_RIVIERES,
    VIEUX_FORT,
    VIEUX_HABITANTS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.GUADELOUPE;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.COMMUNES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ANSE_BERTRAND: return "Anse-Bertrand";
            case BAIE_MANHAULT: return "Baie-Mahault";
            case BASSE_TERRE: return "Basse-Terre";
            case CAPESTERRE_BELLE_EAU: return "Capesterre-Belle-Eau";
            case CAPESTERRE_DE_MARIE_GALANTE: return "Capesterre-de-Marie-Galante";
            case GRAND_BOURG: return "Grand-Bourg";
            case LA_DESIRADE: return "La Désirade";
            case MORNE_A_L_EAU: return "Morne-à-l'Eau";
            case PETIT_BOURG: return "Petit-Bourg";
            case PETIT_CANAL: return "Petit-Canal";
            case POINTE_A_PITRE: return "Pointe-à-Pitre";
            case POINTE_NOIRE: return "Pointe-Noire";
            case PORT_LOUIS: return "Port-Louis";
            case SAINT_CLAUDE: return "Saint-Claude";
            case SAINT_FRANCOIS: return "Saint-François";
            case SAINT_LOUIS: return "Saint-Louis";
            case SAINTE_ANNE: return "Sainte-Anne";
            case SAINTE_ROSE: return "Sainte-Rose";
            case TERRE_DE_BAS: return "Terre-de-Bas";
            case TERRE_DE_HAUT: return "Terre-de-Haut";
            case TROIS_RIVIERES: return "Trois-Rivières";
            case VIEUX_FORT: return "Vieux-Fort";
            case VIEUX_HABITANTS: return "Vieux-Habitants";
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
