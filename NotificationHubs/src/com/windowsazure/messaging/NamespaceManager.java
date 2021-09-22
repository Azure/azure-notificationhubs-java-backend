//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * This interface represents the operations that can be performed by the Azure
 * Notification Hub management API.
 */
public class NamespaceManager extends NotificationHubsService implements NamespaceManagerClient {
    private static final String IF_MATCH_HEADER_NAME = "If-Match";
    private static final String HUBS_COLLECTION_PATH = "$Resources/NotificationHubs/";
    private static final String API_VERSION = "?api-version=2014-09";
    private static final String SKIP_TOP_PARAM = "&$skip=0&$top=2147483647";
    private String endpoint;

    /**
     * Creates a new instance of the NamespaceManager class.
     * @param connectionString The connection string from the Azure Notification Hubs namespace access policies.
     */
    public NamespaceManager(String connectionString) {
        String sasKeyName = null;
        String sasKeyValue = null;

        String[] parts = connectionString.split(";");
        if (parts.length != 3)
            throw new RuntimeException(String.format("Error parsing connection string: %s", connectionString));

        for (String part : parts) {
            if (part.startsWith("Endpoint")) {
                this.endpoint = "https" + part.substring(11);
            } else if (part.startsWith("SharedAccessKeyName")) {
                sasKeyName = part.substring(20);
            } else if (part.startsWith("SharedAccessKey")) {
                sasKeyValue = part.substring(16);
            }
        }

        tokenProvider = new SasTokenProvider(sasKeyName, sasKeyValue);
    }

    /**
     * Gets a notification hub by the hub path asynchronously.
     *
     * @param hubPath  The path of the notification hub.
     * @param callback A callback that returns the notification hub description.
     */
    @Override
    public void getNotificationHubAsync(String hubPath, final FutureCallback<NotificationHubDescription> callback) {
        URI uri;
        try {
            uri = new URI(endpoint + hubPath + API_VERSION);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final SimpleHttpRequest get = createRequest(uri, Method.GET)
            .build();

        executeRequest(get, callback, 200, response -> {
            try {
                callback.completed(NotificationHubDescription.parseOne(response.getBodyBytes()));
            } catch (Exception e) {
                callback.failed(e);
            }
        });
    }

    /**
     * Gets a notification hub by the hub path.
     *
     * @param hubPath The path of the notification hub.
     * @return The notification hub description.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException {
        SyncCallback<NotificationHubDescription> callback = new SyncCallback<>();
        getNotificationHubAsync(hubPath, callback);
        return callback.getResult();
    }

    /**
     * Gets all notification hubs for the namespace.
     *
     * @param callback A callback, when invoked, returns a list of all the
     *                 namespace's registration descriptions.
     */
    @Override
    public void getNotificationHubsAsync(final FutureCallback<List<NotificationHubDescription>> callback) {
        URI uri;
        try {
            uri = new URI(endpoint + HUBS_COLLECTION_PATH + API_VERSION + SKIP_TOP_PARAM);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final SimpleHttpRequest get = createRequest(uri, Method.GET)
            .build();

        executeRequest(get, callback, 200, response -> {
            try {
                callback.completed(NotificationHubDescription.parseCollection(response.getBodyBytes()));
            } catch (Exception e) {
                callback.failed(e);
            }
        });
    }

    /**
     * Gets all notification hubs for the namespace.
     *
     * @return A list of all the namespace's registration descriptions.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException {
        SyncCallback<List<NotificationHubDescription>> callback = new SyncCallback<>();
        getNotificationHubsAsync(callback);
        return callback.getResult();
    }

    /**
     * Creates a notification hub with the given notification hub description.
     *
     * @param hubDescription The notification hub description containing the
     *                       information for the notification hub.
     * @param callback       A callback, when invoked, returns the populated
     *                       notification hub description.
     */
    @Override
    public void createNotificationHubAsync(NotificationHubDescription hubDescription, final FutureCallback<NotificationHubDescription> callback) {
        createOrUpdateNotificationHubAsync(hubDescription, false, callback);
    }

    /**
     * Creates a notification hub with the given notification hub description.
     *
     * @param hubDescription The notification hub description containing the
     *                       information for the notification hub.
     * @return The populated notification hub description
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException {
        SyncCallback<NotificationHubDescription> callback = new SyncCallback<>();
        createNotificationHubAsync(hubDescription, callback);
        return callback.getResult();
    }

    /**
     * Updates a notification hub via the notification hub description.
     *
     * @param hubDescription The notification hub description to update.
     * @param callback       A callback, when invoked, returns the populated
     *                       notification hub description.
     */
    @Override
    public void updateNotificationHubAsync(NotificationHubDescription hubDescription, FutureCallback<NotificationHubDescription> callback) {
        createOrUpdateNotificationHubAsync(hubDescription, true, callback);
    }

    /**
     * Updates a notification hub via the notification hub description.
     *
     * @param hubDescription The notification hub description to update.
     * @return Returns the populated notification hub description.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException {
        SyncCallback<NotificationHubDescription> callback = new SyncCallback<>();
        updateNotificationHubAsync(hubDescription, callback);
        return callback.getResult();
    }

    private void createOrUpdateNotificationHubAsync(NotificationHubDescription hubDescription, final boolean isUpdate, final FutureCallback<NotificationHubDescription> callback) {
        URI uri;
        try {
            uri = new URI(endpoint + hubDescription.getPath() + API_VERSION);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final SimpleHttpRequest put = createRequest(uri, Method.PUT)
            .build();

        if (isUpdate) {
            put.setHeader(IF_MATCH_HEADER_NAME, "*");
        }

        put.setBody(hubDescription.getXml(), ContentType.APPLICATION_ATOM_XML);

        executeRequest(put, callback, isUpdate ? 200 : 201, response -> {
            try {
                callback.completed(NotificationHubDescription.parseOne(response.getBodyBytes()));
            } catch (Exception e) {
                callback.failed(e);
            }
        });
    }

    /**
     * Deletes the notification hub with the given hub name.
     *
     * @param hubPath  The name of the notification hub.
     * @param callback A callback, when invoked, returns nothing.
     */
    @Override
    public void deleteNotificationHubAsync(String hubPath, final FutureCallback<Object> callback) {
        URI uri;
        try {
            uri = new URI(endpoint + hubPath + API_VERSION);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final SimpleHttpRequest delete = createRequest(uri, Method.DELETE)
            .build();

        executeRequest(delete, callback, new int[] { 200, 404 }, response -> callback.completed(null));
    }

    /**
     * Deletes the notification hub with the given hub name.
     *
     * @param hubPath The name of the notification hub.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void deleteNotificationHub(String hubPath) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteNotificationHubAsync(hubPath, callback);
        callback.getResult();
    }
}
