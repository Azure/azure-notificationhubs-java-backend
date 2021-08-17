//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.util.List;

/**
 * This interface represents the operations that can be performed by the Azure
 * Notification Hub management API.
 */
public class NamespaceManager implements NamespaceManagerClient {
    private static final String IF_MATCH_HEADER_NAME = "If-Match";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String HUBS_COLLECTION_PATH = "$Resources/NotificationHubs/";
    private static final String API_VERSION = "?api-version=2014-09";
    private static final String SKIP_TOP_PARAM = "&$skip=0&$top=2147483647";
    private String endpoint;
    private final SasTokenProvider tokenProvider;

    /**
     * Creates a new instance of the NamespaceManager class.
     * @param connectionString The connection string from the Azure Notification Hubs namespace access policies.
     */
    public NamespaceManager(String connectionString) {
        String sasKeyName = null;
        String sasKeyValue = null;

        String[] parts = connectionString.split(";");
        if (parts.length != 3)
            throw new RuntimeException("Error parsing connection string: "
                + connectionString);

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
        try {
            URI uri = new URI(endpoint + hubPath + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader(AUTHORIZATION_HEADER_NAME, tokenProvider.generateSasToken(uri));

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsException.create(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationHubDescription.parseOne(response.getEntity().getContent()));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        get.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    get.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    get.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        try {
            URI uri = new URI(endpoint + HUBS_COLLECTION_PATH + API_VERSION + SKIP_TOP_PARAM);
            final HttpGet get = new HttpGet(uri);
            get.setHeader(AUTHORIZATION_HEADER_NAME, tokenProvider.generateSasToken(uri));

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsException.create(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationHubDescription.parseCollection(response.getEntity().getContent()));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        get.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    get.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    get.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        try {
            URI uri = new URI(endpoint + hubDescription.getPath() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader(AUTHORIZATION_HEADER_NAME, tokenProvider.generateSasToken(uri));
            if (isUpdate) {
                put.setHeader(IF_MATCH_HEADER_NAME, "*");
            }

            StringEntity entity = new StringEntity(hubDescription.getXml(), ContentType.APPLICATION_ATOM_XML);
            entity.setContentEncoding("utf-8");
            put.setEntity(entity);

            HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != (isUpdate ? 200 : 201)) {
                            callback.failed(NotificationHubsException.create(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationHubDescription.parseOne(response.getEntity().getContent()));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        put.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    put.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    put.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the notification hub with the given hub name.
     *
     * @param hubPath  The name of the notification hub.
     * @param callback A callback, when invoked, returns nothing.
     */
    @Override
    public void deleteNotificationHubAsync(String hubPath, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader(AUTHORIZATION_HEADER_NAME, tokenProvider.generateSasToken(uri));

            HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200 && httpStatusCode != 404) {
                            callback.failed(NotificationHubsException.create(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(null);
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        delete.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    delete.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    delete.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
