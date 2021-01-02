package me.randomhashtags.worldlaws.smartphones;

public final class PreSmartphone {
    private final String brand, backendID, model;

    public PreSmartphone(String brand, String backendID, String model) {
        this.brand = brand;
        this.backendID = backendID;
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }
    public String getBackendID() {
        return backendID;
    }
    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "{" +
                "\"brand\":\"" + brand + "\"," +
                "\"backendID\":\"" + backendID + "\"," +
                "\"model\":\"" + model + "\"" +
                "}";
    }
}
