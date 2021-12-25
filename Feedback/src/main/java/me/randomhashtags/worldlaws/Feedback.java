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
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        final String fileName = Long.toString(System.currentTimeMillis());
        switch (key) {
            case "submit":
                switch (values[1]) {
                    case "bug_report":
                        submit(Folder.FEEDBACK_BUG_REPORTS, fileName, values[2], handler);
                        break;
                    case "feature_request":
                        submit(Folder.FEEDBACK_FEATURE_REQUEST, fileName, values[2], handler);
                        break;
                    default:
                        break;
                }
                break;
            case "bug_reports":
                handler.handleString(getAllBugReports());
                break;
            case "feature_requests":
                handler.handleString(getAllFeatureRequests());
                break;
            default:
                break;
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

    private void submit(Folder folder, String fileName, String value, CompletionHandler handler) {
        setFileJSON(folder, fileName, value);
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
