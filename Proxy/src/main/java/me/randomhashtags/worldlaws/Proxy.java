package me.randomhashtags.worldlaws;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.logging.Level;

public final class Proxy implements RestAPI {

    public static void main(String[] args) {
        new Proxy();
    }

    private Proxy() {
        setupServer(false);
    }

    private void setupServer(boolean https) {
        final int port = DataValues.WL_PROXY_PORT;
        if(https) {
            setupHttpsServer(port);
        } else {
            setupHttpServer(port);
        }
    }

    private void connectClients(ServerSocket server, boolean https) throws Exception {
        WLLogger.log(Level.INFO, "Proxy - Listening for clients on port " + DataValues.WL_PROXY_PORT + "...");
        while (true) {
            new ProxyClient(server.accept()).start();
        }
    }

    private void setupHttpServer(int port) {
        try {
            connectClients(new ServerSocket(port), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupHttpsServer(int port) {
        try {
            final SSLContext context = getHttpsContext();
            final SSLServerSocketFactory socketFactory = context.getServerSocketFactory();
            final SSLServerSocket server = (SSLServerSocket) socketFactory.createServerSocket(port);
            connectClients(server, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private SSLContext getHttpsContext() {
        try {
            final KeyStore store = KeyStore.getInstance("JKS");
            final char[] password = {};//DataValues.HTTPS_KEYSTORE_PASSWORD.toCharArray();
            final String factoryType = "SunX509";
            store.load(new FileInputStream("eli5.keystore"), password);

            final KeyManagerFactory factory = KeyManagerFactory.getInstance(factoryType);
            factory.init(store, password);
            final KeyManager[] keyManagers = factory.getKeyManagers();

            final TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(factoryType);
            trustFactory.init(store);
            final TrustManager[] trustManagers = trustFactory.getTrustManagers();

            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(keyManagers, trustManagers, null);
            return context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
        final TargetServer server = TargetServer.valueOfBackendID(key);
        if(server != null) {
            final int length = key.length();
            final String target = value.length() == length ? "" : value.substring(length+1);
            server.sendResponse(RequestMethod.POST, target, handler);
        }
    }
}
