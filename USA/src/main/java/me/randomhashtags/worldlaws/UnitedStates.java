package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.federal.Congress;
import me.randomhashtags.worldlaws.federal.FederalGovernment;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.ICountry;
import me.randomhashtags.worldlaws.states.State;
import me.randomhashtags.worldlaws.states.USState;

public final class UnitedStates implements ICountry {

    private static UnitedStates INSTANCE;

    public static void main(String[] args) {
        INSTANCE = new UnitedStates();
        INSTANCE.init();
    }

    private void init() {
        //USADebt.INSTANCE.update();
        LocalServer.start(Country.UNITED_STATES_OF_AMERICA, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                client.sendResponse(getResponse(client.getTarget()));
            }
        });
    }

    @Override
    public Country getCountry() {
        return Country.UNITED_STATES_OF_AMERICA;
    }

    private String getResponse(String input) {
        final String[] values = input.replace("?", "").split("/");
        final String key = values[0];
        final int length = values.length;
        if(key.startsWith("congress")) {
            return getCongressResponse(values);
        } else {
            switch (key) {
                case "federal":
                    return FederalGovernment.INSTANCE.getIndexesJSON();
                case "law":
                    return getLawResponse(values, length);
                case "debt":
                    return getDebtResponse(values[1]);
                default:
                    return "error;values[0]=" + values[0];
            }
        }
    }


    private String getCongressResponse(String[] values) {
        final Congress congress = Congress.valueOf("v" + values[0].substring("congress".length()));
        switch (values[1]) {
            case "politicians": return congress.getPoliticians();
            case "enactedbills": return congress.getEnactedBills();
            case "enactedbill": return congress.getEnactedBill(Chamber.valueOf(values[2].toUpperCase()), values[3]);
            default: return "";
        }
    }

    private String getLawResponse(String[] values, int length) {
        final USState usstate = USState.valueOf(values[1].toUpperCase());
        final State state = usstate.getState();
        switch (length) {
            case 2:
                return state.getIndexesJSON();
            case 3:
                final String[] array = values[2].split("\\+");
                switch (array.length) {
                    case 1: return state.getTableOfChapters(array[0]);
                    case 2: return state.getStatuteList(array[0], array[1]);
                    case 3: return state.getStatute(array[0], array[1], array[2]);
                    default: return "";
                }
            default: return "";
        }
    }

    private String getDebtResponse(String value) {
        switch (value) {
            case "current": return USADebt.INSTANCE.getCurrent();
            case "historic": return USADebt.INSTANCE.getHistoric();
            default: return "";
        }
    }
}
