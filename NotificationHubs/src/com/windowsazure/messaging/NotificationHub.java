// ----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
// ----------------------------------------------------------------

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
 * This class represents all actions that can be done on an Azure Notification
 * Hub.
 */
public class NotificationHub implements NotificationHubClient {

    private static final String API_VERSION = "?api-version=2020-06";
    private static final String CONTENT_LOCATION_HEADER = "Location";
    private static final String TRACKING_ID_HEADER = "TrackingId";
    private static final String USER_AGENT = "NHub/2020-06 (api-origin=JavaSDK;os=%s;os-version=%s)";
    private String endpoint;
    private final String hubPath;
    private String SasKeyName;
    private String SasKeyValue;

    /**
     * Creates a new instance of the notification hub with hub path and connection
     * string.
     *
     * @param connectionString The connection string from the access policy.
     * @param hubPath          The path of the notification hub.
     */
    public NotificationHub(String connectionString, String hubPath) {
        this.hubPath = hubPath;

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
    public void createRegistrationAsync(Registration registration, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations" + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public Registration createRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        createRegistrationAsync(registration, callback);
        return callback.getResult();
    }

    @Override
    public void createRegistrationIdAsync(final FutureCallback<String> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrationids" + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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
                            callback.failed(new RuntimeException("Invalid registration data returned"));
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

    @Override
    public String createRegistrationId() throws NotificationHubsException {
        SyncCallback<String> callback = new SyncCallback<>();
        createRegistrationIdAsync(callback);
        return callback.getResult();
    }

    @Override
    public void updateRegistrationAsync(Registration registration, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public Registration updateRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        updateRegistrationAsync(registration, callback);
        return callback.getResult();
    }

    @Override
    public void upsertRegistrationAsync(Registration registration, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public Registration upsertRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        upsertRegistrationAsync(registration, callback);
        return callback.getResult();
    }

    @Override
    public void deleteRegistrationAsync(Registration registration, final FutureCallback<Object> callback) {
        deleteRegistrationAsync(registration.getRegistrationId(), callback);
    }

    @Override
    public void deleteRegistration(Registration registration) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteRegistrationAsync(registration, callback);
        callback.getResult();
    }

    @Override
    public void deleteRegistrationAsync(String registrationId, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public void deleteRegistration(String registrationId) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteRegistrationAsync(registrationId, callback);
        callback.getResult();
    }

    @Override
    public void getRegistrationAsync(String registrationId, final FutureCallback<Registration> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public Registration getRegistration(String registrationId) throws NotificationHubsException {
        SyncCallback<Registration> callback = new SyncCallback<>();
        getRegistrationAsync(registrationId, callback);
        return callback.getResult();
    }

    @Override
    public void getRegistrationsAsync(FutureCallback<CollectionResult> callback) {
        getRegistrationsAsync(0, null, callback);
    }

    @Override
    public void getRegistrationsAsync(int top, String continuationToken,
            final FutureCallback<CollectionResult> callback) {
        String queryUri = endpoint + hubPath + "/registrations" + API_VERSION + getQueryString(top, continuationToken);
        retrieveRegistrationCollectionAsync(queryUri, callback);
    }

    @Override
    public CollectionResult getRegistrations(int top, String continuationToken) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsAsync(top, continuationToken, callback);
        return callback.getResult();
    }

    @Override
    public CollectionResult getRegistrations() throws NotificationHubsException {
        return getRegistrations(0, null);
    }

    @Override
    public void getRegistrationsByTagAsync(String tag, int top, String continuationToken,
            final FutureCallback<CollectionResult> callback) {
        String queryUri = endpoint + hubPath + "/tags/" + tag + "/registrations" + API_VERSION
                + getQueryString(top, continuationToken);
        retrieveRegistrationCollectionAsync(queryUri, callback);
    }

    @Override
    public CollectionResult getRegistrationsByTag(String tag, int top, String continuationToken)
            throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByTagAsync(tag, top, continuationToken, callback);
        return callback.getResult();
    }

    @Override
    public void getRegistrationsByTagAsync(String tag, final FutureCallback<CollectionResult> callback) {
        getRegistrationsByTagAsync(tag, 0, null, callback);
    }

