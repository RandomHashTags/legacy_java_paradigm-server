package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.LawController;
import me.randomhashtags.worldlaws.country.State;
import me.randomhashtags.worldlaws.country.usa.federal.FederalGovernment;
import me.randomhashtags.worldlaws.country.usa.federal.PreCongressBill;
import me.randomhashtags.worldlaws.country.usa.federal.USCongress;
import me.randomhashtags.worldlaws.country.usa.service.CongressService;
import me.randomhashtags.worldlaws.country.usa.service.usaproject.UnitedStatesProject;
import me.randomhashtags.worldlaws.location.WLCountry;

import java.time.LocalDate;
import java.util.HashSet;

public enum USLaws implements LawController {
    INSTANCE;

    private final CongressService politicianService;

    USLaws() {
        politicianService = UnitedStatesProject.INSTANCE;
    }

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public void getRecentActivity(CompletionHandler handler) {
        final LocalDate startingDate = LocalDate.now().minusDays(7);
        final int currentCongressVersion = USCongress.getCurrentAdministrationVersion();
        USCongress.getCongress(currentCongressVersion).getPreCongressBillsBySearch(BillStatus.BECAME_LAW, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                if(object != null) {
                    @SuppressWarnings({ "unchecked" })
                    final HashSet<PreCongressBill> bills = (HashSet<PreCongressBill>) object;
                    bills.removeIf(bill -> bill.getDate().getLocalDate().isBefore(startingDate));
                    String string = null;
                    if(!bills.isEmpty()) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(PreCongressBill bill : bills) {
                            builder.append(isFirst ? "" : ",").append(bill.toString());
                            isFirst = false;
                        }
                        builder.append("}");
                        string = builder.toString();
                    }
                    handler.handle(string);
                } else {
                    handler.handle(null);
                }
            }
        });
    }

    @Override
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
                congress.getEnactedBills(handler);
                break;
            case "bill":
                congress.getBill(USChamber.valueOf(values[2].toUpperCase()), values[3], handler);
                break;
            case "politician":
                politicianService.getPolitician(values[2], handler);
                break;
            default:
                final BillStatus status = BillStatus.valueOf(values[1].toUpperCase());
                congress.getBillsByStatus(status, handler);
                break;
        }
    }

    private String getLawResponse(String[] values, int length) {
        final USStateEnum usstate = USStateEnum.valueOf(values[0].toUpperCase());
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
