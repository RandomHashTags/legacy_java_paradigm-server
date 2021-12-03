package me.randomhashtags.worldlaws;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public final class Proxy implements UserServer, RestAPI {

    private static ServerSocket SOCKET;

    public static void main(String[] args) {
        new Proxy().start();
    }

    @Override
    public void start() {
        setupServer(false);
    }

    @Override
    public void stop() {
        try {
            SOCKET.close();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }

    private void setupServer(boolean https) {
        listenForUserInput();

        final int port = Jsonable.getSettingsJSON().getJSONObject("server").getInt("proxy_port");
        if(https) {
            setupHttpsServer(port);
        } else {
            setupHttpServer(port);
        }
    }
    private void connectClients(boolean https) throws Exception {
        WLLogger.logInfo("Proxy - Listening for http" + (https ? "s" : "") + " clients on port " + SOCKET.getLocalPort() + "...");
        try {
            while (true) {
                final Socket client = SOCKET.accept();
                if(!TargetServer.isMaintenanceMode()) {
                    new ProxyClient(client).start();
                }
            }
        } catch (Exception e) {
            WLLogger.logInfo("Proxy - stopped listening for clients");
        }
    }

    private void setupHttpServer(int port) {
        try {
            SOCKET = new ServerSocket(port);
            connectClients(false);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void setupHttpsServer(int port) {
        try {
            final SSLContext context = getHttpsContext();
            final SSLServerSocketFactory socketFactory = context.getServerSocketFactory();
            SOCKET = socketFactory.createServerSocket(port);
            connectClients(true);
        } catch (Exception e) {
            WLUtilities.saveException(e);
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
            WLUtilities.saveException(e);
        }
        return null;
    }


    private void getResponse(String value, CompletionHandler handler) {
        /*final String[] values = value.split("/");
        final String key = values[0];
        final TargetServer server = TargetServer.valueOfBackendID(key);
        if(server != null) {
            final int length = key.length();
            final String target = value.length() == length ? "" : value.substring(length+1);
            server.sendResponse(RequestMethod.POST, target, handler);
        }*/
    }
}
