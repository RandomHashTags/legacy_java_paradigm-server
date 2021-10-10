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
            case "bug_report":
                setFileJSON(Folder.FEEDBACK_BUG_REPORTS, fileName, "{\"text\":\"test\"}");
                break;
            case "feature_request":
                setFileJSON(Folder.FEEDBACK_FEATURE_REQUEST, fileName, "{\"text\":\"test\"}");
                break;
            default:
                break;
        }
        handler.handleString("{\"recorded\":true}");
    }

    @Override
    public String[] getHomeRequests() {
        return null;
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
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
}
