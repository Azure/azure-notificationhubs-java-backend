//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;
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
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

import static com.windowsazure.messaging.RetryUtil.getRetryPolicy;
import static com.windowsazure.messaging.RetryUtil.withRetry;

/**
 *
 * Class implementing the INotificationHub interface.
 *
 */
public class NotificationHub implements NotificationHubClient {

	private static final String API_VERSION = "?api-version=2020-06";
	private static final String CONTENT_LOCATION_HEADER = "Location";
	private static final String TRACKING_ID_HEADER = "TrackingId";
    private final RetryOptions retryOptions;
    private final RetryPolicy retryPolicy;
	private String endpoint;
	private final String hubPath;
	private String SasKeyName;
	private String SasKeyValue;

	public NotificationHub(String connectionString, String hubPath, RetryOptions retryOptions) {
		this.hubPath = hubPath;

		String[] parts = connectionString.split(";");
		if (parts.length != 3)
			throw new RuntimeException("Error parsing connection string: "
					+ connectionString);

        for (String part : parts) {
            if (part.startsWith("Endpoint")) {
                this.endpoint = "https" + part.substring(11);
            } else if (part.startsWith("SharedAccessKeyName")) {
                this.SasKeyName = part.substring(20);
            } else if (part.startsWith("SharedAccessKey")) {
                this.SasKeyValue = part.substring(16);
            }
        }

		this.retryOptions = Objects.requireNonNull(retryOptions, "'retryOptions' cannot be null.");
		this.retryPolicy = getRetryPolicy(retryOptions);
	}

