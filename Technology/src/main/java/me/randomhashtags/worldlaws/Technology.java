package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
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
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        return null; // TODO: fix this
        /*
        final String target = request.getTarget();
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "smartphones":
                return getSmartphoneCompanyResponse(target.substring(key.length()+1));
            default:
                return null;
        }*/
    }

    private String getSmartphoneCompanyResponse(String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "brands":
                return getSmartphoneBrands();
            default:
                final int length = values.length;
                final SmartphoneCompany smartphoneCompany = valueOfSmartphoneCompanyBackendID(key);
                if(smartphoneCompany != null) {
                    if(length == 1) {
                        return smartphoneCompany.getSmartphoneListJSON();
                    } else if(length == 2) {
                        return smartphoneCompany.getSmartphoneDetails(values[1]);
                    }
                }
                return null;
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
