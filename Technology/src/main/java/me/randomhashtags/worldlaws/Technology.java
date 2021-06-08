package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.smartphones.*;

public final class Technology implements WLServer {

    private String smartphones;

    public static void main(String[] args) {
        new Technology();
    }

    private Technology() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.TECHNOLOGY;
    }

    private void test() {
    }

    private SmartphoneCompany[] getCompanies() {
        return new SmartphoneCompany[] {
                PhonesApple.INSTANCE,
                PhonesGoogle.INSTANCE,
                PhonesHuawei.INSTANCE,
                PhonesOnePlus.INSTANCE,
                PhonesSamsung.INSTANCE
        };
    }

    @Override
    public void getServerResponse(ServerVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "smartphones":
                getSmartphoneCompanyResponse(target.substring(key.length()+1), handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return null;
    }

    private void getSmartphoneCompanyResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "brands":
                handler.handle(getSmartphoneBrands());
                break;
            default:
                final int length = values.length;
                final SmartphoneCompany smartphoneCompany = valueOfSmartphoneCompanyBackendID(key);
                if(smartphoneCompany != null) {
                    if(length == 1) {
                        smartphoneCompany.getSmartphoneListJSON(handler);
                    } else if(length == 2) {
                        smartphoneCompany.getSmartphoneDetails(values[1], handler);
                    }
                }
                break;
        }
    }
    private String getSmartphoneBrands() {
        if(smartphones == null) {
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(SmartphoneCompany company : getCompanies()) {
                builder.append(isFirst ? "" : ",").append("\"").append(company.getBackendID()).append("\"");
                isFirst = false;
            }
            builder.append("]");
            smartphones = builder.toString();
        }
        return smartphones;
    }

    private SmartphoneCompany valueOfSmartphoneCompanyBackendID(String backendID) {
        for(SmartphoneCompany company : getCompanies()) {
            if(backendID.equals(company.getBackendID())) {
                return company;
            }
        }
        return null;
    }
}
