package me.randomhashtags.worldlaws.states;

import me.randomhashtags.worldlaws.states.unfinished.Connecticut;
import me.randomhashtags.worldlaws.states.unfinished.Indiana;
import me.randomhashtags.worldlaws.states.unfinished.NorthCarolina;
import me.randomhashtags.worldlaws.states.unfinished.Oregon;

public enum USState {

    //ALABAMA(Alabama.INSTANCE), // incomplete - http://alisondb.legislature.state.al.us/alison/codeofalabama/1975/coatoc.htm
    ALASKA(Alaska.INSTANCE),
    ARIZONA(Arizona.INSTANCE),
    //ARKANSA(Arkansas.INSTANCE), // incomplete - https://advance.lexis.com/container?config=00JAA3ZTU0NTIzYy0zZDEyLTRhYmQtYmRmMS1iMWIxNDgxYWMxZTQKAFBvZENhdGFsb2cubRW4ifTiwi5vLw6cI1uX&crid=c09db75f-5410-4ac2-9710-5f75d54774b2
    //CALIFORNIA
    //COLORADO
    CONNECTICUT(Connecticut.INSTANCE), // incomplete - unable to get correct index document
    DELAWARE(Delaware.INSTANCE),
    FLORIDA(Florida.INSTANCE),
    //GEORGIA(Georgia.INSTANCE), // incomplete - https://advance.lexis.com/container?config=00JAAzZDgzNzU2ZC05MDA0LTRmMDItYjkzMS0xOGY3MjE3OWNlODIKAFBvZENhdGFsb2fcIFfJnJ2IC8XZi1AYM4Ne&crid=7e31b0f3-f5ae-469c-aaab-6dc43140c8b2
    //HAWAII(Hawaii.INSTANCE),
    IDAHO(Idaho.INSTANCE),
    //ILLINOIS
    INDIANA(Indiana.INSTANCE), // incomplete - unable to get correct index document
    IOWA(Iowa.INSTANCE), // completed - fix statutes (pdf/rtf)
    KANSAS(Kansas.INSTANCE),
    KENTUCKY(Kentucky.INSTANCE), // completed - fix statutes list
    //LOUISIANA(Louisiana.INSTANCE), // incomplete - http://legis.la.gov/Legis/Laws_Toc.aspx?folder=75&level=Parent
    //MAINE(Maine.INSTANCE), // incomplete - http://www.mainelegislature.org/legis/statutes/
    //MARYLAND(Maryland.INSTANCE), // incomplete - doesn't have titles, only chapters (http://mgaleg.maryland.gov/mgawebsite/Laws/Statutes)
    //MASSACHUSETTS(Massachusetts.INSTANCE), // TODO: support parts
    MICHIGAN(Michigan.INSTANCE),
    MINNESOTA(Minnesota.INSTANCE),
    //MISSISSIPPI
    MISSOURI(Missouri.INSTANCE),
    MONTANA(Montana.INSTANCE), // completed - fix statutes list (support parts)
    //NEBRASKA(Nebraska.INSTANCE), // incomplete - doesn't have titles, only chapters (https://www.nebraskalegislature.gov/laws/browse-statutes.php)
    //NEVADA
    NEW_HAMPSHIRE(NewHampshire.INSTANCE),
    //NEW_JERSEY
    //NEW_MEXICO(NewMexico.INSTANCE), // incomplete - doesn't have titles, only chapters (https://nmonesource.com/nmos/nmsa/en/nav_alpha.do)
    //NEW_YORK(NewYork.INSTANCE), // incomplete - doesn't have titles (http://public.leginfo.state.ny.us/lawssrch.cgi?NVLWO:)
    NORTH_CAROLINA(NorthCarolina.INSTANCE), // incomplete - pdf
    NORTH_DAKOTA(NorthDakota.INSTANCE), // completed - fix statutes (pdf)
    OHIO(Ohio.INSTANCE),
    //OKLAHOMA(Oklahoma.INSTANCE), // only available to download as rtf
    OREGON(Oregon.INSTANCE), // incomplete - unable to get correct index document
    PENNSYLVANIA(Pennsylvania.INSTANCE),
    RHODE_ISLAND(RhodeIsland.INSTANCE),
    //SOUTH_CAROLINA
    SOUTH_DAKOTA(SouthDakota.INSTANCE), // fix if statute is 2-paragraph
    //TENNESSEE(Tennessee.INSTANCE), // incomplete - https://advance.lexis.com/container?config=014CJAA5ZGVhZjA3NS02MmMzLTRlZWQtOGJjNC00YzQ1MmZlNzc2YWYKAFBvZENhdGFsb2e9zYpNUjTRaIWVfyrur9ud&crid=bac664b6-23a1-4129-b54b-a8f0e6612a09
    //TEXAS
    UTAH(Utah.INSTANCE), // incomplete - index unable to get correct index document
    VERMONT(Vermont.INSTANCE),
    VIRGINIA(Virginia.INSTANCE),
    WASHINGTON(Washington.INSTANCE),
    WEST_VIRGINIA(WestVirginia.INSTANCE),
    WISCONSIN(Wisconsin.INSTANCE), // completed - fix statutes
    //WYOMING
    ;

    private State state;

    USState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}