package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.smartphones.*;

public final class Technology implements DataValues {

    private String smartphones;

    public static void main(String[] args) {
        new Technology();
    }

    private Technology() {
        LocalServer.start("Technology", WL_TECHNOLOGY_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String response = object.toString();
                        client.sendResponse(response);
                    }
                });
            }
        });
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

    private void getResponse(String target, CompletionHandler handler) {
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
