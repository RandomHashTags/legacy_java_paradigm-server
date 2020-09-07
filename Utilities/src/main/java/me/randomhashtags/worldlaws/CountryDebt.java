package me.randomhashtags.worldlaws;

public final class CountryDebt {
    private String current, publicDebt, total;

    public CountryDebt(String current, String publicDebt, String total) {
        this.current = current;
        this.publicDebt = publicDebt;
        this.total = total;
    }

    public String getCurrent() {
        return current;
    }
    public String getPublic() {
        return publicDebt;
    }
    public String getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "{\"current\":\"" + current + "\"," +
                "\"public\":\"" + publicDebt + "\"," +
                "\"total\":\"" + total + "\"" +
                "}";
    }
}
