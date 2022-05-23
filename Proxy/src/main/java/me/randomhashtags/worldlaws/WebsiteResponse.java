package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.WLContentType;

import java.io.File;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class WebsiteResponse {

    private final WLContentType contentType;
    private final String response;

    public WebsiteResponse(String targetPath) {
        if(targetPath.startsWith("/")) {
            targetPath = targetPath.substring(1);
        }
        if(targetPath.isEmpty()) {
            targetPath = "index";
        } else {
            switch (targetPath) {
                case "index":
                    targetPath = null;
                    break;
                default:
                    break;
            }
        }
        String string = null;
        WLContentType contentType = null;
        if(targetPath != null) {
            final String[] extensions = targetPath.contains(".") && !targetPath.endsWith(".html") ? targetPath.split("\\.") : null;
            final String extension = extensions != null ? extensions[extensions.length-1] : "html";
            contentType = WLContentType.valueOfInput(extension);
            string = getFileString(targetPath, extension);
            if(string == null) {
                contentType = WLContentType.HTML;
            }
        }
        if(contentType == null) {
            contentType = WLContentType.HTML;
        }
        this.contentType = contentType;

        if(string == null) {
            string = ProxyHttpHandler.getHTMLErrorMsg(HttpURLConnection.HTTP_NOT_FOUND);
        }

        switch (contentType) {
            case HTML:
                break;
            default:
                break;
        }
        response = string;
    }

    public WLContentType getContentType() {
        return contentType;
    }
    public String getResponse() {
        return response;
    }

    private static String getFileString(String targetPath, String extension) {
        final String target = Jsonable.CURRENT_FOLDER + "_html" + File.separator + targetPath.replace("/", File.separator).replaceFirst("\\." + extension, "") + "." + extension;
        final Path path = Paths.get(target);
        if(Files.exists(path)) {
            try {
                return Files.readString(path);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
