package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.usa.federal.USCongress;
import me.randomhashtags.worldlaws.country.usa.federal.FederalGovernment;
import me.randomhashtags.worldlaws.country.usa.service.CongressService;
import me.randomhashtags.worldlaws.country.usa.service.usaproject.UnitedStatesProject;
import me.randomhashtags.worldlaws.law.State;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.location.ICountry;

public enum USLaws implements ICountry {
    INSTANCE;

    private CongressService politicianService;

    USLaws() {
        politicianService = UnitedStatesProject.INSTANCE;
    }

    @Override
    public WLCountry getCountryBackendID() {
        return WLCountry.UNITED_STATES;
    }

    public void getResponse(String input, CompletionHandler handler) {
        final String[] values = input.replace("?", "").split("/");
        final String key = values[0];
        final int length = values.length;
        if(key.startsWith("congress")) {
            getCongressResponse(values, handler);
        } else {
            switch (key) {
                case "federal":
                    FederalGovernment.INSTANCE.getIndexesJSON();
                    break;
                default:
                    final String response = getLawResponse(values, length);
                    handler.handle(response);
                    break;
            }
        }
    }

    private void getCongressResponse(String[] values, CompletionHandler handler) {
        final int version = Integer.parseInt(values[0].substring("congress".length()));
        final USCongress congress = USCongress.getCongress(version);
        switch (values[1]) {
            case "enactedbills":
                handler.handle(congress.getEnactedBills());
                break;
            case "bill":
                congress.getBill(USChamber.valueOf(values[2].toUpperCase()), values[3], handler);
                break;
            case "politician":
                politicianService.getPolitician(values[2], handler);
                break;
            default:
                final BillStatus status = BillStatus.valueOf(values[1].toUpperCase());
                handler.handle(congress.getBillsByStatus(status));
                break;
        }
    }

    private String getLawResponse(String[] values, int length) {
        final USState usstate = USState.valueOf(values[0].toUpperCase());
        final State state = usstate.getState();
        switch (length) {
            case 1:
                return state.getIndexesJSON();
            case 2:
                final String[] array = values[1].split("\\+");
                switch (array.length) {
                    case 1: return state.getTableOfChapters(array[0]);
                    case 2: return state.getStatuteList(array[0], array[1]);
                    case 3: return state.getStatute(array[0], array[1], array[2]);
                    default: return "";
                }
            default: return "";
        }
    }
}
