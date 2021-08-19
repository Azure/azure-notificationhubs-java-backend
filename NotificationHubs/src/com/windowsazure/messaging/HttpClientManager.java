//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.util.Timeout;

public class HttpClientManager {

    private static CloseableHttpAsyncClient httpAsyncClient;

    // A timeout value of zero is interpreted as an infinite timeout.
    // A negative value is interpreted as undefined (system default).
    // https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/config/RequestConfig.html#getConnectionRequestTimeout()

    // The timeout in milliseconds used when requesting a connection from the connection manager.
    private static int connectionRequestTimeout = -1;

    // The timeout in milliseconds until a connection is established.
    private static int connectionTimeout = -1;

    private static void initializeHttpAsyncClient() {
        synchronized (HttpClientManager.class) {
            if (httpAsyncClient == null) {
                RequestConfig config = RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
                    .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
                    .build();

                CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                    .setDefaultRequestConfig(config)
                    .setRetryStrategy(BasicRetryStrategy.INSTANCE)
                    .build();

                client.start();
                httpAsyncClient = client;
            }
        }
    }

    public static CloseableHttpAsyncClient getHttpAsyncClient() {
        if (httpAsyncClient == null) {
            initializeHttpAsyncClient();
        }
        return httpAsyncClient;
    }

    public static void setHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient) {
        synchronized (HttpClientManager.class) {
            if (HttpClientManager.httpAsyncClient == null) {
                HttpClientManager.httpAsyncClient = httpAsyncClient;
            } else {
                throw new RuntimeException("Cannot setHttpAsyncClient after having previously set, or after default already initialized from earlier call to getHttpAsyncClient.");
            }
        }
    }

    // Sets the timeout in milliseconds used when requesting a connection from the connection manager.
    public static void setConnectionRequestTimeout(int timeout) {
        if (HttpClientManager.httpAsyncClient == null) {
            connectionRequestTimeout = timeout;
        } else {
            throw new RuntimeException("Cannot setConnectionRequestTimeout after previously setting httpAsyncClient, or after default already initialized from earlier call to getHttpAsyncClient.");
        }
    }

    // Sets the timeout in milliseconds until a connection is established.
    public static void setConnectTimeout(int timeout) {
        if (HttpClientManager.httpAsyncClient == null) {
            connectionTimeout = timeout;
        } else {
            throw new RuntimeException("Cannot setConnectTimeout after previously setting httpAsyncClient, or after default already initialized from earlier call to getHttpAsyncClient.");
        }
    }
}
