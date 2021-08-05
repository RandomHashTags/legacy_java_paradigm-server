package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.LawController;
import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.UnitedStatesSubdivisions;
import me.randomhashtags.worldlaws.country.usa.federal.FederalGovernment;
import me.randomhashtags.worldlaws.country.usa.federal.PreCongressBill;
import me.randomhashtags.worldlaws.country.usa.federal.USCongress;
import me.randomhashtags.worldlaws.country.usa.service.UnitedStatesProject;
import me.randomhashtags.worldlaws.country.usa.state.*;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.Connecticut;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.Indiana;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.NorthCarolina;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.Oregon;

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
        final USCongress congress = USCongress.getCongress(getCurrentAdministrationVersion());
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
                        if(!bills.isEmpty()) {
                            values.put(status, bills);
                        }
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
        switch (key) {
            case "federal":
                FederalGovernment.INSTANCE.getIndexesJSON();
                break;
            default:
                final String response = getLawResponse(key, values, length);
                handler.handleString(response);
                break;
        }
    }

    @Override
    public void getGovernmentResponse(APIVersion version, int administration, String input, CompletionHandler handler) {
        final String[] values = input.split("/");
        final String key = values[0];
        final USCongress congress = USCongress.getCongress(administration);
        switch (key) {
            case "enactedbills":
                congress.getEnactedBills(handler);
                break;
            case "bill":
                congress.getBill(USChamber.valueOf(values[1].toUpperCase()), values[2], handler);
                break;
            case "politician":
                politicianService.getPolitician(values[1], handler);
                break;
            default:
                final BillStatus status = BillStatus.valueOf(key.toUpperCase());
                congress.getBillsByStatus(status, handler);
                break;
        }
    }

    private String getLawResponse(String key, String[] values, int length) {
        final UnitedStatesSubdivisions usstate = UnitedStatesSubdivisions.valueOf(key.toUpperCase());
        final LawSubdivisionController state = getStateFrom(usstate);
        if(state != null) {
            switch (length) {
                case 1:
                    return state.getIndexesJSON();
                case 2:
                    final String[] array = values[1].split("\\+");
                    final String zero = array[0];
                    switch (array.length) {
                        case 1: return state.getTableOfChapters(zero);
                        case 2: return state.getStatuteList(zero, array[1]);
                        case 3: return state.getStatute(zero, array[1], array[2]);
                        default: return "";
                    }
                default: return "";
            }
        }
        return null;
    }

    private LawSubdivisionController getStateFrom(UnitedStatesSubdivisions state) {
        switch (state) {
            case ALABAMA: return null; // incomplete - http://alisondb.legislature.state.al.us/alison/codeofalabama/1975/coatoc.htm
            case ALASKA: return Alaska.INSTANCE;
            case ARKANSAS: return null; // incomplete - https://advance.lexis.com/container?config=00JAA3ZTU0NTIzYy0zZDEyLTRhYmQtYmRmMS1iMWIxNDgxYWMxZTQKAFBvZENhdGFsb2cubRW4ifTiwi5vLw6cI1uX&crid=c09db75f-5410-4ac2-9710-5f75d54774b2
            case ARIZONA: return Arizona.INSTANCE;
            case CALIFORNIA: return null;
            case COLORADO: return null;
            case CONNECTICUT: return Connecticut.INSTANCE; // incomplete - unable to get correct index document
            case DELAWARE: return Delaware.INSTANCE;
            case FLORIDA: return Florida.INSTANCE;
            case GEORGIA: return null; // incomplete - https://advance.lexis.com/container?config=00JAAzZDgzNzU2ZC05MDA0LTRmMDItYjkzMS0xOGY3MjE3OWNlODIKAFBvZENhdGFsb2fcIFfJnJ2IC8XZi1AYM4Ne&crid=7e31b0f3-f5ae-469c-aaab-6dc43140c8b2
            case HAWAII: return null;
            case IDAHO: return Idaho.INSTANCE;
            case ILLINOIS: return null;
            case INDIANA: return Indiana.INSTANCE; // incomplete - unable to get correct index document
            case IOWA: return Iowa.INSTANCE; // completed - fix statutes (pdf/rtf)
            case KANSAS: return Kansas.INSTANCE;
            case KENTUCKY: return Kentucky.INSTANCE; // completed - fix statutes list
            case LOUISIANA: return null; // incomplete - http://legis.la.gov/Legis/Laws_Toc.aspx?folder=75&level=Parent
            case MAINE: return null; // incomplete - http://www.mainelegislature.org/legis/statutes/
            case MARYLAND: return null; // incomplete - doesn't have titles, only chapters (http://mgaleg.maryland.gov/mgawebsite/Laws/Statutes)
            case MASSACHUSETTS: return null; // TODO: support parts
            case MICHIGAN: return Michigan.INSTANCE;
            case MINNESOTA: return Minnesota.INSTANCE;
            case MISSISSIPPI: return null;
            case MISSOURI: return Missouri.INSTANCE;
            case MONTANA: return Montana.INSTANCE; // completed - fix statutes list (support parts)
            case NEBRASKA: return null; // incomplete - doesn't have titles, only chapters (https://www.nebraskalegislature.gov/laws/browse-statutes.php)
            case NEVADA: return null;
            case NEW_HAMPSHIRE: return NewHampshire.INSTANCE;
            case NEW_JERSEY: return null;
            case NEW_MEXICO: return null; // incomplete - doesn't have titles, only chapters (https://nmonesource.com/nmos/nmsa/en/nav_alpha.do)
            case NEW_YORK: return null; // incomplete - doesn't have titles (http://public.leginfo.state.ny.us/lawssrch.cgi?NVLWO:)
            case NORTH_CAROLINA: return NorthCarolina.INSTANCE; // incomplete - pdf
            case NORTH_DAKOTA: return NorthDakota.INSTANCE; // completed - fix statutes (pdf)
            case OHIO: return Ohio.INSTANCE;
            case OKLAHOMA: return null; // only available to download as rtf
            case OREGON: return Oregon.INSTANCE; // incomplete - unable to get correct index document
            case PENNSYLVANIA: return Pennsylvania.INSTANCE;
            case RHODE_ISLAND: return RhodeIsland.INSTANCE;
            case SOUTH_CAROLINA: return null;
            case SOUTH_DAKOTA: return SouthDakota.INSTANCE; // fix if statute is 2-paragraph
            case TENNESSEE: return null; // incomplete - https://advance.lexis.com/container?config=014CJAA5ZGVhZjA3NS02MmMzLTRlZWQtOGJjNC00YzQ1MmZlNzc2YWYKAFBvZENhdGFsb2e9zYpNUjTRaIWVfyrur9ud&crid=bac664b6-23a1-4129-b54b-a8f0e6612a09
            case TEXAS: return null;
            case UTAH: return Utah.INSTANCE; // incomplete - index unable to get correct index document
            case VERMONT: return Vermont.INSTANCE;
            case VIRGINIA: return Virginia.INSTANCE;
            case WASHINGTON: return Washington.INSTANCE;
            case WEST_VIRGINA: return WestVirginia.INSTANCE;
            case WISCONSIN: return Wisconsin.INSTANCE; // completed - fix statutes
            case WYOMING: return null;
            default: return null;
        }
    }
}
