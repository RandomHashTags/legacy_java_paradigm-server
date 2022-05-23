package me.randomhashtags.worldlaws;

import com.sun.net.httpserver.HttpsServer;
import me.randomhashtags.worldlaws.garbage.JavaUtil;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WLUtilities {
    public static final String SERVER_EMPTY_JSON_RESPONSE = "{}";

    static {
        final TrustManager[] trustManager = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
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
        final URLConnection connection = link.openConnection();
        connection.setConnectTimeout(10_000);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", RestAPI.USER_AGENT);
        int responseCode = 200;
        if(connection instanceof HttpsURLConnection) {
            final HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            httpsConnection.setRequestMethod(RequestMethod.GET.name());
            httpsConnection.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
            responseCode = httpsConnection.getResponseCode();
        }

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

    public static Timer getTimer(LocalDateTime startingDate, long interval, Runnable runnable) {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(runnable != null) {
                    runnable.run();
                }
            }
        };
        if(startingDate == null) {
            timer.scheduleAtFixedRate(timerTask, interval, interval);
        } else {
            final long targetTime = startingDate.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            final Date targetDate = new Date(targetTime);
            timer.scheduleAtFixedRate(timerTask, targetDate, interval);
        }
        return timer;
    }

    public static String executeCommand(String command, boolean printToTerminal) {
        final Runtime runtime = Runtime.getRuntime();
        final StringBuilder builder = new StringBuilder();
        try {
            final Process p = runtime.exec(command.split(", "));
            p.waitFor();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(builder.length() == 0 ? "" : "\n").append(line);
                if(printToTerminal) {
                    System.out.println(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        WLLogger.logInfo("WLUtilities - executed command \"" + command + "\"");
        return builder.toString();
    }

    public static Month valueOfMonthFromInput(String input) {
        if(input.length() < 3) {
            return null;
        }
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

    public static long parseDateFormatToMilliseconds(DateTimeFormatter formatter, String input) {
        if(input.isEmpty()) {
            return 0;
        }
        try {
            final TemporalAccessor time = formatter.parse(input);
            return time.query(Instant::from).toEpochMilli();
        } catch (Exception e) {
            final String trace = WLUtilities.getThrowableStackTrace(e);
            WLUtilities.saveLoggedError("WLUtilities", "failed to parse date format for input \"" + input + "\"!\n\n" + trace);
            return 0;
        }
    }
    public static long getTodayStartOfDayMilliseconds() {
        return Instant.now().atZone(ZoneId.of(ZoneOffset.UTC.getId())).withSecond(0).withHour(0).withMinute(0).toEpochSecond() * 1000;
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

    public static void saveException(Throwable exception) {
        final String trace = getThrowableStackTrace(exception);
        final String errorName = exception.getClass().getSimpleName();
        saveError("WLUtilities.saveException", errorName, trace);
    }
    public static String getThrowableStackTrace(Throwable throwable) {
        if(throwable == null) {
            return "null throwable";
        }
        String message = throwable.getLocalizedMessage();
        if(message == null) {
            message = throwable.getMessage();
        }
        final StringBuilder builder = new StringBuilder(message != null && !message.isEmpty() ? message : "null message");
        for(StackTraceElement element : throwable.getStackTrace()) {
            builder.append("\n").append(element.toString());
        }
        return builder.toString();
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
        Jsonable.saveFile(sender, folder, fileName, value, "txt", false, true);
    }

    public static String getElapsedTime(long started) {
        return getElapsedTimeFromMilliseconds(System.currentTimeMillis()-started);
    }
    public static String getElapsedTimeFromMilliseconds(long elapsedMilliseconds) {
        long elapsedSeconds = elapsedMilliseconds / 1000;
        elapsedMilliseconds -= elapsedSeconds * 1000;
        final long elapsedMinutes = elapsedSeconds / 60;
        elapsedSeconds -= elapsedMinutes * 60;
        return (elapsedMinutes > 0 ? elapsedMinutes + "m" : "") + (elapsedSeconds > 0 ? elapsedSeconds + "s" : "") + elapsedMilliseconds + "ms";
    }

    public static JSONObject translateJSON(JSONTranslatable json, LanguageTranslator translator, Language clientLanguage) {
        final boolean isNotEnglish = clientLanguage != Language.ENGLISH;
        final String localeKey = "locale", translatorID = translator.getID(), languageID = clientLanguage.getID();
        if(isNotEnglish && json != null && !json.isEmpty()) {
            if(json instanceof JSONObjectTranslatable) {
                final JSONObjectTranslatable translatable = (JSONObjectTranslatable) json;
                translatable.updateIfNeeded(translator, clientLanguage);
            }
        }
        final JSONObject jsonClone = json != null ? new JSONObject(json.toString()) : null;
        if(jsonClone != null) {
            if(isNotEnglish) {
                JSONTranslatable.insertTranslations(jsonClone, translatorID, languageID);
            }
            jsonClone.remove(localeKey);

            final HashSet<String> removedKeys = json.getRemovedClientKeys();
            if(removedKeys != null) {
                for(String key : removedKeys) {
                    jsonClone.remove(key);
                }
            }
        }
        return jsonClone;
    }

    public static HttpsServer getHttpsServer(int port) {
        return JavaUtil.getHttpsServer(port);
    }
}
