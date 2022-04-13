package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

public enum ServerRequestTypeFeedback implements ServerRequestType {
    BUG_REPORTS,
    FEATURE_REQUESTS,
    SUBMIT,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        return httpExchange -> {
            final String[] values = httpExchange.getShortPathValues();
            switch (this) {
                case SUBMIT:
                    final String fileName = Long.toString(System.currentTimeMillis());
                    switch (values[0]) {
                        case "bug_report":
                            return submit(Folder.FEEDBACK_BUG_REPORTS, fileName, values[1]);
                        case "feature_request":
                            return submit(Folder.FEEDBACK_FEATURE_REQUEST, fileName, values[1]);
                        default:
                            return null;
                    }
                case BUG_REPORTS:
                    return getAllBugReports();
                case FEATURE_REQUESTS:
                    return getAllFeatureRequests();
                default:
                    return null;
            }
        };
    }

    @Override
    public boolean isConditional() {
        return this == SUBMIT;
    }

    private JSONObjectTranslatable submit(Folder folder, String fileName, String value) {
        setFileJSON(folder, fileName, value);
        return null;
    }
    private JSONObjectTranslatable getAllBugReports() {
        return null;
    }
    private JSONObjectTranslatable getAllFeatureRequests() {
        return null;
    }
}
