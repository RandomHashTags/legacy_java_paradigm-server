package me.randomhashtags.worldlaws;

import com.sun.net.httpserver.HttpServer;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpExchange;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.settings.Settings;
import org.apache.commons.text.StringEscapeUtils;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LocalServer implements UserServer, DataValues, RestAPI {
    private final WLServer wlserver;
    private final String serverName;
    private HttpServer server;
    private HashSet<Timer> timers;

    private ConcurrentHashMap<String, Integer> totalRequests;
    private ConcurrentHashMap<String, HashSet<String>> uniqueRequests;
    private HashSet<String> totalUniqueIdentifiers;

    public LocalServer(WLServer server) {
        wlserver = server;
        final TargetServer targetServer = server.getServer();
        this.serverName = targetServer.getName();
    }

    @Override
    public void start() {
        uniqueRequests = new ConcurrentHashMap<>();
        totalRequests = new ConcurrentHashMap<>();
        totalUniqueIdentifiers = new HashSet<>();
        final TargetServer server = wlserver.getServer();
        if(server.recordsStatistics()) {
            final long interval = UpdateIntervals.SAVE_STATISTICS;
            registerFixedTimer(interval, this::saveStatistics);
        }
        registerFixedTimer(UpdateIntervals.REFRESH_SETTINGS, Settings::refresh);
        setupHttpServer(server.getPort());
    }

    @Override
    public void stop() {
        final long started = System.currentTimeMillis();
        WLLogger.logInfo(serverName + " - shutting down server...");
        if(timers != null) {
            for(Timer timer : timers) {
                timer.cancel();
            }
        }
        wlserver.stop();
        if(wlserver.getServer().recordsStatistics()) {
            saveStatistics();
        }
        try {
            server.stop(0);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        WLLogger.logInfo(serverName + " - server has shut down (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public void registerFixedTimer(long interval, Runnable runnable) {
        registerFixedTimer(null, interval, runnable);
    }
    public void registerFixedTimerStartingAt(LocalDateTime startingDate, long interval, Runnable runnable) {
        registerFixedTimer(startingDate, interval, runnable);
    }
    private void registerFixedTimer(LocalDateTime startingDate, long interval, Runnable runnable) {
        if(timers == null) {
            timers = new HashSet<>();
        }
        final Timer timer = WLUtilities.getTimer(startingDate, interval, runnable);
        timers.add(timer);
    }

    private ServerRequestType[] getRequestTypes() {
        return wlserver.getRequestTypes();
    }

    public void madeRequest(String identifier, String target) {
        if(wlserver.getServer().recordsStatistics() && !Settings.Server.getUUID().equals(identifier)) {
            uniqueRequests.putIfAbsent(target, new HashSet<>());
            uniqueRequests.get(target).add(identifier);
            totalRequests.put(target, totalRequests.getOrDefault(target, 0) + 1);
            totalUniqueIdentifiers.add(identifier);
        }
    }
    @Override
    public void saveStatistics() {
        Statistics.INSTANCE.save(wlserver.getServer(), totalUniqueIdentifiers, uniqueRequests, totalRequests);
        totalRequests.clear();
        totalUniqueIdentifiers.clear();
        uniqueRequests.clear();
    }

    public HttpServer getServer() {
        return server;
    }
    private void setupHttpServer(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            createServerContexts();
            final WLHttpHandler defaultHandler = wlserver.getDefaultHandler();
            if(defaultHandler != null) {
                server.createContext("/", defaultHandler);
            }
            final ServerRequestType[] requestTypes = getRequestTypes();
            if(requestTypes != null) {
                final APIVersion[] apiVersions = APIVersion.values();
                for(APIVersion apiVersion : apiVersions) {
                    final String prefix = getContextPrefix(apiVersion);
                    final WLHttpHandler homeHandler = getHomeHandler(apiVersion);
                    server.createContext(prefix + "home", homeHandler);
                }
                final HashMap<APIVersion, List<ServerRequestType>> conditionals = new HashMap<>();
                for(ServerRequestType type : requestTypes) {
                    for(APIVersion apiVersion : apiVersions) {
                        final String prefix = getContextPrefix(apiVersion);
                        if(type.isConditional()) {
                            conditionals.putIfAbsent(apiVersion, new ArrayList<>());
                            conditionals.get(apiVersion).add(type);
                        } else {
                            final WLHttpHandler handler = type.getWLHttpHandler(this, apiVersion);
                            if(handler != null) {
                                server.createContext(prefix + type.name().toLowerCase(), handler);
                            }
                        }
                    }
                }
                if(!conditionals.isEmpty()) {
                    for(Map.Entry<APIVersion, List<ServerRequestType>> entry : conditionals.entrySet()) {
                        final APIVersion apiVersion = entry.getKey();
                        final String prefix = "/" + apiVersion.name() + "/";
                        final List<ServerRequestType> types = entry.getValue();
                        if(conditionals.containsKey(apiVersion)) {
                            for(ServerRequestType type : types) {
                                final String typeNameLowercase = type.name().toLowerCase();
                                final WLHttpHandler handler = type.getWLHttpHandler(this, apiVersion);
                                server.createContext(prefix + typeNameLowercase, handler);
                            }
                        }
                    }
                }
            }
            server.setExecutor(null);
            server.start();
            WLLogger.logInfo(serverName + " - Listening for clients on port " + port + "...");
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private String getContextPrefix(APIVersion apiVersion) {
        return "/" + apiVersion.name() + "/";
    }
    private void createServerContexts() {
        final String[] paths = { "ping", "stop", "refresh" };
        for(String path : paths) {
            server.createContext("/" + path, new WLHttpHandler() {
                @Override
                public JSONTranslatable getResponse(WLHttpExchange httpExchange) {
                    return null;
                }

                @Override
                public String getFallbackResponse(WLHttpExchange httpExchange) {
                    final String identifier = httpExchange.getIdentifier();
                    switch (path) {
                        case "ping":
                            return "1";
                        case "stop":
                            if(Settings.Server.getUUID().equals(identifier)) {
                                stop();
                                return "1";
                            } else {
                                return null;
                            }
                        case "refresh":
                            if(Settings.Server.getUUID().equals(identifier)) {
                                Settings.refresh();
                                return "1";
                            } else {
                                return null;
                            }
                        default:
                            return null;
                    }
                }
            });
        }
    }
    private WLHttpHandler getHomeHandler(APIVersion version) {
        return httpExchange -> {
            if(!Settings.Server.getUUID().equals(httpExchange.getIdentifier())) {
                return null;
            }
            final ServerRequest[] types = wlserver.getHomeRequests();
            if(types == null) {
                return null;
            }
            if(WLServer.CACHED_HOME_RESPONSES.containsKey(version)) {
                return WLServer.CACHED_HOME_RESPONSES.get(version);
            } else {
                tryStartingAutoUpdates(version);
                return wlserver.refreshHome(version, httpExchange.getIdentifier(), httpExchange.getPlatform(), httpExchange.getVersion());
            }
        };
    }
    private void tryStartingAutoUpdates(APIVersion version) {
        final long updateInterval = wlserver.getHomeResponseUpdateInterval();
        if(updateInterval > 0) {
            final TargetServer server = wlserver.getServer();
            final String serverName = server.getName(), simpleName = getClass().getSimpleName();
            registerFixedTimer(updateInterval, () -> {
                final long started = System.currentTimeMillis();
                wlserver.autoRefreshHome(simpleName, started, serverName, version);
            });
        }
    }

    public static String fixEscapeValues(String input) {
        return StringEscapeUtils.escapeJava(input);
    }
    public static String fixUnescapeValues(String input) {
        return StringEscapeUtils.unescapeJava(input);
    }
    public static String removeWikipediaTranslations(String input) {
        return input.replaceAll(" \\(.*?:.*?\\)", "");
    }
    public static String removeWikipediaReferences(String string) {
        return string != null && !string.isEmpty() ? string.replaceAll("\\[.*?]", "") : string;
    }

    public static String toCorrectCapitalization(String input, String...excludedWords) {
        final String lowercase = input.toLowerCase();
        final String[] values = lowercase.split("_");
        final StringBuilder builder = new StringBuilder();
        final List<String> excluded = new ArrayList<>();
        if(excludedWords != null) {
            for(String string : excludedWords) {
                excluded.add(string.toLowerCase());
            }
        }
        boolean first = true;
        for(String string : values) {
            builder.append(first ? "" : " ");
            if(first || !excluded.contains(string)) {
                builder.append(string.substring(0, 1).toUpperCase()).append(string.substring(1));
                first = false;
            } else {
                builder.append(string);
            }
        }
        return builder.toString();
    }
    public static String toCorrectCapitalization(String input, boolean onlyFirstWordIsCapitalized, String...excluded) {
        if(onlyFirstWordIsCapitalized) {
            String words = input.toLowerCase().replace("_", " ");
            for(String string : excluded) {
                words = words.replace(string.toLowerCase(), string);
            }
            return words.substring(0, 1).toUpperCase() + words.substring(1);
        } else {
            return toCorrectCapitalization(input, excluded);
        }
    }
}
