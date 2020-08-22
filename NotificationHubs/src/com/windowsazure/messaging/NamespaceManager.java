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
 * This class represents the operations that can be performed by the Azure
 * Notification Hub management API.
 */
public class NamespaceManager implements NamespaceManagerClient {
    private static final String IF_MATCH_HEADER_NAME = "If-Match";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String HUBS_COLLECTION_PATH = "$Resources/NotificationHubs/";
    private static final String API_VERSION = "?api-version=2014-09";
    private static final String SKIP_TOP_PARAM = "&$skip=0&$top=2147483647";
    private String endpoint;
    private String SasKeyName;
    private String SasKeyValue;

    /**
     * Creates a namespace manager with a connection string.
     *
     * @param connectionString The connection string from the access policy.
     */
    public NamespaceManager(String connectionString) {
        String[] parts = connectionString.split(";");
        if (parts.length != 3)
            throw new RuntimeException("Error parsing connection string: " + connectionString);

        for (String part : parts) {
            if (part.startsWith("Endpoint")) {
                this.endpoint = "https" + part.substring(11);
            } else if (part.startsWith("SharedAccessKeyName")) {
                this.SasKeyName = part.substring(20);
            } else if (part.startsWith("SharedAccessKey")) {
                this.SasKeyValue = part.substring(16);
            }
        }
    }

    @Override
    public void getNotificationHubAsync(String hubPath, final FutureCallback<NotificationHubDescription> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader(AUTHORIZATION_HEADER_NAME, TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
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

    @Override
    public NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException {
        SyncCallback<NotificationHubDescription> callback = new SyncCallback<>();
        getNotificationHubAsync(hubPath, callback);
        return callback.getResult();
    }

    @Override
    public void getNotificationHubsAsync(final FutureCallback<List<NotificationHubDescription>> callback) {
        try {
            URI uri = new URI(endpoint + HUBS_COLLECTION_PATH + API_VERSION + SKIP_TOP_PARAM);
            final HttpGet get = new HttpGet(uri);
            get.setHeader(AUTHORIZATION_HEADER_NAME, TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                    httpStatusCode));
                            return;
                        }

                        callback.completed(
                                NotificationHubDescription.parseCollection(response.getEntity().getContent()));
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

    @Override
    public List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException {
        SyncCallback<List<NotificationHubDescription>> callback = new SyncCallback<>();
        getNotificationHubsAsync(callback);
        return callback.getResult();
    }

    @Override
    public void createNotificationHubAsync(NotificationHubDescription hubDescription,
            final FutureCallback<NotificationHubDescription> callback) {
        createOrUpdateNotificationHubAsync(hubDescription, false, new FutureCallback<NotificationHubDescription>() {
            @Override
            public void completed(NotificationHubDescription result) {

            }

            @Override
            public void failed(Exception ex) {

            }

            @Override
            public void cancelled() {

            }
        });
        createOrUpdateNotificationHubAsync(hubDescription, false, callback);
    }

    @Override
    public NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription)
            throws NotificationHubsException {
        SyncCallback<NotificationHubDescription> callback = new SyncCallback<>();
        createNotificationHubAsync(hubDescription, callback);
        return callback.getResult();
    }

    @Override
    public void updateNotificationHubAsync(NotificationHubDescription hubDescription,
            FutureCallback<NotificationHubDescription> callback) {
        createOrUpdateNotificationHubAsync(hubDescription, true, callback);
    }

    @Override
    public NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription)
            throws NotificationHubsException {
        SyncCallback<NotificationHubDescription> callback = new SyncCallback<>();
        updateNotificationHubAsync(hubDescription, callback);
        return callback.getResult();
    }

    private void createOrUpdateNotificationHubAsync(NotificationHubDescription hubDescription, final boolean isUpdate,
            final FutureCallback<NotificationHubDescription> callback) {
        try {
            URI uri = new URI(endpoint + hubDescription.getPath() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader(AUTHORIZATION_HEADER_NAME, TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
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

    @Override
    public void deleteNotificationHubAsync(String hubPath, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader(AUTHORIZATION_HEADER_NAME, TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));

            HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200 && httpStatusCode != 404) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
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

    @Override
    public void deleteNotificationHub(String hubPath) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteNotificationHubAsync(hubPath, callback);
        callback.getResult();
    }
}
