//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.Method;

import java.net.URI;

public abstract class NotificationHubsService {
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    protected static final String USER_AGENT_HEADER_NAME = "User-Agent";
    protected static final String USER_AGENT = "NHub/2020-06 (api-origin=JavaSDK;os=%s;os-version=%s)";
    protected static final String TRACKING_ID_HEADER = "TrackingId";

    protected SasTokenProvider tokenProvider;

    protected SimpleRequestBuilder createRequest(URI uri, Method method) {
        return addBaseHeaders(SimpleRequestBuilder.create(method), uri);
    }

    private SimpleRequestBuilder addBaseHeaders(SimpleRequestBuilder builder, URI uri) {
        final String trackingId = java.util.UUID.randomUUID().toString();

        return builder
            .setUri(uri)
            .setHeader(AUTHORIZATION_HEADER_NAME, tokenProvider.generateSasToken(uri))
            .setHeader(TRACKING_ID_HEADER, trackingId)
            .setHeader(USER_AGENT_HEADER_NAME, getUserAgent());
    }

    private static String getUserAgent() {
        String os = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return String.format(USER_AGENT, os, osVersion);
    }
}
