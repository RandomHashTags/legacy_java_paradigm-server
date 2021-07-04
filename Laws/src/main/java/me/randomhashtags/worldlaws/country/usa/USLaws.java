package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.APIVersion;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void getRecentActivity(APIVersion version, CompletionHandler handler) {
        final LocalDate startingDate = LocalDate.now().minusDays(7);
        final USCongress congress = USCongress.getCongress(USCongress.getCurrentAdministrationVersion());
        final BillStatus[] statuses = new BillStatus[] {
                BillStatus.BECAME_LAW,
                BillStatus.VETO_ACTIONS
        };
        final int max = statuses.length;
        final HashMap<BillStatus, HashSet<PreCongressBill>> values = new HashMap<>();
        final AtomicInteger completed = new AtomicInteger(0);
        Arrays.asList(statuses).parallelStream().forEach(status -> {
            congress.getPreCongressBillsBySearch(status, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    if(object != null) {
                        @SuppressWarnings({ "unchecked" })
                        final HashSet<PreCongressBill> bills = (HashSet<PreCongressBill>) object;
                        bills.removeIf(bill -> bill.getDate().getLocalDate().isBefore(startingDate));
                        values.put(status, bills);
                    }
                    if(completed.addAndGet(1) == max) {
                        String string = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("{");
                            boolean isFirstStatus = true;
                            for(Map.Entry<BillStatus, HashSet<PreCongressBill>> map : values.entrySet()) {
                                final BillStatus status = map.getKey();
                                builder.append(isFirstStatus ? "" : ",").append("\"").append(status.name().toLowerCase()).append("\":{");
                                final HashSet<PreCongressBill> bills = map.getValue();
                                boolean isFirst = true;
                                for(PreCongressBill bill : bills) {
                                    builder.append(isFirst ? "" : ",").append(bill.toString());
                                    isFirst = false;
                                }
                                isFirstStatus = false;
                                builder.append("}");
                            }
                            builder.append("}");
                            string = builder.toString();
                        }
                        handler.handleString(string);
                    }
                }
            });
        });
    }

    @Override
    public void getResponse(APIVersion version, String input, CompletionHandler handler) {
        final String[] values = input.replace("?", "").split("/");
        final String key = values[0];
        final int length = values.length;
        if(key.startsWith("congress")) {
            getCongressResponse(version, values, handler);
        } else {
            switch (key) {
                case "federal":
                    FederalGovernment.INSTANCE.getIndexesJSON();
                    break;
                default:
                    final String response = getLawResponse(values, length);
                    handler.handleString(response);
                    break;
            }
        }
    }

    private void getCongressResponse(APIVersion version, String[] values, CompletionHandler handler) {
        final int congressVersion = Integer.parseInt(values[0].substring("congress".length()));
        final USCongress congress = USCongress.getCongress(congressVersion);
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
