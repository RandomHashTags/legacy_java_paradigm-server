package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

public abstract class WLUtilities {
    //public static final long LAWS_HOME_RESPONSE_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(30);
    public static final long PROXY_HOME_RESPONSE_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    public static final long UPCOMING_EVENTS_HOME_UPDATE_INTERVAL = TimeUnit.HOURS.toMillis(1);
    public static final long UPCOMING_EVENTS_TV_SHOW_UPDATE_INTERVAL = TimeUnit.DAYS.toMillis(1);
    public static final long UPCOMING_EVENTS_TV_SHOW_NAMES_UPDATE_INTERVAL = TimeUnit.DAYS.toMillis(7);
    public static final long WEATHER_ALERTS_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    public static final long WEATHER_EARTHQUAKES_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(30);
    public static final long WEATHER_NASA_WEATHER_EVENT_TRACKER_UPDATE_INTERVAL = TimeUnit.HOURS.toMillis(1);

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static final String SERVER_EMPTY_JSON_RESPONSE = "{}";

    static {
        final TrustManager[] trustManager = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
        try {
            final SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(null, trustManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String host, SSLSession session) {
                    WLLogger.logError(this, "static{};HttpsURLConnection.setDefaultHostnameVerifier;verify;host=" + host + ";returned true");
                    return true;
                }
            });
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }

    public static Document getJsoupDocumentFrom(String url) throws Exception {
        final URL link = new URL(url);
        final HttpsURLConnection connection = (HttpsURLConnection) link.openConnection();
        connection.setConnectTimeout(10_000);
        connection.setRequestMethod(RequestMethod.GET.name());
        connection.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        final int responseCode = connection.getResponseCode();
        if(responseCode >= 200 && responseCode < 400) {
            String line = null;
            final StringBuilder builder = new StringBuilder();
            final InputStream is = connection.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while((line = reader.readLine()) != null)  {
                builder.append(line);
            }
            reader.close();
            return Jsoup.parse(builder.toString());
        } else {
            WLLogger.logError("WLUtilities", "getJsoupDocumentFrom - invalid response code (" + responseCode + ") for url \"" + url + "\"!");
            return null;
        }
    }

    public static Month valueOfMonthFromInput(String input) {
        input = input.toLowerCase().substring(0, 3);
        switch (input) {
            case "jan": return Month.JANUARY;
            case "feb": return Month.FEBRUARY;
            case "mar": return Month.MARCH;
            case "apr": return Month.APRIL;
            case "may": return Month.MAY;
            case "jun": return Month.JUNE;
            case "jul": return Month.JULY;
            case "aug": return Month.AUGUST;
            case "sep": return Month.SEPTEMBER;
            case "oct": return Month.OCTOBER;
            case "nov": return Month.NOVEMBER;
            case "dec": return Month.DECEMBER;
            default: return null;
        }
    }
    public static LocalDate getNow() { // TODO: convert all LocalDate.now requests to this method - and convert it to use current UTC system time
        return LocalDate.now();
    }
    public static int getTodayYear() {
        return getNow().getYear();
    }

    public static LocalDate getNowUTC() {
        return LocalDate.now(Clock.systemUTC());
    }

    public static void saveException(Exception exception) {
        String message = exception.getLocalizedMessage();
        if(message == null) {
            message = exception.getMessage();
        }
        final StringBuilder builder = new StringBuilder(message != null ? message : "null message");
        for(StackTraceElement element : exception.getStackTrace()) {
            builder.append("\n").append(element.toString());
        }
        final String errorName = exception.getClass().getSimpleName();
        saveError("WLUtilities.saveException", errorName, builder.toString());
    }
    public static void saveLoggedError(String folderName, String value) {
        saveError("WLUtilities.saveLoggedError", "LoggedError" + File.separator + folderName, value);
    }
    private static void saveError(String sender, String folderName, String value) {
        final Folder folder = Folder.ERRORS;
        final String fileName = LocalDateTime.now().toString();
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%errorName%", folderName));
        saveErrorToFile(sender, folder, fileName, value);
    }
    private static void saveErrorToFile(String sender, Folder folder, String fileName, String value) {
        Jsonable.saveFile(sender, Level.ERROR, folder, fileName, value, "txt");
    }
}
