package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.country.usa.federal.FederalGovernment;
import me.randomhashtags.worldlaws.country.usa.federal.PreCongressBill;
import me.randomhashtags.worldlaws.country.usa.federal.USCongress;
import me.randomhashtags.worldlaws.country.usa.service.UnitedStatesProject;
import me.randomhashtags.worldlaws.country.usa.state.*;
import me.randomhashtags.worldlaws.country.usa.state.recode.*;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.Connecticut;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.Indiana;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.NorthCarolina;
import me.randomhashtags.worldlaws.country.usa.state.unfinished.Oregon;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.recode.TestLawSubdivisionController;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class USLaws extends LawController {
    public static final USLaws INSTANCE = new USLaws();
    private final CongressService politicianService;

    private USLaws() {
        politicianService = UnitedStatesProject.INSTANCE;
    }

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public BillStatus[] getBillStatuses() {
        return USBillStatus.values();
    }

    @Override
    public JSONObjectTranslatable refreshRecentActivity(APIVersion version) {
        final LocalDate startingDate = LocalDate.now().minusDays(30);
        final USCongress congress = USCongress.getCongress(getCurrentAdministrationVersion());
        final USBillStatus[] statuses = new USBillStatus[] {
                USBillStatus.BECAME_LAW,
                USBillStatus.VETOED
        };

        final HashMap<USBillStatus, HashSet<PreCongressBill>> values = new HashMap<>();
        new CompletableFutures<USBillStatus>().stream(Arrays.asList(statuses), status -> {
            final HashSet<PreCongressBill> bills = congress.getPreCongressBillsBySearch(status);
            if(bills != null) {
                bills.removeIf(bill -> bill.getDate().getLocalDate().isBefore(startingDate));
                if(!bills.isEmpty()) {
                    values.put(status, bills);
                }
            }
        });

        JSONObjectTranslatable json = null;
        if(!values.isEmpty()) {
            json = new JSONObjectTranslatable("statuses");

            final JSONObjectTranslatable statusesJSON = new JSONObjectTranslatable();
            for(BillStatus status : getBillStatuses()) {
                final String id = status.getID();
                statusesJSON.put(id, status.toJSONObject());
                statusesJSON.addTranslatedKey(id);
            }
            json.put("statuses", statusesJSON);

            for(Map.Entry<USBillStatus, HashSet<PreCongressBill>> map : values.entrySet()) {
                final USBillStatus status = map.getKey();
                final String key = status.getName();
                final JSONObjectTranslatable activity = USCongress.getPreCongressBillsJSON(map.getValue());
                json.put(key, activity);
            }
        }
        return json;
    }

    @Override
    public JSONObjectTranslatable getResponse(APIVersion version, String input) {
        final String[] values = input.replace("?", "").split("/");
        final String key = values[0];
        switch (key) {
            case "federal":
                return FederalGovernment.INSTANCE.getIndexesJSON();
            case "subdivision":
                final String[] subdivisionValues = input.substring(key.length()+1).split("/");
                try {
                    final SubdivisionsUnitedStates usstate = SubdivisionsUnitedStates.valueOf(subdivisionValues[0].toUpperCase());
                    return handleSubdivisionResponse(usstate, subdivisionValues);
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
                return null;
            default:
                WLLogger.logError(this, "getResponse(" + input + ") == null!");
                return null;
        }
    }

    @Override
    public JSONObjectTranslatable getGovernmentResponse(APIVersion version, int administration, String input) {
        final String[] values = input.split("/");
        final String key = values[0];
        final USCongress congress = USCongress.getCongress(administration);
        switch (key) {
            case "enactedbills":
                return congress.getEnactedBills();
            case "bill":
                return congress.getBill(USChamber.valueOf(values[1].toUpperCase()), values[2]);
            case "politician":
                return politicianService.getPolitician(values[1]);
            default:
                final USBillStatus status = USBillStatus.valueOf(key.toUpperCase());
                return congress.getBillsByStatus(status);
        }
    }

    private JSONObjectTranslatable handleSubdivisionResponse(SubdivisionsUnitedStates usstate, String[] values) {
        final TestLawSubdivisionController controller = getSubdivisionFrom(usstate);
        if(controller != null) {
            final int length = values.length;
            switch (length) {
                case 1:
                    return controller.getIndexes();
                case 2:
                    final String[] array = values[1].split("\\+");
                    final String zero = array[0];
                    switch (array.length) {
                        case 1:
                            return controller.getTableOfChapters(zero);
                        case 2:
                            return controller.getStatutesList(zero, array[1]);
                        case 3:
                            return controller.getStatute(zero, array[1], array[2]);
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        } else {
            return getSubdivisionResponse(usstate, values);
        }
    }
    private JSONObjectTranslatable getSubdivisionResponse(SubdivisionsUnitedStates usstate, String[] values) {
        final LawSubdivisionController state = getStateFrom(usstate);
        if(state != null) {
            final int length = values.length;
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
                        default: return null;
                    }
                default:
                    return null;
            }
        }
        return null;
    }

    private TestLawSubdivisionController getSubdivisionFrom(SubdivisionsUnitedStates state) {
        switch (state) {
            case ARIZONA: return Arizona.INSTANCE;
            case DELAWARE: return Delaware.INSTANCE;
            case FLORIDA: return Florida.INSTANCE;
            case IDAHO: return Idaho.INSTANCE;
            case MICHIGAN: return Michigan.INSTANCE;
            case MINNESOTA: return Minnesota.INSTANCE;
            case NEW_HAMPSHIRE: return NewHampshire.INSTANCE;
            case OHIO: return Ohio.INSTANCE;
            case VERMONT: return Vermont.INSTANCE;
            case WASHINGTON: return Washington.INSTANCE;
            default: return null;
        }
    }
    private LawSubdivisionController getStateFrom(SubdivisionsUnitedStates state) {
        switch (state) {
            case ALABAMA: return null; // incomplete - http://alisondb.legislature.state.al.us/alison/codeofalabama/1975/coatoc.htm
            case ALASKA: return Alaska.INSTANCE;
            case ARKANSAS: return null; // incomplete - https://advance.lexis.com/container?config=00JAA3ZTU0NTIzYy0zZDEyLTRhYmQtYmRmMS1iMWIxNDgxYWMxZTQKAFBvZENhdGFsb2cubRW4ifTiwi5vLw6cI1uX&crid=c09db75f-5410-4ac2-9710-5f75d54774b2
            case CALIFORNIA: return null;
            case COLORADO: return null;
            case CONNECTICUT: return Connecticut.INSTANCE; // incomplete - unable to get correct index document
            case GEORGIA: return null; // incomplete - https://advance.lexis.com/container?config=00JAAzZDgzNzU2ZC05MDA0LTRmMDItYjkzMS0xOGY3MjE3OWNlODIKAFBvZENhdGFsb2fcIFfJnJ2IC8XZi1AYM4Ne&crid=7e31b0f3-f5ae-469c-aaab-6dc43140c8b2
            case HAWAII: return null;
            case ILLINOIS: return null;
            case INDIANA: return Indiana.INSTANCE; // incomplete - unable to get correct index document
            case IOWA: return Iowa.INSTANCE; // completed - fix statutes (pdf/rtf)
            case KANSAS: return Kansas.INSTANCE;
            case KENTUCKY: return Kentucky.INSTANCE; // completed - fix statutes list
            case LOUISIANA: return null; // incomplete - http://legis.la.gov/Legis/Laws_Toc.aspx?folder=75&level=Parent
            case MAINE: return null; // incomplete - http://www.mainelegislature.org/legis/statutes/
            case MARYLAND: return null; // incomplete - doesn't have titles, only chapters (http://mgaleg.maryland.gov/mgawebsite/Laws/Statutes)
            case MASSACHUSETTS: return null; // TODO: support parts
            case MISSISSIPPI: return null;
            case MISSOURI: return Missouri.INSTANCE;
            case MONTANA: return Montana.INSTANCE; // completed - fix statutes list (support parts)
            case NEBRASKA: return null; // incomplete - doesn't have titles, only chapters (https://www.nebraskalegislature.gov/laws/browse-statutes.php)
            case NEVADA: return null;
            case NEW_JERSEY: return null;
            case NEW_MEXICO: return null; // incomplete - doesn't have titles, only chapters (https://nmonesource.com/nmos/nmsa/en/nav_alpha.do)
            case NEW_YORK: return null; // incomplete - doesn't have titles (http://public.leginfo.state.ny.us/lawssrch.cgi?NVLWO:)
            case NORTH_CAROLINA: return NorthCarolina.INSTANCE; // incomplete - pdf
            case NORTH_DAKOTA: return NorthDakota.INSTANCE; // completed - fix statutes (pdf)
            case OKLAHOMA: return null; // only available to download as rtf
            case OREGON: return Oregon.INSTANCE; // incomplete - unable to get correct index document
            case PENNSYLVANIA: return Pennsylvania.INSTANCE;
            case RHODE_ISLAND: return RhodeIsland.INSTANCE;
            case SOUTH_CAROLINA: return null;
            case SOUTH_DAKOTA: return SouthDakota.INSTANCE; // fix if statute is 2-paragraph
            case TENNESSEE: return null; // incomplete - https://advance.lexis.com/container?config=014CJAA5ZGVhZjA3NS02MmMzLTRlZWQtOGJjNC00YzQ1MmZlNzc2YWYKAFBvZENhdGFsb2e9zYpNUjTRaIWVfyrur9ud&crid=bac664b6-23a1-4129-b54b-a8f0e6612a09
            case TEXAS: return null;
            case UTAH: return Utah.INSTANCE; // incomplete - index unable to get correct index document
            case VIRGINIA: return Virginia.INSTANCE;
            case WEST_VIRGINA: return WestVirginia.INSTANCE;
            case WISCONSIN: return Wisconsin.INSTANCE; // completed - fix statutes
            case WYOMING: return null;
            default: return null;
        }
    }
}
