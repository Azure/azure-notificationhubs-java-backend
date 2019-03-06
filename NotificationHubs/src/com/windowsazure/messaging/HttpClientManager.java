package com.windowsazure.messaging;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.client.config.RequestConfig;

public class HttpClientManager {

    private static CloseableHttpAsyncClient httpAsyncClient;

    // A timeout value of zero is interpreted as an infinite timeout.
    // A negative value is interpreted as undefined (system default).

    // The timeout in milliseconds used when requesting a connection from the connection manager.
    private static int ConnectionRequestTimeout = -1;

    // The timeout in milliseconds until a connection is established.
    private static int ConnectionTimeout = -1;

    // The socket timeout in milliseconds, which is the timeout for waiting for data or,
    // put differently, a maximum period inactivity between two consecutive data packets.
    private static int SocketTimeout = -1;

    public static CloseableHttpAsyncClient getHttpAsyncClient() {
        if (httpAsyncClient == null) {
            synchronized (HttpClientManager.class) {
                if (httpAsyncClient == null) {
                    RequestConfig config = RequestConfig.custom()
                            .setConnectionRequestTimeout(ConnectionRequestTimeout)
                            .setConnectTimeout(ConnectionTimeout)
                            .setSocketTimeout(SocketTimeout)
                            .build();
                    CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create()
                            .setDefaultRequestConfig(config)
                            .build();
                    client.start();
                    httpAsyncClient = client;
                }
            }
        }
        return httpAsyncClient;
    }

    public static void setHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient) {
        synchronized (HttpClientManager.class) {
            if (HttpClientManager.httpAsyncClient == null) {
                HttpClientManager.httpAsyncClient = httpAsyncClient;
            }
            else {
                throw new RuntimeException("HttpAsyncClient was already set before or default one is being used.");
            }
        }
    }

    public static void setConnectionRequestTimeout(int timeout) {
        ConnectionRequestTimeout = timeout;
    }

    public static void setConnectTimeout(int timeout) {
        ConnectionTimeout = timeout;
    }

    public static void setSocketTimeout(int timeout) {
        SocketTimeout = timeout;
    }
}
