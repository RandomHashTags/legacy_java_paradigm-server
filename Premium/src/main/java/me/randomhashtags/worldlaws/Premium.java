package me.randomhashtags.worldlaws;

public final class Premium implements WLServer {

    public static void main(String[] args) {
        new Premium().load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.PREMIUM;
    }

    @Override
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "verify":
                switch (values[1]) {
                    case "apple":
                        return Verification.INSTANCE.verifyAppleAutoRenewableSubscription(values[2]);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
