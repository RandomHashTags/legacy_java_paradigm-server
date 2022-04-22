package me.randomhashtags.worldlaws.request;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.settings.Settings;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public final class WLServerSocket {

    private final ServerSocket server;
    private final HashMap<String, WLHttpHandler> handlers;

    public WLServerSocket(int port, boolean https) throws Exception {
        handlers = new HashMap<>();
        if(https) {
            final SSLContext context = getSSLContext();
            final SSLServerSocketFactory socketFactory = context.getServerSocketFactory();
            server = socketFactory.createServerSocket(port);
        } else {
            server = new ServerSocket(port);
        }
        connectClients(https);
    }

    private void connectClients(boolean https) {
        WLLogger.logInfo("WLServerSocket - Listening for http" + (https ? "s" : "") + " clients on port " + server.getLocalPort() + "...");
        try {
            while (!server.isClosed()) {
                final Socket client = server.accept();
                CompletableFuture.runAsync(() -> {
                    if(https) {
                        handleHttpsClient(((SSLSocket) client));
                    } else {
                        handleClient(client);
                    }
                });
            }
        } catch (Exception e) {
            WLLogger.logInfo("WLServerSocket - stopped listening for clients");
        }
    }

    private void handleHttpsClient(SSLSocket client) {
        client.setEnabledCipherSuites(client.getSupportedCipherSuites());
        try {
            client.startHandshake();
            handleClient(client);
        } catch (SocketException | SSLException ignored) {
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void handleClient(Socket client) {
        final ClientHeaders clientHeaders = ClientHeaders.getFrom(client);
        final boolean productionMode = true;
        if(productionMode == clientHeaders.isValidRequest()) {
            final String identifier = clientHeaders.getIdentifier(), originalRequest = clientHeaders.getOriginalRequest();
            if(handlers.containsKey(originalRequest)) {
                //handlers.get(originalRequest).handle(clientHeaders);
            } else {
                //WLUtilities.writeClientOutput(client, DataValues.HTTP_ERROR_404);
            }
        } else {
            //WLUtilities.writeClientOutput(client, DataValues.HTTP_ERROR_404);
        }
    }

    public static SSLContext getSSLContext() throws Exception {
        final KeyStore store = KeyStore.getInstance("JKS");
        final char[] password = Settings.Server.Https.Keystore.getPassword().toCharArray();

        final String keystoreFileName = Settings.Server.Https.Keystore.getFileName() + ".keystore";
        final FileInputStream file = new FileInputStream(keystoreFileName);
        store.load(file, password);

        final String factoryType = "SunX509";
        final KeyManagerFactory factory = KeyManagerFactory.getInstance(factoryType);
        factory.init(store, password);

        final TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(factoryType);
        trustFactory.init(store);

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(factory.getKeyManagers(), trustFactory.getTrustManagers(), null);
        return context;
    }

    public void createContext(String path, WLHttpHandler handler) {
        handlers.put(path, handler);
    }
}