    @Override
    public CollectionResult getRegistrationsByTag(String tag) throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByTagAsync(tag, callback);
        return callback.getResult();
    }

    @Override
    public void getRegistrationsByChannelAsync(String channel, int top, String continuationToken,
            final FutureCallback<CollectionResult> callback) {
        String queryUri;
        try {
            String channelQuery = URLEncoder.encode("ChannelUri eq '" + channel + "'", "UTF-8");
            queryUri = endpoint + hubPath + "/registrations" + API_VERSION + "&$filter=" + channelQuery
                    + getQueryString(top, continuationToken);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        retrieveRegistrationCollectionAsync(queryUri, callback);
    }

    @Override
    public CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken)
            throws NotificationHubsException {
        SyncCallback<CollectionResult> callback = new SyncCallback<>();
        getRegistrationsByChannelAsync(channel, top, continuationToken, callback);
        return callback.getResult();
    }

    @Override
    public void getRegistrationsByChannelAsync(String channel, final FutureCallback<CollectionResult> callback) {
        getRegistrationsByChannelAsync(channel, 0, null, callback);
    }

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
            get.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));

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

    @Override
    public void sendNotificationAsync(Notification notification, FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, "", null, callback);
    }

    @Override
    public NotificationOutcome sendNotification(Notification notification) throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendNotificationAsync(notification, callback);
        return callback.getResult();
    }

    @Override
    public void sendNotificationAsync(Notification notification, Set<String> tags,
            FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, tags, null, callback);
    }

    @Override
    public NotificationOutcome sendNotification(Notification notification, Set<String> tags)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendNotificationAsync(notification, tags, callback);
        return callback.getResult();
    }

    @Override
    public void sendNotificationAsync(Notification notification, String tagExpression,
            FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, tagExpression, null, callback);
    }

    @Override
    public NotificationOutcome sendNotification(Notification notification, String tagExpression)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendNotificationAsync(notification, tagExpression, callback);
        return callback.getResult();
    }

    @Override
    public void scheduleNotificationAsync(Notification notification, Date scheduledTime,
            FutureCallback<NotificationOutcome> callback) {
        scheduleNotificationAsync(notification, "", scheduledTime, callback);
    }

    @Override
    public NotificationOutcome scheduleNotification(Notification notification, Date scheduledTime)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        scheduleNotificationAsync(notification, scheduledTime, callback);
        return callback.getResult();
    }

    @Override
    public void scheduleNotificationAsync(Notification notification, Set<String> tags, Date scheduledTime,
            FutureCallback<NotificationOutcome> callback) {
        if (tags.isEmpty())
            throw new IllegalArgumentException("tags has to contain at least an element");

        StringBuilder exp = new StringBuilder();
        for (Iterator<String> iterator = tags.iterator(); iterator.hasNext();) {
            exp.append(iterator.next());
            if (iterator.hasNext())
                exp.append(" || ");
        }

        scheduleNotificationAsync(notification, exp.toString(), scheduledTime, callback);
    }

    @Override
    public NotificationOutcome scheduleNotification(Notification notification, Set<String> tags, Date scheduledTime)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        scheduleNotificationAsync(notification, tags, scheduledTime, callback);
        return callback.getResult();
    }

    @Override
    public void scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime,
            final FutureCallback<NotificationOutcome> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + (scheduledTime == null ? "/messages" : "/schedulednotifications")
                    + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);

            if (scheduledTime != null) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                String scheduledTimeHeader = df.format(scheduledTime);
                post.setHeader("ServiceBusNotification-ScheduleTime", scheduledTimeHeader);
            }

            if (tagExpression != null && !tagExpression.isEmpty()) {
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
                            String errorMessage = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                    httpStatusCode, errorMessage));
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
    public NotificationOutcome scheduleNotification(Notification notification, String tagExpression, Date scheduledTime)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        scheduleNotificationAsync(notification, tagExpression, scheduledTime, callback);
        return callback.getResult();
    }

    @Override
    public void cancelScheduledNotification(String notificationId) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        cancelScheduledNotificationAsync(notificationId, callback);
        callback.getResult();
    }

    @Override
    public void cancelScheduledNotificationAsync(String notificationId, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/schedulednotifications/" + notificationId + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));

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
    public NotificationOutcome sendDirectNotification(Notification notification, String deviceHandle)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendDirectNotificationAsync(notification, deviceHandle, callback);
        return callback.getResult();
    }

    @Override
    public NotificationOutcome sendDirectNotification(Notification notification, List<String> deviceHandles)
            throws NotificationHubsException {
        SyncCallback<NotificationOutcome> callback = new SyncCallback<>();
        sendDirectNotificationAsync(notification, deviceHandles, callback);
        return callback.getResult();
    }

    @Override
    public void sendDirectNotificationAsync(Notification notification, String deviceHandle,
            final FutureCallback<NotificationOutcome> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages" + API_VERSION + "&direct");
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("ServiceBusNotification-DeviceHandle", deviceHandle);
            post.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);

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

                            String errorMessage = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                    httpStatusCode, errorMessage));

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
    public void sendDirectNotificationAsync(Notification notification, List<String> deviceHandles,
            final FutureCallback<NotificationOutcome> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages/$batch" + API_VERSION + "&direct");
            final HttpPost post = new HttpPost(uri);
            final String trackingId = java.util.UUID.randomUUID().toString();
            post.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
            post.setHeader(TRACKING_ID_HEADER, trackingId);

            for (String header : notification.getHeaders().keySet()) {
                post.setHeader(header, notification.getHeaders().get(header));
            }

            FormBodyPart notificationPart = FormBodyPartBuilder.create().setName("notification")
                    .addField("Content-Disposition", "inline; name=notification")
                    .setBody(new StringBody(notification.getBody(), notification.getContentType())).build();

            String deviceHandlesJson = new GsonBuilder().disableHtmlEscaping().create().toJson(deviceHandles);
            FormBodyPart devicesPart = FormBodyPartBuilder.create().setName("devices")
                    .addField("Content-Disposition", "inline; name=devices")
                    .setBody(new StringBody(deviceHandlesJson, ContentType.APPLICATION_JSON)).build();

            HttpEntity entity = MultipartEntityBuilder.create().addPart(notificationPart).addPart(devicesPart).build();

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

                            String errorMessage = "Error: " + response.getStatusLine() + " body: " + msg;
                            callback.failed(NotificationHubsExceptionFactory.createNotificationHubException(response,
                                    httpStatusCode, errorMessage));

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
    public NotificationTelemetry getNotificationTelemetry(String notificationId) throws NotificationHubsException {
        SyncCallback<NotificationTelemetry> callback = new SyncCallback<>();
        getNotificationTelemetryAsync(notificationId, callback);
        return callback.getResult();
    }

    @Override
    public void getNotificationTelemetryAsync(String notificationId,
            final FutureCallback<NotificationTelemetry> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/messages/" + notificationId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));

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

    @Override
    public void createOrUpdateInstallationAsync(Installation installation, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installation.getInstallationId() + API_VERSION);
            final HttpPut put = new HttpPut(uri);
            put.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public void createOrUpdateInstallation(Installation installation) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        createOrUpdateInstallationAsync(installation, callback);
        callback.getResult();
    }

    @Override
    public void patchInstallationAsync(String installationId, FutureCallback<Object> callback,
            PartialUpdateOperation... operations) {
        patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations), callback);
    }

    @Override
    public void patchInstallation(String installationId, PartialUpdateOperation... operations)
            throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        patchInstallationAsync(installationId, callback, operations);
        callback.getResult();
    }

    @Override
    public void patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations,
            FutureCallback<Object> callback) {
        patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations), callback);
    }

    @Override
    public void patchInstallation(String installationId, List<PartialUpdateOperation> operations)
            throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        patchInstallationAsync(installationId, operations, callback);
        callback.getResult();
    }

    private void patchInstallationInternalAsync(String installationId, String operationsJson,
            final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
            final HttpPatch patch = new HttpPatch(uri);
            patch.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public void deleteInstallationAsync(String installationId, final FutureCallback<Object> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
            final HttpDelete delete = new HttpDelete(uri);
            delete.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public void deleteInstallation(String installationId) throws NotificationHubsException {
        SyncCallback<Object> callback = new SyncCallback<>();
        deleteInstallationAsync(installationId, callback);
        callback.getResult();
    }

    @Override
    public void getInstallationAsync(String installationId, final FutureCallback<Installation> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

                        callback.completed(Installation.fromJson(response.getEntity().getContent()));
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
    public Installation getInstallation(String installationId) throws NotificationHubsException {
        SyncCallback<Installation> callback = new SyncCallback<>();
        getInstallationAsync(installationId, callback);
        return callback.getResult();
    }

    @Override
    public void submitNotificationHubJobAsync(NotificationHubJob job,
            final FutureCallback<NotificationHubJob> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/jobs" + API_VERSION);
            final HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public NotificationHubJob submitNotificationHubJob(NotificationHubJob job) throws NotificationHubsException {
        SyncCallback<NotificationHubJob> callback = new SyncCallback<>();
        submitNotificationHubJobAsync(job, callback);
        return callback.getResult();
    }

    @Override
    public void getNotificationHubJobAsync(String jobId, final FutureCallback<NotificationHubJob> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/jobs/" + jobId + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public NotificationHubJob getNotificationHubJob(String jobId) throws NotificationHubsException {
        SyncCallback<NotificationHubJob> callback = new SyncCallback<>();
        getNotificationHubJobAsync(jobId, callback);
        return callback.getResult();
    }

    @Override
    public void getAllNotificationHubJobsAsync(final FutureCallback<List<NotificationHubJob>> callback) {
        try {
            URI uri = new URI(endpoint + hubPath + "/jobs" + API_VERSION);
            final HttpGet get = new HttpGet(uri);
            get.setHeader("Authorization", TokenProvider.generateSasToken(SasKeyName, SasKeyValue, uri));
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

    @Override
    public List<NotificationHubJob> getAllNotificationHubJobs() throws NotificationHubsException {
        SyncCallback<List<NotificationHubJob>> callback = new SyncCallback<>();
        getAllNotificationHubJobsAsync(callback);
        return callback.getResult();
    }

    private static String getUserAgent() {
        String os = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return String.format(USER_AGENT, os, osVersion);
    }
}
