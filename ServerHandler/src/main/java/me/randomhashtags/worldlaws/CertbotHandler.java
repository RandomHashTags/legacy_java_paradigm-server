package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum CertbotHandler {
    ;

    public static void generateCertificates() {
        final String letsencryptServer = "https://acme-v02.api.letsencrypt.org/directory";

        final String email = Settings.Server.Https.Certbot.getEmail(), folder = Settings.Server.Https.Certbot.getFolderName();
        final String command = "sudo certbot certonly --manual --preferred-challenges=dns --email " + email + " --server " + letsencryptServer + " --work-dir=" + folder + " --config-dir=" + folder + " --logs-dir=" + folder + " --agree-tos -d " + "\"***REMOVED***, *.***REMOVED***\"";
        WLUtilities.executeCommand(command, true);
    }
    public static void importCertificates() {
        generateP12("***REMOVED***");
    }

    public static void renewCertificates() {
        final String folder = Settings.Server.Https.Certbot.getFolderName();
        final String command = "certbot renew --work-dir=" + folder + " --config-dir=" + folder + " --logs-dir=" + folder;
        WLUtilities.executeCommand(command, true);
    }

    private static void generateP12(String domain) {
        final String certificate = getCertificatePath(domain), key = getPrivateKey(domain), fullChain = getFullChainPath(domain), p12 = domain + "Certificate.p12", password = Settings.Server.Https.Keystore.getPassword();
        if(certificate != null && key != null && fullChain != null) {
            deleteKey(p12);
            final String command = "openssl pkcs12 -export -in " + certificate + " -inkey " + key + " -out " + p12 + " -name " + domain + " -CAfile " + fullChain + " -caname \"Let's Encrypt Authority X3\" -password pass:" + password;
            WLUtilities.executeCommand(command, true);
            generateKeystore(domain, password);
        } else {
            WLLogger.logWarning("CertbotHandler - failed to generate P12 for domain \"" + domain + "\"! certificate=" + certificate + ",key=" + key + ",fullChain=" + fullChain);
        }
    }
    private static void generateKeystore(String domain, String password) {
        final String p12 = domain + "Certificate.p12", keystore = Settings.Server.Https.Keystore.getFileName();
        deleteKey(keystore);
        final String command = "keytool -importkeystore -deststorepass " + password + " -destkeypass " + password + " -deststoretype JKS -srckeystore " + p12 + " -srcstoretype PKCS12 -srcstorepass " + password + " -destkeystore " + keystore;
        WLUtilities.executeCommand(command, true);
    }

    private static void deleteKey(String domainPath) {
        final Path path = Paths.get(domainPath);
        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private static String getPrivateKey(String domain) {
        return getKeyPath(domain, "privkey");
    }
    private static String getCertificatePath(String domain) {
        return getKeyPath(domain, "cert");
    }
    private static String getFullChainPath(String domain) {
        return getKeyPath(domain, "fullchain");
    }
    private static String getKeyPath(String domain, String fileName) {
        final String folderName = Settings.Server.Https.Certbot.getFolderName();
        final String targetPath = folderName + File.separator + "live" + File.separator + domain + File.separator + fileName + ".pem";
        final Path path = Paths.get(targetPath);
        return Files.exists(path) ? targetPath : null;
    }
}
