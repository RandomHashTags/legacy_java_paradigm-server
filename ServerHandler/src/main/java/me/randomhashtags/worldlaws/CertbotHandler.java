package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONArray;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum CertbotHandler {
    ;

    public static void generateCertificates() {
        final String letsencryptServer = "https://acme-v02.api.letsencrypt.org/directory";

        final JSONArray domainsArray = Settings.Server.Https.getDomains();
        final String email = Settings.Server.Https.Certbot.getEmail(), folder = Settings.Server.Https.Certbot.getFolderName();
        final StringBuilder domains = new StringBuilder();
        boolean isFirst = true;
        for(Object domainObj : domainsArray) {
            final String domain = (String) domainObj;
            if(!isFirst) {
                domains.append(",");
            }
            domains.append(domain);
            isFirst = false;
        }
        final String command = "sudo certbot certonly --manual --preferred-challenges=dns --email " + email + " --server " + letsencryptServer + " --work-dir=" + folder + " --config-dir=" + folder + " --logs-dir=" + folder + " --agree-tos -d " + domains.toString();
        WLUtilities.executeCommand(command, true);
    }

    private void renewCertificates() {
        final String folder = Settings.Server.Https.Certbot.getFolderName();
        final String command = "certbot renew -q --work-dir=" + folder + " --config-dir=" + folder + " --logs-dir=" + folder;
        WLUtilities.executeCommand(command, true);
    }

    private void generateP12(String domain) {
        final String certificate = ".crt", key = getKeyPath(domain) + ".key", p12 = ".p12";
        final String command = "openssl pkcs12 -export -in " + certificate + " -inkey " + key + " -out " + p12;
        WLUtilities.executeCommand(command, true);
        generateKeystore();
    }
    private void generateKeystore() {
        final String p12 = Settings.Server.Https.P12.getFileName() + ".p12", keystore = Settings.Server.Https.Keystore.getFileName() + ".keystore";
        final String command = "keytool -importkeystore -srckeystore " + p12 + " -srcstoretype PKCS12 -destkeystore " + keystore + " -deststoretype JKS";
        WLUtilities.executeCommand(command, true);
    }

    private String getKeyPath(String domain) {
        final String certbot = "certbot";
        final String prefix = Jsonable.USER_DIR + certbot + File.separator + "archive" + File.separator + domain + File.separator, name = "privkey", extension = ".pem";
        for(int i = 1000; i > 0; i--) {
            final String targetPath = prefix + name + i + extension;
            final Path path = Paths.get(targetPath);
            if(Files.exists(path)) {
                return targetPath;
            }
        }
        return null;
    }
}
