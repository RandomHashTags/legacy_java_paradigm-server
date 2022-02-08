package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeFeedback;

public final class Feedback implements WLServer, Jsonable {

    public static void main(String[] args) {
        new Feedback();
    }

    private Feedback() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.FEEDBACK;
    }

    private void test() {
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeFeedback type = (ServerRequestTypeFeedback) request.getType();
        switch (type) {
            case SUBMIT:
                final String fileName = Long.toString(System.currentTimeMillis());
                final String[] values = request.getTarget().split("/");
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
    }

    private String submit(Folder folder, String fileName, String value) {
        setFileJSON(folder, fileName, value);
        return null;
    }

    private String getText(String[] headers) {
        final String target = "Text: ";
        for(String string : headers) {
            if(string.startsWith(target)) {
                return string.substring(target.length());
            }
        }
        return null;
    }

    private String getAllBugReports() {
        return null;
    }
    private String getAllFeatureRequests() {
        return null;
    }
}
