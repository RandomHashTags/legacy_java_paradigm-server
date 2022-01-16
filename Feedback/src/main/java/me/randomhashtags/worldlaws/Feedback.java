package me.randomhashtags.worldlaws;

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
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        final String fileName = Long.toString(System.currentTimeMillis());
        switch (key) {
            case "submit":
                switch (values[1]) {
                    case "bug_report":
                        return submit(Folder.FEEDBACK_BUG_REPORTS, fileName, values[2]);
                    case "feature_request":
                        return submit(Folder.FEEDBACK_FEATURE_REQUEST, fileName, values[2]);
                    default:
                        return null;
                }
            case "bug_reports":
                return getAllBugReports();
            case "feature_requests":
                return getAllFeatureRequests();
            default:
                return null;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return null;
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
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
