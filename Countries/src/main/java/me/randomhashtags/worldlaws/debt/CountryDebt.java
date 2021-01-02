package me.randomhashtags.worldlaws.debt;

public final class CountryDebt {
    private double governmentHoldings, publicDebt, total;

    public CountryDebt(double governmentHoldings, double publicDebt, double total) {
        this.governmentHoldings = governmentHoldings;
        this.publicDebt = publicDebt;
        this.total = total;
    }

    public double getGovernmentHoldings() {
        return governmentHoldings;
    }
    public double getPublic() {
        return publicDebt;
    }
    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "{" +
                "\"governmentHoldings\":" + governmentHoldings + "," +
                "\"publicDebt\":" + publicDebt + "," +
                "\"totalDebt\":" + total +
                "}";
    }
}