	@Override
	public Mono<Registration> createRegistrationAsync(Registration registration) {
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations"	+ API_VERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));

			StringEntity entity = new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(Registration.parse(response.getEntity().getContent()));
                        } catch (Exception e) {
                            sink.error(e);
                        } finally {
                            post.releaseConnection();
                        }
                    }
                    public void failed(final Exception ex) {
                        post.releaseConnection();
                        sink.error(ex);
                    }
                    public void cancelled() {
                        post.releaseConnection();
                        sink.error(new RuntimeException("Operation was cancelled."));
                    }
                })), retryOptions.getTryTimeout(), retryPolicy);

		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<String> createRegistrationIdAsync(){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrationids"+ API_VERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        String location = response.getFirstHeader(CONTENT_LOCATION_HEADER).getValue();
                        Pattern extractId = Pattern.compile("(\\S+)/registrationids/([^?]+).*");
                        Matcher m = extractId.matcher(location);
                        if (m.matches()) {
                            String id = m.group(2);
                            sink.success(id);
                        }
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        post.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    post.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    post.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
            return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Registration> updateRegistrationAsync(Registration registration){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + API_VERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
			put.setHeader("If-Match", registration.getEtag() == null ? "*"	: "W/\"" + registration.getEtag() + "\"");
			put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(Registration.parse(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        put.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    put.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    put.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
        })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Registration> upsertRegistrationAsync(Registration registration){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + API_VERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
			put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(Registration.parse(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        put.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    put.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    put.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Void> deleteRegistrationAsync(String registrationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + API_VERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
			delete.setHeader("If-Match", "*");

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200 && httpStatusCode!=404) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success();
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        delete.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    delete.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    delete.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Void> deleteRegistrationAsync(Registration registration){
		return deleteRegistrationAsync(registration.getRegistrationId());
	}

	@Override
	public Mono<Registration> getRegistrationAsync(String registrationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + API_VERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(Registration.parse(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        get.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    get.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    get.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<CollectionResult> getRegistrationsAsync(int top, String continuationToken) {
		String queryUri = endpoint + hubPath + "/registrations" + API_VERSION + getQueryString(top, continuationToken);
		return retrieveRegistrationCollectionAsync(queryUri);
	}

	@Override
    public Mono<CollectionResult> getRegistrationsAsync() {
	    return getRegistrationsAsync(0, null);
    }

	@Override
	public Mono<CollectionResult> getRegistrationsByTagAsync(String tag, int top,	String continuationToken) {
		String queryUri = endpoint + hubPath + "/tags/" + tag
				+ "/registrations" + API_VERSION
				+ getQueryString(top, continuationToken);
		return retrieveRegistrationCollectionAsync(queryUri);
	}

	@Override
	public Mono<CollectionResult> getRegistrationsByTagAsync(String tag) {
		return getRegistrationsByTagAsync(tag, 0, null);
	}

	@Override
	public Mono<CollectionResult> getRegistrationsByChannelAsync(String channel, int top, String continuationToken) {
		String queryUri;
		try {
			String channelQuery = URLEncoder.encode("ChannelUri eq '" + channel	+ "'", "UTF-8");
			queryUri = endpoint + hubPath + "/registrations" + API_VERSION
					+ "&$filter=" + channelQuery
					+ getQueryString(top, continuationToken);
		} catch (UnsupportedEncodingException e) {
			return Mono.error(new RuntimeException(e));
		}
		return retrieveRegistrationCollectionAsync(queryUri);
	}

	@Override
	public Mono<CollectionResult> getRegistrationsByChannelAsync(String channel) {
		return getRegistrationsByChannelAsync(channel, 0, null);
	}

	private String getQueryString(int top, String continuationToken) {
		StringBuilder buf = new StringBuilder();
		if (top > 0) {
			buf.append("&$top=");
			buf.append(top);
		}
		if (continuationToken != null) {
			buf.append("&ContinuationToken=");
			buf.append(continuationToken);
		}
		return buf.toString();
	}

	private Mono<CollectionResult> retrieveRegistrationCollectionAsync(String queryUri) {
		try {
			URI uri = new URI(queryUri);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        CollectionResult result = Registration.parseRegistrations(response.getEntity().getContent());
                        Header contTokenHeader = response.getFirstHeader("X-MS-ContinuationToken");
                        if (contTokenHeader != null) {
                            result.setContinuationToken(contTokenHeader.getValue());
                        }

                        sink.success(result);
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        get.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    get.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    get.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<NotificationOutcome> sendNotificationAsync(Notification notification) {
		return scheduleNotificationWithResult(notification, "", null);
	}

	@Override
	public Mono<NotificationOutcome> sendNotificationAsync(Notification notification, Set<String> tags) {
		return scheduleNotificationWithResult(notification, tags, null);
	}

	@Override
	public Mono<NotificationOutcome> sendNotificationAsync(Notification notification, String tagExpression) {
		return scheduleNotificationWithResult(notification, tagExpression, null);
	}

	@Override
	public Mono<NotificationOutcome> scheduleNotificationAsync(Notification notification, Date scheduledTime) {
		return scheduleNotificationWithResult(notification, "", scheduledTime);
	}

	@Override
	public Mono<NotificationOutcome> scheduleNotificationAsync(Notification notification, Set<String> tags, Date scheduledTime) {
		return scheduleNotificationWithResult(notification, tags, scheduledTime);
	}

	private Mono<NotificationOutcome> scheduleNotificationWithResult(Notification notification, Set<String> tags, Date scheduledTime) {
		if (tags.isEmpty())
			throw new IllegalArgumentException(
					"tags has to contain at least an element");

		StringBuilder exp = new StringBuilder();
		for (Iterator<String> iterator = tags.iterator(); iterator.hasNext();) {
			exp.append(iterator.next());
			if (iterator.hasNext())
				exp.append(" || ");
		}

		return scheduleNotificationWithResult(notification, exp.toString(), scheduledTime);
	}

	private Mono<NotificationOutcome> scheduleNotificationWithResult(Notification notification, String tagExpression, Date scheduledTime){
		try {
			URI uri = new URI(endpoint + hubPath + (scheduledTime == null ? "/messages" : "/schedulednotifications") + API_VERSION);
			final HttpPost post = new HttpPost(uri);
			final String trackingId = java.util.UUID.randomUUID().toString();
			post.setHeader("Authorization", generateSasToken(uri));
			post.setHeader(TRACKING_ID_HEADER, trackingId);

			if(scheduledTime != null){
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

			return withRetry(process(post, trackingId), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<NotificationOutcome> scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime){
		return scheduleNotificationWithResult(notification, tagExpression, scheduledTime);
	}

	@Override
	public Mono<Void> cancelScheduledNotificationAsync(String notificationId) {
		try {
			URI uri = new URI(endpoint + hubPath + "/schedulednotifications/" + notificationId + API_VERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200 && httpStatusCode!=404) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success();
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        delete.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    delete.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    delete.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<NotificationOutcome> sendDirectNotificationAsync(Notification notification, String deviceHandle) {
		return sendDirectNotificationWithResult(notification, deviceHandle);
	}

	private Mono<NotificationOutcome> sendDirectNotificationWithResult(Notification notification, String deviceHandle) {
		try {
			URI uri = new URI(endpoint + hubPath + "/messages" + API_VERSION + "&direct");
			final HttpPost post = new HttpPost(uri);
			final String trackingId = java.util.UUID.randomUUID().toString();
			post.setHeader("ServiceBusNotification-DeviceHandle", deviceHandle);
			post.setHeader("Authorization", generateSasToken(uri));
			post.setHeader(TRACKING_ID_HEADER, trackingId);

			for (String header : notification.getHeaders().keySet()) {
				post.setHeader(header, notification.getHeaders().get(header));
			}

			post.setEntity(new StringEntity(notification.getBody(), notification.getContentType()));

			return withRetry(process(post, trackingId), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<NotificationOutcome> sendDirectNotificationAsync(Notification notification, List<String> deviceHandles) {
		return sendDirectNotificationWithResult(notification, deviceHandles);
	}

	private Mono<NotificationOutcome> sendDirectNotificationWithResult(Notification notification, List<String> deviceHandles) {
		try {
			URI uri = new URI(endpoint + hubPath + "/messages/$batch" + API_VERSION + "&direct");
			final HttpPost post = new HttpPost(uri);
			final String trackingId = java.util.UUID.randomUUID().toString();
			post.setHeader("Authorization", generateSasToken(uri));
			post.setHeader(TRACKING_ID_HEADER, trackingId);

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
			return withRetry(process(post, trackingId), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
            return Mono.error(new RuntimeException(e));
		}

	}

	private Mono<NotificationOutcome> process(HttpPost post, String trackingId){
		return Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse response) {
                try {
                    int httpStatusCode = response.getStatusLine().getStatusCode();

                    if (httpStatusCode != 201) {
                        String msg = "";
                        if (response.getEntity() != null&& response.getEntity().getContent() != null) {
                            msg = IOUtils.toString(response.getEntity().getContent());
                        }

                        if (httpStatusCode == 429 || httpStatusCode == 403) {
                            throw new QuotaExceededException("Error: " + response.getStatusLine(), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        throw new NotificationHubsException("Error: " + response.getStatusLine()	+ " body: " + msg, httpStatusCode, RetryUtil.parseRetryAfter(response));
                    }

                    String notificationId = null;
                    Header locationHeader = response.getFirstHeader(CONTENT_LOCATION_HEADER);
                    if(locationHeader != null){
                        URI location = new URI(locationHeader.getValue());
                        String[] segments = location.getPath().split("/");
                        notificationId = segments[segments.length-1];
                    }

                    sink.success(new NotificationOutcome(trackingId, notificationId));
                } catch (Exception e) {
                    sink.error(e);
                } finally {
                    post.releaseConnection();
                }
            }
            public void failed(final Exception ex) {
                post.releaseConnection();
                sink.error(ex);
            }
            public void cancelled() {
                post.releaseConnection();
                sink.error(new RuntimeException("Operation was cancelled."));
            }
        }));
	}

	@Override
	public Mono<NotificationTelemetry> getNotificationTelemetryAsync(String notificationId) {
		try {
			URI uri = new URI(endpoint + hubPath + "/messages/"	+ notificationId + API_VERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(NotificationTelemetry.parseOne(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        get.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    get.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    get.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Void> createOrUpdateInstallationAsync(Installation installation){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installation.getInstallationId() + API_VERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));

			StringEntity entity = new StringEntity(installation.toJson(), ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success();
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        put.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    put.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    put.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Void> patchInstallationAsync(String installationId, PartialUpdateOperation... operations) {
		return patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations));
	}

	@Override
	public Mono<Void> patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations) {
		return patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations));
	}

	private Mono<Void> patchInstallationInternalAsync(String installationId, String operationsJson){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
			final HttpPatch patch = new HttpPatch(uri);
			patch.setHeader("Authorization", generateSasToken(uri));

			StringEntity entity = new StringEntity(operationsJson, ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			patch.setEntity(entity);

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(patch, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success();
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        patch.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    patch.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    patch.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);

		} catch (Exception e) {
			 return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Void> deleteInstallationAsync(String installationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 204) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success();
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        delete.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    delete.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    delete.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);

		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<Installation> getInstallationAsync(String installationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + API_VERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(Installation.fromJson(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        get.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    get.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    get.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<NotificationHubJob> submitNotificationHubJobAsync(NotificationHubJob job){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + API_VERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));

			StringEntity entity = new StringEntity(job.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);

			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 201) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(NotificationHubJob.parseOne(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        post.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    post.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    post.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<NotificationHubJob> getNotificationHubJobAsync(String jobId){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs/"	+ jobId + API_VERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(NotificationHubJob.parseOne(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        get.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    get.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    get.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public Mono<List<NotificationHubJob>> getAllNotificationHubJobsAsync(){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + API_VERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			return withRetry(Mono.create(sink -> HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    try{
                        int httpStatusCode = response.getStatusLine().getStatusCode();
                        if (httpStatusCode != 200) {
                            throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
                        }

                        sink.success(NotificationHubJob.parseCollection(response.getEntity().getContent()));
                    } catch (Exception e) {
                        sink.error(e);
                    } finally {
                        get.releaseConnection();
                    }
                }
                public void failed(final Exception ex) {
                    get.releaseConnection();
                    sink.error(ex);
                }
                public void cancelled() {
                    get.releaseConnection();
                    sink.error(new RuntimeException("Operation was cancelled."));
                }
            })), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			 return Mono.error(new RuntimeException(e));
		}
	}

	private String getErrorString(HttpResponse response)
			throws IllegalStateException, IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		String body = writer.toString();
		return "Error: " + response.getStatusLine() + " - " + body;
	}

	private String generateSasToken(URI uri) {
		String targetUri;
		try {
			targetUri = URLEncoder
					.encode(uri.toString().toLowerCase(), "UTF-8")
					.toLowerCase();

			long expiresOnDate = System.currentTimeMillis();
			expiresOnDate += SdkGlobalSettings.getAuthorizationTokenExpirationInMinutes() * 60 * 1000;
			long expires = expiresOnDate / 1000;
			String toSign = targetUri + "\n" + expires;

			// Get an hmac_sha1 key from the raw key bytes
			byte[] keyBytes = SasKeyValue.getBytes(StandardCharsets.UTF_8);
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));

			// Convert raw bytes to Hex
			String signature = URLEncoder.encode(
					Base64.encodeBase64String(rawHmac), "UTF-8");

			// construct authorization string
            return "SharedAccessSignature sr=" + targetUri + "&sig="
                    + signature + "&se=" + expires + "&skn=" + SasKeyName;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
