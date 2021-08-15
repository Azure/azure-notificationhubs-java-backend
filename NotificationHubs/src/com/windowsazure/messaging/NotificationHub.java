//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents all actions that can be done on an Azure Notification Hub.
 */
public class NotificationHub implements NotificationHubClient {

    private static final String USER_AGENT = "NHub/2020-06 (api-origin=JavaSDK;os=%s;os-version=%s)";
    private static final String API_VERSION = "?api-version=2020-06";
    private static final String CONTENT_LOCATION_HEADER = "Location";
    private static final String TRACKING_ID_HEADER = "TrackingId";
    private String endpoint;
    private final String hubPath;
    private final SasTokenProvider tokenProvider;

    /**
     * Creates a new instance of the NotificationHub class with connection string and hub path.
     * @param connectionString The connection string from the Azure Notification Hub access policies.
     * @param hubPath The name of the Azure Notification Hub name.
     */
    public NotificationHub(String connectionString, String hubPath) {
        this.hubPath = hubPath;

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
     * This method creates a new registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to create. ETag and registration ID are
     *                     ignored
     * @param callback     A callback when invoked returns created registration
     *                     containing the read-only parameters (registration ID,
     *                     ETag, and expiration time)
     */
    @Override
    public void createRegistrationAsync(Registration registration, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations" + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader("User-Agent", getUserAgent());

            StringEntity entity = new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML);
            entity.setContentEncoding("utf-8");
            post.setEntity(entity);

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(Registration.parse(response.getEntity().getContent()));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method creates a new registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to create. ETag and registration ID are
     *                     ignored
     * @return The created registration containing the read-only parameters
     * (registration ID, ETag, and expiration time).
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public Registration createRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        createRegistrationAsync(registration, callback);
        return callback.getResult();
    }

    /**
     * Create a registrationId, without creating an actual registration. To create
     * use upsert. This method is used when the registration id is stored only on
     * the device.
     *
     * @param callback A callback with the newly created registration ID.
     */
    @Override
    public void createRegistrationIdAsync(final FutureCallback<String> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrationids" + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        String location = response.getFirstHeader(CONTENT_LOCATION_HEADER).getValue();
                        Pattern extractId = Pattern.compile("(\\S+)/registrationids/([^?]+).*");
                        Matcher m = extractId.matcher(location);

                        if (m.matches()) {
                            String id = m.group(2);
                            callback.completed(id);
                        } else {
                            callback.completed(null);
                        }
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a registrationId, without creating an actual registration. To create
     * use upsert. This method is used when the registration id is stored only on
     * the device.
     *
     * @return The newly created registration ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public String createRegistrationId() throws NotificationHubsException {
        SyncCallback<String> callback = new SyncCallback<>();
        createRegistrationIdAsync(callback);
        return callback.getResult();
    }

    /**
     * This method updates an existing registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to update. The registration ID must be
     *                     populated.
     * @param callback     A callback when invoked, returns the updated registration
     *                     containing the read-only parameters (registration ID,
     *                     ETag, and expiration time).
     */
    @Override
    public void updateRegistrationAsync(Registration registration, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            put.setHeader("If-Match", registration.getEtag() == null ? "*" : "W/\"" + registration.getEtag() + "\"");
            put.setHeader("User-Agent", getUserAgent());
            put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));

            HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(Registration.parse(response.getEntity().getContent()));
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
     * This method updates an existing registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to update. The registration ID has to be
     *                     populated.
     * @return The updated registration containing the read-only parameters
     * (registration ID, ETag, and expiration time).
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public Registration updateRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        updateRegistrationAsync(registration, callback);
        return callback.getResult();
    }

    /**
     * This method updates or creates a new registration with the registration id
     * specified.
     *
     * @param registration A registration object containing the description of the
     *                     registration to create or update. The registration ID
     *                     must be populated.
     * @param callback     A callback, when invoked, returns the updated
     *                     registration containing the read-only parameters
     *                     (registration ID, ETag, and expiration time).
     */
    @Override
    public void upsertRegistrationAsync(Registration registration, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            put.setHeader("User-Agent", getUserAgent());
            put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));

            HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(Registration.parse(response.getEntity().getContent()));
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
     * This method updates or creates a new registration with the registration ID
     * specified.
     *
     * @param registration A registration object containing the description of the
     *                     registration to create or update. The registration ID
     *                     must be populated.
     * @return The updated registration containing the read-only parameters
     * (registration ID, ETag, and expiration time).
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public Registration upsertRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        upsertRegistrationAsync(registration, callback);
        return callback.getResult();
    }

    /**
     * Deletes a registration with the given registration containing a populated
     * registrationId.
     *
     * @param registration The registration containing the registrationId field
     *                     populated.
     * @param callback     A callback when invoked returns nothing.
     */
    @Override
    public void deleteRegistrationAsync(Registration registration, final FutureCallback<Object> callback) {
        deleteRegistrationAsync(registration.getRegistrationId(), callback);
    }

    /**
     * Deletes a registration with the given registration containing a populated
     * registrationId.
     *
     * @param registration The registration containing the registrationId field
     *                     populated.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void deleteRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteRegistrationAsync(registration, callback);
        callback.getResult();
    }

    /**
     * Deletes a registration by the given registration ID.
     *
     * @param registrationId The registration ID for the registration to delete.
     * @param callback       A callback when invoked returns nothing.
     */
    @Override
    public void deleteRegistrationAsync(String registrationId, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            delete.setHeader("If-Match", "*");
            delete.setHeader("User-Agent", getUserAgent());

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

    /**
     * Deletes a registration by the given registration ID.
     *
     * @param registrationId The registration ID for the registration to delete.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void deleteRegistration(String registrationId) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteRegistrationAsync(registrationId, callback);
        callback.getResult();
    }

    /**
     * Retrieves the description of a registration based on the ID.
     *
     * @param registrationId The ID for the registration to retrieve.
     * @param callback       A callback, when invoked, returns the registration with
     *                       the ID matching the given registration ID.
     */
    @Override
    public void getRegistrationAsync(String registrationId, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            get.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(Registration.parse(response.getEntity().getContent()));
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
     * Retrieves the description of a registration based on the ID.
     *
     * @param registrationId The ID for the registration to retrieve.
     * @return The registration with the ID matching the given registration ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public Registration getRegistration(String registrationId) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        getRegistrationAsync(registrationId, callback);
        return callback.getResult();
    }

    /**
     * Return all registrations in the current notification hub.
     *
     * @param callback The callback when invoked, returns a collection containing
     *                 registrations.
     */
    @Override
    public void getRegistrationsAsync(FutureCallback<CollectionResult> callback) {
        getRegistrationsAsync(0, null, callback);
    }

    /**
     * Returns all registrations in this hub
     *
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @param callback          A callback when invoked returns a collection
     *                          containing the registrations.
     */
    @Override
    public void getRegistrationsAsync(int top, String continuationToken, final FutureCallback<CollectionResult> callback) {
        String queryUri = endpoint + hubPath + "/registrations" + API_VERSION + getQueryString(top, continuationToken);
        retrieveRegistrationCollectionAsync(queryUri, callback);
    }

    /**
     * Returns all registrations in this hub
     *
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @return A collection containing the registrations.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public CollectionResult getRegistrations(int top, String continuationToken) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsAsync(top, continuationToken, callback);
        return callback.getResult();
    }

    /**
     * Return all registrations in the current notification hub.
     *
     * @return Collection containing the registrations.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public CollectionResult getRegistrations() throws NotificationHubsException {
        return getRegistrations(0, null);
    }

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag               The tag to search for registrations.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @param callback          A callback when invoked, returns a collection of
     *                          registrations with the given tag.
     */
    @Override
    public void getRegistrationsByTagAsync(String tag, int top, String continuationToken, final FutureCallback<CollectionResult> callback) {
        String queryUri = endpoint + hubPath + "/tags/" + tag
            + "/registrations" + API_VERSION
            + getQueryString(top, continuationToken);
        retrieveRegistrationCollectionAsync(queryUri, callback);
    }

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag               The tag to search for registrations.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @return A collection of registrations with the given tag.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public CollectionResult getRegistrationsByTag(String tag, int top, String continuationToken) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByTagAsync(tag, top, continuationToken, callback);
        return callback.getResult();
    }

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag      The tag to search for registrations.
     * @param callback A callback, when invoked, returns a collection of
     *                 registrations with the given tag.
     */
    @Override
    public void getRegistrationsByTagAsync(String tag, final FutureCallback<CollectionResult> callback) {
        getRegistrationsByTagAsync(tag, 0, null, callback);
    }

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag The tag to search for registrations.
     * @return A collection of registrations with the given tag.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public CollectionResult getRegistrationsByTag(String tag) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByTagAsync(tag, callback);
        return callback.getResult();
    }

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel           The channel URI, device token or other unique PNS
     *                          identifier.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @param callback          A callback, when invoked, returns a collection of
     *                          registrations with matching channels.
     */
    @Override
    public void getRegistrationsByChannelAsync(String channel, int top, String continuationToken, final FutureCallback<CollectionResult> callback) {
        String queryUri;
        try {
            String channelQuery = URLEncoder.encode("ChannelUri eq '" + channel + "'", "UTF-8");
            queryUri = endpoint + hubPath + "/registrations" + API_VERSION
                + "&$filter=" + channelQuery
                + getQueryString(top, continuationToken);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        retrieveRegistrationCollectionAsync(queryUri, callback);
    }

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel           The channel URI, device token or other unique PNS
     *                          identifier.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @return A collection of registrations with matching channels.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByChannelAsync(channel, top, continuationToken, callback);
        return callback.getResult();
    }

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel  The channel URI, device token or other unique PNS identifier.
     * @param callback A callback, when invoked, returns a collection of
     *                 registrations with matching channels.
     */
    @Override
    public void getRegistrationsByChannelAsync(String channel, final FutureCallback<CollectionResult> callback) {
        getRegistrationsByChannelAsync(channel, 0, null, callback);
    }

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel The channel URI, device token or other unique PNS identifier.
     * @return A collection of registrations with matching channels.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public CollectionResult getRegistrationsByChannel(String channel) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByChannelAsync(channel, callback);
        return callback.getResult();
    }

    private String getQueryString(int top, String continuationToken) {
        StringBuilder buf = new StringBuilder();
        if (top > 0) {
            buf.append("&$top=").append(top);
        }
        if (continuationToken != null) {
            buf.append("&ContinuationToken=").append(continuationToken);
        }
        return buf.toString();
    }

    private void retrieveRegistrationCollectionAsync(String queryUri, final FutureCallback<CollectionResult> callback) {
        try {
            URI uri = new URI(queryUri);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            get.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        CollectionResult result = Registration.parseRegistrations(response.getEntity().getContent());
                        Header contTokenHeader = response.getFirstHeader("X-MS-ContinuationToken");
                        if (contTokenHeader != null) {
                            result.setContinuationToken(contTokenHeader.getValue());
                        }

                        callback.completed(result);
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
     * Sends a notification to all eligible registrations (i.e. only correct
     * platform, if notification is platform specific)
     *
     * @param notification The notification to send to all eligible registrations.
     * @param callback     A callback, when invoked, returns a notification outcome
     *                     with the tracking ID and notification ID.
     */
    @Override
    public void sendNotificationAsync(Notification notification, FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, "", null, callback);
    }

    /**
     * Sends a notification to all eligible registrations (i.e. only correct
     * platform, if notification is platform specific)
     *
     * @param notification The notification to send to all eligible registrations.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome sendNotification(Notification notification) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendNotificationAsync(notification, callback);
        return callback.getResult();
    }

    /**
     * Sends a notifications to all eligible registrations with at least one of the
     * specified tags
     *
     * @param notification The notification to send to the audience with the
     *                     specified tags.
     * @param tags         The tags for targeting the notifications.
     * @param callback     A callback, when invoked, returns a notification outcome
     *                     with the tracking ID and notification ID.
     */
    @Override
    public void sendNotificationAsync(Notification notification, Set<String> tags, FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, tags, null, callback);
    }

    /**
     * Sends a notifications to all eligible registrations with at least one of the
     * specified tags
     *
     * @param notification The notification to send to the audience with the
     *                     specified tags.
     * @param tags         The tags for targeting the notifications.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome sendNotification(Notification notification, Set<String> tags) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendNotificationAsync(notification, tags, callback);
        return callback.getResult();
    }

    /**
     * Sends a notifications to all eligible registrations that satisfy the provided
     * tag expression.
     *
     * @param notification  The notification to send to the audience that matches
     *                      the specified tag expression.
     * @param tagExpression The tag expression for targeting the notifications.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    @Override
    public void sendNotificationAsync(Notification notification, String tagExpression, FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, tagExpression, null, callback);
    }

    /**
     * Sends a notifications to all eligible registrations that satisfy the provided
     * tag expression.
     *
     * @param notification  The notification to send to the audience that matches
     *                      the specified tag expression.
     * @param tagExpression The tag expression for targeting the notifications.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome sendNotification(Notification notification, String tagExpression) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendNotificationAsync(notification, tagExpression, callback);
        return callback.getResult();
    }

    /**
     * Schedules a notification at the given scheduled time.
     *
     * @param notification  The notification to send at the scheduled time.
     * @param scheduledTime The scheduled time for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    @Override
    public void scheduleNotificationAsync(Notification notification, Date scheduledTime, FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, "", scheduledTime, callback);
    }

    /**
     * Schedules a notification at the given scheduled time.
     *
     * @param notification  The notification to send at the scheduled time.
     * @param scheduledTime The scheduled time for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome scheduleNotification(Notification notification, Date scheduledTime) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        scheduleNotificationAsync(notification, scheduledTime, callback);
        return callback.getResult();
    }

    /**
     * Schedules a notification at the given time with a set of tags.
     *
     * @param notification  The notification to send at the given time.
     * @param tags          The tags associated with the notification targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    @Override
    public void scheduleNotificationAsync(Notification notification, Set<String> tags, Date scheduledTime, FutureCallback<NotificationOutcome> callback) {
        if (tags.isEmpty())
            throw new IllegalArgumentException(
                "tags has to contain at least an element");

        StringBuilder exp = new StringBuilder();
        for (Iterator<String> iterator = tags.iterator(); iterator.hasNext(); ) {
            exp.append(iterator.next());
            if (iterator.hasNext())
                exp.append(" || ");
        }

        scheduleNotificationAsync(notification, exp.toString(), scheduledTime, callback);
    }

    /**
     * Schedules a notification at the given time with a set of tags.
     *
     * @param notification  The notification to send at the given time.
     * @param tags          The tags associated with the notification targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome scheduleNotification(Notification notification, Set<String> tags, Date scheduledTime) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        scheduleNotificationAsync(notification, tags, scheduledTime, callback);
        return callback.getResult();
    }

    /**
     * Schedules a notification at the given time with a tag expression.
     *
     * @param notification  The notification to send at the given time.
     * @param tagExpression The tag expression associated with the notification
     *                      targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    @Override
    public void scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime, final FutureCallback<NotificationOutcome> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + (scheduledTime == null ? "/messages" : "/schedulednotifications") + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);
            post.setHeader("User-Agent", getUserAgent());

            if (scheduledTime != null) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                String scheduledTimeHeader = df.format(scheduledTime);
                post.setHeader("ServiceBusNotification-ScheduleTime", scheduledTimeHeader);
            }

            if (tagExpression != null && !"".equals(tagExpression)) {
                post.setHeader("ServiceBusNotification-Tags", tagExpression);
            }

            for (String header : notification.getHeaders().keySet()) {
                post.setHeader(header, notification.getHeaders().get(header));
            }

            post.setEntity(new StringEntity(notification.getBody(), notification.getContentType()));

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            String msg = "";
                            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                                msg = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                            }
                            msg = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response, httpStatusCode, msg));
                            return;
                        }

                        String notificationId = null;
                        Header locationHeader = response.getFirstHeader(CONTENT_LOCATION_HEADER);
                        if (locationHeader != null) {
                            URI location = new URI(locationHeader.getValue());
                            String[] segments = location.getPath().split("/");
                            notificationId = segments[segments.length - 1];
                        }

                        callback.completed(new NotificationOutcome(trackingId, notificationId));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Schedules a notification at the given time with a tag expression.
     *
     * @param notification  The notification to send at the given time.
     * @param tagExpression The tag expression associated with the notification
     *                      targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome scheduleNotification(Notification notification, String tagExpression, Date scheduledTime) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        scheduleNotificationAsync(notification, tagExpression, scheduledTime, callback);
        return callback.getResult();
    }

    /**
     * Cancels the scheduled notification with the given notification ID.
     *
     * @param notificationId The notification ID of the notification to cancel.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void cancelScheduledNotification(String notificationId) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        cancelScheduledNotificationAsync(notificationId, callback);
        callback.getResult();
    }

    /**
     * Cancels the scheduled notification with the given notification ID.
     *
     * @param notificationId The notification ID of the notification to cancel.
     * @param callback       A callback, when invoked, returns nothing.
     */
    @Override
    public void cancelScheduledNotificationAsync(String notificationId, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/schedulednotifications/" + notificationId + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            delete.setHeader("User-Agent", getUserAgent());

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

    /**
     * Sends a direct notification to a given device handle.
     *
     * @param notification The notification to send directly to the device handle.
     * @param deviceHandle The device handle to target for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome sendDirectNotification(Notification notification, String deviceHandle) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendDirectNotificationAsync(notification, deviceHandle, callback);
        return callback.getResult();
    }

    /**
     * Sends a direct notification to a given browser push channel.
     *
     * @param notification The notification to send directly to the browser push channel.
     * @param pushChannel The browser push channel to target for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome sendDirectNotification(BrowserNotification notification, BrowserPushChannel pushChannel) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendDirectNotificationAsync(notification, pushChannel, callback);
        return callback.getResult();
    }

    /**
     * Sends a direct notification to the given device handles.
     *
     * @param notification  The notification to send directly to the device handles.
     * @param deviceHandles The device handles to target for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationOutcome sendDirectNotification(Notification notification, List<String> deviceHandles) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendDirectNotificationAsync(notification, deviceHandles, callback);
        return callback.getResult();
    }

    /**
     * Sends a direct notification to a given device handle.
     *
     * @param notification The notification to send directly to the device handle.
     * @param deviceHandle The device handle to target for the notification.
     * @param callback     A callback, when invoked, returns a notification outcome
     *                     with the tracking ID and notification ID.
     */
    @Override
    public void sendDirectNotificationAsync(
        Notification notification,
        String deviceHandle,
        final FutureCallback<NotificationOutcome> callback
    ) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages" + API_VERSION + "&direct");
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("ServiceBusNotification-DeviceHandle", deviceHandle);
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);
            post.setHeader("User-Agent", getUserAgent());

            for (String header : notification.getHeaders().keySet()) {
                post.setHeader(header, notification.getHeaders().get(header));
            }

            post.setEntity(new StringEntity(notification.getBody(), notification.getContentType()));

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            String msg = "";
                            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                                msg = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                            }
                            msg = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response, httpStatusCode, msg));
                            return;
                        }

                        String notificationId = null;
                        Header locationHeader = response.getFirstHeader(CONTENT_LOCATION_HEADER);
                        if (locationHeader != null) {
                            URI location = new URI(locationHeader.getValue());
                            String[] segments = location.getPath().split("/");
                            notificationId = segments[segments.length - 1];
                        }

                        callback.completed(new NotificationOutcome(trackingId, notificationId));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a direct notification to a given browser push channel.
     *
     * @param notification The notification to send directly to the push channel.
     * @param pushChannel  The browser push channel to send directly to.
     * @param callback     A callback, when invoked, returns a notification outcome
     */
    @Override
    public void sendDirectNotificationAsync(
        BrowserNotification notification,
        BrowserPushChannel pushChannel,
        FutureCallback<NotificationOutcome> callback
    ) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages" + API_VERSION + "&direct");
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("ServiceBusNotification-DeviceHandle", pushChannel.getEndpoint());
            post.setHeader("P256DH", pushChannel.getP256dh());
            post.setHeader("Auth", pushChannel.getAuth());
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);
            post.setHeader("User-Agent", getUserAgent());

            for (String header : notification.getHeaders().keySet()) {
                post.setHeader(header, notification.getHeaders().get(header));
            }

            post.setEntity(new StringEntity(notification.getBody(), notification.getContentType()));

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            String msg = "";
                            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                                msg = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                            }
                            msg = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response, httpStatusCode, msg));
                            return;
                        }

                        String notificationId = null;
                        Header locationHeader = response.getFirstHeader(CONTENT_LOCATION_HEADER);
                        if (locationHeader != null) {
                            URI location = new URI(locationHeader.getValue());
                            String[] segments = location.getPath().split("/");
                            notificationId = segments[segments.length - 1];
                        }

                        callback.completed(new NotificationOutcome(trackingId, notificationId));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a direct notification to the given device handles.
     *
     * @param notification  The notification to send directly to the device handles.
     * @param deviceHandles The device handles to target for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    @Override
    public void sendDirectNotificationAsync(Notification notification, List<String> deviceHandles, final FutureCallback<NotificationOutcome> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages/$batch" + API_VERSION + "&direct");
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);
            post.setHeader("User-Agent", getUserAgent());

            for (String header : notification.getHeaders().keySet()) {
                post.setHeader(header, notification.getHeaders().get(header));
            }

            FormBodyPart notificationPart = FormBodyPartBuilder.create()
                .setName("notification")
                .addField("Content-Disposition", "inline; name=notification")
                .setBody(new StringBody(notification.getBody(), notification.getContentType()))
                .build();

            String deviceHandlesJson = new GsonBuilder().disableHtmlEscaping().create().toJson(deviceHandles);
            FormBodyPart devicesPart = FormBodyPartBuilder.create()
                .setName("devices")
                .addField("Content-Disposition", "inline; name=devices")
                .setBody(new StringBody(deviceHandlesJson, ContentType.APPLICATION_JSON))
                .build();

            HttpEntity entity = MultipartEntityBuilder.create()
                .addPart(notificationPart)
                .addPart(devicesPart)
                .build();

            post.setEntity(entity);

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            String msg = "";
                            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                                msg = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                            }
                            msg = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response, httpStatusCode, msg));
                            return;
                        }

                        String notificationId = null;
                        Header locationHeader = response.getFirstHeader(CONTENT_LOCATION_HEADER);
                        if (locationHeader != null) {
                            URI location = new URI(locationHeader.getValue());
                            String[] segments = location.getPath().split("/");
                            notificationId = segments[segments.length - 1];
                        }

                        callback.completed(new NotificationOutcome(trackingId, notificationId));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotificationTelemetry getNotificationTelemetry(String notificationId)
        throws NotificationHubsException {
        SyncCallback<NotificationTelemetry> callback = new SyncCallback<>();
        getNotificationTelemetryAsync(notificationId, callback);
        return callback.getResult();
    }

    @Override
    public void getNotificationTelemetryAsync(String notificationId, final FutureCallback<NotificationTelemetry> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages/" + notificationId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            get.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationTelemetry.parseOne(response.getEntity().getContent()));
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
     * Creates or updates an installation.
     *
     * @param installation The installation to create or update.
     * @param callback     A callback, when invoked, returns nothing.
     */
    @Override
    public void createOrUpdateInstallationAsync(BaseInstallation installation, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installation.getInstallationId() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            put.setHeader("User-Agent", getUserAgent());

            StringEntity entity = new StringEntity(installation.toJson(), ContentType.APPLICATION_JSON);
            entity.setContentEncoding("utf-8");
            put.setEntity(entity);

            HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(null);
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
     * Creates or updates an installation.
     *
     * @param installation The installation to create or update.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void createOrUpdateInstallation(BaseInstallation installation) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        createOrUpdateInstallationAsync(installation, callback);
        callback.getResult();
    }

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param callback       A callback, when invoked, returns nothing.
     * @param operations     The list of operations to perform on the installation.
     */
    @Override
    public void patchInstallationAsync(String installationId, FutureCallback<Object> callback, PartialUpdateOperation... operations) {
        patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations), callback);
    }

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param operations     The list of operations to perform on the installation.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void patchInstallation(String installationId, PartialUpdateOperation... operations) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        patchInstallationAsync(installationId, callback, operations);
        callback.getResult();
    }

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param operations     The list of operations to perform on the installation.
     * @param callback       A callback, when invoked, returns nothing.
     */
    @Override
    public void patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations, FutureCallback<Object> callback) {
        patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations), callback);
    }

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param operations     The list of operations to perform on the installation.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void patchInstallation(String installationId, List<PartialUpdateOperation> operations) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        patchInstallationAsync(installationId, operations, callback);
        callback.getResult();
    }

    private void patchInstallationInternalAsync(String installationId, String operationsJson, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
            final HttpPatch patch = new HttpPatch(uri);
            patch.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            patch.setHeader("User-Agent", getUserAgent());

            StringEntity entity = new StringEntity(operationsJson, ContentType.APPLICATION_JSON);
            entity.setContentEncoding("utf-8");
            patch.setEntity(entity);

            HttpClientManager.getHttpAsyncClient().execute(patch, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(null);
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        patch.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    patch.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    patch.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an installation with the given installation ID.
     *
     * @param installationId The installation ID.
     * @param callback       A callback, when invoked, returns nothing.
     */
    @Override
    public void deleteInstallationAsync(String installationId, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            delete.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 204) {
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

    /**
     * Deletes an installation with the given installation ID.
     *
     * @param installationId The installation ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public void deleteInstallation(String installationId) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteInstallationAsync(installationId, callback);
        callback.getResult();
    }

    /**
     * Gets an installation by the given installation ID.
     *
     * @param installationId The installation ID for the installation to get.
     * @param callback       A callback, when invoked, returns the matching
     *                       installation by the installation ID.
     */
    @Override
    public <T extends BaseInstallation> void getInstallationAsync(String installationId, final FutureCallback<T> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            get.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(BaseInstallationFactory.createInstallation(response.getEntity().getContent()));
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
     * Gets an installation by the given installation ID.
     *
     * @param installationId The installation ID for the installation to get.
     * @return The matching installation by the installation ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public <T extends BaseInstallation> T getInstallation(String installationId) throws NotificationHubsException {
        SyncCallback<T> callback = new SyncCallback<>();
        getInstallationAsync(installationId, callback);
        return callback.getResult();
    }

    /**
     * Submits a notification hub job such as import or export.
     *
     * @param job      The notification hubs job to submit.
     * @param callback A callback, when invoked, returns the notification job with
     *                 status.
     */
    @Override
    public void submitNotificationHubJobAsync(NotificationHubJob job, final FutureCallback<NotificationHubJob> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/jobs" + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            post.setHeader("User-Agent", getUserAgent());

            StringEntity entity = new StringEntity(job.getXml(), ContentType.APPLICATION_ATOM_XML);
            entity.setContentEncoding("utf-8");
            post.setEntity(entity);

            HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationHubJob.parseOne(response.getEntity().getContent()));
                    } catch (Exception e) {
                        callback.failed(e);
                    } finally {
                        post.releaseConnection();
                    }
                }

                public void failed(final Exception ex) {
                    post.releaseConnection();
                    callback.failed(ex);
                }

                public void cancelled() {
                    post.releaseConnection();
                    callback.cancelled();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Submits a notification hub job such as import or export.
     *
     * @param job The notification hubs job to submit.
     * @return The notification job with status.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationHubJob submitNotificationHubJob(NotificationHubJob job) throws NotificationHubsException {
        SyncCallback<NotificationHubJob> callback = new SyncCallback<>();
        submitNotificationHubJobAsync(job, callback);
        return callback.getResult();
    }

    /**
     * Gets a notification hub job by the job ID.
     *
     * @param jobId    The job ID of the notification hub job to get.
     * @param callback A callback, when invoked, returns the notification hub job
     *                 with the matching job ID.
     */
    @Override
    public void getNotificationHubJobAsync(String jobId, final FutureCallback<NotificationHubJob> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/jobs/" + jobId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            get.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationHubJob.parseOne(response.getEntity().getContent()));
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
     * Gets a notification hub job by the job ID.
     *
     * @param jobId The job ID of the notification hub job to get.
     * @return The notification hub job with the matching job ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public NotificationHubJob getNotificationHubJob(String jobId) throws NotificationHubsException {
        SyncCallback<NotificationHubJob> callback = new SyncCallback<>();
        getNotificationHubJobAsync(jobId, callback);
        return callback.getResult();
    }

    /**
     * Gets all notification hub jobs for this namespace.
     *
     * @param callback A callback, when invoked, returns all notification hub jobs
     *                 for this namespace.
     */
    @Override
    public void getAllNotificationHubJobsAsync(final FutureCallback<List<NotificationHubJob>> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/jobs" + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", tokenProvider.generateSasToken(uri));
            get.setHeader("User-Agent", getUserAgent());

            HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try {
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                httpStatusCode));
                            return;
                        }

                        callback.completed(NotificationHubJob.parseCollection(response.getEntity().getContent()));
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
     * Gets all notification hub jobs for this namespace.
     *
     * @return All notification hub jobs for this namespace.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @Override
    public List<NotificationHubJob> getAllNotificationHubJobs() throws NotificationHubsException {
        SyncCallback<List<NotificationHubJob>> callback = new SyncCallback<>();
        getAllNotificationHubJobsAsync(callback);
        return callback.getResult();
    }

    private String getErrorString(HttpResponse response)
        throws IllegalStateException, IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
        String body = writer.toString();
        return "Error: " + response.getStatusLine() + " - " + body;
    }

    private static String getUserAgent() {
        String os = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return String.format(USER_AGENT, os, osVersion);
    }
}
