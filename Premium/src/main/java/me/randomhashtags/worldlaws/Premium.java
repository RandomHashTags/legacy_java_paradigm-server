package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypePremium;

public final class Premium implements WLServer {

    public static void main(String[] args) {
        new Premium().load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.PREMIUM;
    }

    @Override
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypePremium type = (ServerRequestTypePremium) request.getType();
        if(type == null) {
            return null;
        }
        switch (type) {
            case VERIFY:
                final String[] values = request.getTarget().split("/");
                switch (values[0]) {
                    case "apple":
                        return Verification.INSTANCE.verifyAppleAutoRenewableSubscription(values[1]);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
