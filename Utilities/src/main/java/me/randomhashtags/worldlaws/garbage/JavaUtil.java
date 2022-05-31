package me.randomhashtags.worldlaws.garbage;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.request.WLServerSocket;
import me.randomhashtags.worldlaws.settings.Settings;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public abstract class JavaUtil {
    public static HttpsServer getHttpsServer(int port) {
        HttpsServer server = null;
        try {
            final SSLContext context = WLServerSocket.getSSLContext();
            server = HttpsServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(Settings.Server.getProxyClientThreadPoolSize()));
            server.setHttpsConfigurator(new HttpsConfigurator(context) {
                @Override
                public void configure(HttpsParameters params) {
                    try {
                        final SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(true);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        final SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);
                    } catch (Exception e) {
                        WLUtilities.saveException(e);
                    }
                }
            });
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        return server;
    }
}
