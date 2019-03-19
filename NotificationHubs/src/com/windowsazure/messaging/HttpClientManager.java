package com.windowsazure.messaging;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.client.config.RequestConfig;

public class HttpClientManager {

    private static CloseableHttpAsyncClient httpAsyncClient;

    // A timeout value of zero is interpreted as an infinite timeout.
    // A negative value is interpreted as undefined (system default).
    // https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.html#getConnectionRequestTimeout()

    // The timeout in milliseconds used when requesting a connection from the connection manager.
    private static int connectionRequestTimeout = -1;

    // The timeout in milliseconds until a connection is established.
    private static int connectionTimeout = -1;

    // The socket timeout in milliseconds, which is the timeout for waiting for data or,
    // put differently, a maximum period inactivity between two consecutive data packets.
    private static int socketTimeout = -1;

    public static CloseableHttpAsyncClient initializeHttpAsyncClient() {
        synchronized (HttpClientManager.class) {
            if (httpAsyncClient == null) {
                RequestConfig config = RequestConfig.custom()
                        .setConnectionRequestTimeout(connectionRequestTimeout)
                        .setConnectTimeout(connectionTimeout)
                        .setSocketTimeout(socketTimeout)
                        .build();
                CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create()
                        .setDefaultRequestConfig(config)
                        .build();
                client.start();
                httpAsyncClient = client;
            } else {
                throw new RuntimeException("initializeHttpAsyncClient cannot be called twice or after setHttpAsyncClient.");
            }
        }
        return httpAsyncClient;
    }

    public static CloseableHttpAsyncClient getHttpAsyncClient() {
        return httpAsyncClient;
    }

    public static void setHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient) {
        synchronized (HttpClientManager.class) {
            if (HttpClientManager.httpAsyncClient == null) {
                HttpClientManager.httpAsyncClient = httpAsyncClient;
            } else {
                throw new RuntimeException("Cannot setHttpAsyncClient after having previously set, or after default already initialized.");
            }
        }
    }

    // Sets the timeout in milliseconds used when requesting a connection from the connection manager.
    public static void setConnectionRequestTimeout(int timeout) {
        if (HttpClientManager.httpAsyncClient == null) {
            connectionRequestTimeout = timeout;
        } else {
            throw new RuntimeException("Set timeout preference only before initializeHttpAsyncClient.");
        }
    }

    // Sets the timeout in milliseconds until a connection is established.
    public static void setConnectTimeout(int timeout) {
        if (HttpClientManager.httpAsyncClient == null) {
            connectionTimeout = timeout;
        } else {
            throw new RuntimeException("Set timeout preference only before initializeHttpAsyncClient.");
        }
    }

    // Sets the timeout in milliseconds for waiting for data or,
    // put differently, a maximum period inactivity between two consecutive data packets.
    public static void setSocketTimeout(int timeout) {
        if (HttpClientManager.httpAsyncClient == null) {
            socketTimeout = timeout;
        } else {
            throw new RuntimeException("Set timeout preference only before initializeHttpAsyncClient.");
        }
    }
}
