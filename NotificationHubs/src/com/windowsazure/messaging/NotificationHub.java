//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.StringBody;

import com.google.gson.GsonBuilder;

import reactor.core.publisher.Mono;

import static com.windowsazure.messaging.RetryUtil.getRetryPolicy;
import static com.windowsazure.messaging.RetryUtil.withRetry;

/**
 * 
 * Class implementing the INotificationHub interface.
 *
 */
public class NotificationHub implements INotificationHub {
		
	private static final String APIVERSION = "?api-version=2020-06";
	private static final String CONTENT_LOCATION_HEADER = "Location";
	private static final String TRACKING_ID_HEADER = "TrackingId";
    private final RetryOptions retryOptions;
    private final RetryPolicy retryPolicy;
	private String endpoint;
	private String hubPath;
	private String SasKeyName;
	private String SasKeyValue;	
	
	public NotificationHub(String connectionString, String hubPath, RetryOptions retryOptions) {
		this.hubPath = hubPath;

		String[] parts = connectionString.split(";");
		if (parts.length != 3)
			throw new RuntimeException("Error parsing connection string: "
					+ connectionString);

		for (int i = 0; i < parts.length; i++) {
			if (parts[i].startsWith("Endpoint")) {
				this.endpoint = "https" + parts[i].substring(11);
			} else if (parts[i].startsWith("SharedAccessKeyName")) {
				this.SasKeyName = parts[i].substring(20);
			} else if (parts[i].startsWith("SharedAccessKey")) {
				this.SasKeyValue = parts[i].substring(16);
			}
		}
		
		this.retryOptions = Objects.requireNonNull(retryOptions, "'retryOptions' cannot be null.");
		this.retryPolicy = getRetryPolicy(retryOptions);
	}
	
	@Override
	public Mono<Registration> createRegistrationAsync(Registration registration){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations"	+ APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			StringEntity entity = new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);
			
			return Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{		        		       		
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});
			});
						
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public Registration createRegistration(Registration registration)  throws NotificationHubsException{
		return createRegistrationAsync(registration).block();
	}

	@Override
	public Mono<String> createRegistrationIdAsync(){
		try {			
			URI uri = new URI(endpoint + hubPath + "/registrationids"+ APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			return Mono.create(sink -> {		    	
				HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 201) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
			    			}	
			        		
				        	String location = response.getFirstHeader(CONTENT_LOCATION_HEADER).getValue();
							Pattern extractId = Pattern.compile("(\\S+)/registrationids/([^?]+).*");
							Matcher m = extractId.matcher(location);
							m.matches();
							String id = m.group(2);
							sink.success(id);
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
				});
			});
		} catch (Exception e) {
            return Mono.error(new RuntimeException(e));
		}
	}

	@Override
	public String createRegistrationId()  throws NotificationHubsException{
		return createRegistrationIdAsync().block();
	}
	
	@Override
	public Mono<Registration> updateRegistrationAsync(Registration registration){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
			put.setHeader("If-Match", registration.getEtag() == null ? "*"	: "W/\"" + registration.getEtag() + "\"");
			put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));
			
			return  Mono.create(sink -> {		
					HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
				        public void completed(final HttpResponse response) {
				        	try{
				        		int httpStatusCode = response.getStatusLine().getStatusCode();
				        		if (httpStatusCode != 200) {
				        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});				
			});			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}
	
	@Override
	public Registration updateRegistration(Registration registration)  throws NotificationHubsException{
		return updateRegistrationAsync(registration).block();
	}
	
	@Override
	public Mono<Registration> upsertRegistrationAsync(Registration registration){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
			put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));

			return  Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});		
			});	
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public Registration upsertRegistration(Registration registration)  throws NotificationHubsException{
		return upsertRegistrationAsync(registration).block();
	}

	@Override
	public Mono<Void> deleteRegistrationAsync(String registrationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
			delete.setHeader("If-Match", "*");
			
			return  Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200 && httpStatusCode!=404) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});
			});
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public void deleteRegistration(Registration registration)  throws NotificationHubsException{
		deleteRegistrationAsync(registration.getRegistrationId()).block();
	}
	
	@Override
	public Mono<Void> deleteRegistrationAsync(Registration registration){
		return deleteRegistrationAsync(registration.getRegistrationId());
	}
	
	@Override
	public void deleteRegistration(String registrationId)  throws NotificationHubsException{
		deleteRegistrationAsync(registrationId).block();
	}
	
	@Override
	public Mono<Registration> getRegistrationAsync(String registrationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			return  Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});	
			});
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public Registration getRegistration(String registrationId)  throws NotificationHubsException{
		return getRegistrationAsync(registrationId).block();
	}

	@Override
	public Mono<CollectionResult> getRegistrationsAsync(int top, String continuationToken) {
		String queryUri = endpoint + hubPath + "/registrations" + APIVERSION + getQueryString(top, continuationToken);
		return retrieveRegistrationCollectionAsync(queryUri);
	}
	
	@Override
	public CollectionResult getRegistrations(int top, String continuationToken)  throws NotificationHubsException{
		String queryUri = endpoint + hubPath + "/registrations" + APIVERSION + getQueryString(top, continuationToken);
		return retrieveRegistrationCollectionAsync(queryUri).block();
	}
	
	@Override
	public CollectionResult getRegistrations()  throws NotificationHubsException{
		return getRegistrations(0, null);
	}
	
	@Override
	public Mono<CollectionResult> getRegistrationsByTagAsync(String tag, int top,	String continuationToken) {
		String queryUri = endpoint + hubPath + "/tags/" + tag
				+ "/registrations" + APIVERSION
				+ getQueryString(top, continuationToken);
		return retrieveRegistrationCollectionAsync(queryUri);
	}
	
	@Override
	public CollectionResult getRegistrationsByTag(String tag, int top,	String continuationToken)  throws NotificationHubsException{
		String queryUri = endpoint + hubPath + "/tags/" + tag
				+ "/registrations" + APIVERSION
				+ getQueryString(top, continuationToken);
		return retrieveRegistrationCollectionAsync(queryUri).block();
	}
	
	@Override
	public Mono<CollectionResult> getRegistrationsByTagAsync(String tag) {
		return getRegistrationsByTagAsync(tag, 0, null);
	}
	
	@Override
	public CollectionResult getRegistrationsByTag(String tag)  throws NotificationHubsException{
		return getRegistrationsByTagAsync(tag).block();
	}
	
	@Override
	public Mono<CollectionResult> getRegistrationsByChannelAsync(String channel, int top, String continuationToken) {
		String queryUri = null;
		try {
			String channelQuery = URLEncoder.encode("ChannelUri eq '" + channel	+ "'", "UTF-8");
			queryUri = endpoint + hubPath + "/registrations" + APIVERSION
					+ "&$filter=" + channelQuery
					+ getQueryString(top, continuationToken);
		} catch (UnsupportedEncodingException e) {
			return Mono.error(new RuntimeException(e));
		}
		return retrieveRegistrationCollectionAsync(queryUri);
	}

	@Override
	public CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken)  throws NotificationHubsException{
		return getRegistrationsByChannelAsync(channel, top, continuationToken).block();
	}

	@Override
	public Mono<CollectionResult> getRegistrationsByChannelAsync(String channel) {
		return getRegistrationsByChannelAsync(channel, 0, null);
	}
	
	@Override
	public CollectionResult getRegistrationsByChannel(String channel)  throws NotificationHubsException{
		return getRegistrationsByChannelAsync(channel).block();
	}
	
	private String getQueryString(int top, String continuationToken) {
		StringBuffer buf = new StringBuffer();
		if (top > 0) {
			buf.append("&$top=" + top);
		}
		if (continuationToken != null) {
			buf.append("&ContinuationToken=" + continuationToken);
		}
		return buf.toString();
	}
	
	private Mono<CollectionResult> retrieveRegistrationCollectionAsync(String queryUri) {
		try {
			URI uri = new URI(queryUri);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			return  Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});
			});			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public Mono<Void> sendNotificationAsync(Notification notification) {
		return scheduleNotificationAsync(notification, "", null);
	}
	
	@Override
	public NotificationOutcome sendNotification(Notification notification)  throws NotificationHubsException{
		return scheduleNotificationWithResult(notification, "", null).block();
	}

	@Override
	public Mono<Void> sendNotificationAsync(Notification notification, Set<String> tags) {
		return scheduleNotificationAsync(notification, tags, null);
	}
	
	@Override
	public NotificationOutcome sendNotification(Notification notification, Set<String> tags)  throws NotificationHubsException{
		return scheduleNotificationWithResult(notification, tags, null).block();
	}
	
	@Override
	public Mono<Void> sendNotificationAsync(Notification notification, String tagExpression) {
		return scheduleNotificationAsync(notification, tagExpression, null);
	}

	@Override
	public NotificationOutcome sendNotification(Notification notification, String tagExpression)  throws NotificationHubsException{
		return scheduleNotificationWithResult(notification, tagExpression, null).block();
	}
	
	@Override
	public Mono<Void> scheduleNotificationAsync(Notification notification, Date scheduledTime) {
		return scheduleNotificationAsync(notification, "", scheduledTime);
	}
	
	@Override
	public NotificationOutcome scheduleNotification(Notification notification,	Date scheduledTime)  throws NotificationHubsException{		
		return scheduleNotificationWithResult(notification, "", scheduledTime).block();
	}
	
	@Override
	public Mono<Void> scheduleNotificationAsync(Notification notification, Set<String> tags, Date scheduledTime) {
		return scheduleNotificationWithResult(notification, tags, scheduledTime).then();
	}
	
	private Mono<NotificationOutcome> scheduleNotificationWithResult(Notification notification, Set<String> tags, Date scheduledTime) {
		if (tags.isEmpty())
			throw new IllegalArgumentException(
					"tags has to contain at least an element");

		StringBuffer exp = new StringBuffer();
		for (Iterator<String> iterator = tags.iterator(); iterator.hasNext();) {
			exp.append(iterator.next());
			if (iterator.hasNext())
				exp.append(" || ");
		}

		return scheduleNotificationWithResult(notification, exp.toString(), scheduledTime);
	}

	@Override
	public NotificationOutcome scheduleNotification(Notification notification,	Set<String> tags, Date scheduledTime)  throws NotificationHubsException{
		return scheduleNotificationWithResult(notification, tags, scheduledTime).block();		
	}

	private Mono<NotificationOutcome> scheduleNotificationWithResult(Notification notification, String tagExpression, Date scheduledTime){
		try {
			URI uri = new URI(endpoint + hubPath + (scheduledTime == null ? "/messages" : "/schedulednotifications") + APIVERSION);
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
	public Mono<Void> scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime){
		return scheduleNotificationWithResult(notification, tagExpression, scheduledTime).then();
	}	
	
	@Override
	public NotificationOutcome scheduleNotification(Notification notification,	String tagExpression, Date scheduledTime)  throws NotificationHubsException{
		return scheduleNotificationWithResult(notification, tagExpression, scheduledTime).block();
	}	
	
	@Override
	public void cancelScheduledNotification(String notificationId) throws NotificationHubsException{
		cancelScheduledNotificationAsync(notificationId).block();
	}

	@Override
	public Mono<Void> cancelScheduledNotificationAsync(String notificationId) {
		try {
			URI uri = new URI(endpoint + hubPath + "/schedulednotifications/" + notificationId + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
						
			return  Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
					public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200 && httpStatusCode!=404) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});	
			});
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}		
	}
	
	@Override
	public NotificationOutcome sendDirectNotification(Notification notification, String deviceHandle)	throws NotificationHubsException {		
		return sendDirectNotificationWithResult(notification, deviceHandle).block();
	}

	@Override
	public NotificationOutcome sendDirectNotification(Notification notification, List<String> deviceHandles) throws NotificationHubsException {
		return sendDirectNotificationWithResult(notification, deviceHandles).block();
	}

	@Override
	public Mono<Void> sendDirectNotificationAsync(Notification notification, String deviceHandle) {
		return sendDirectNotificationWithResult(notification, deviceHandle).then();
	}
	
	private Mono<NotificationOutcome> sendDirectNotificationWithResult(Notification notification, String deviceHandle) {
		try {
			URI uri = new URI(endpoint + hubPath + "/messages" + APIVERSION + "&direct");
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
	public Mono<Void> sendDirectNotificationAsync(Notification notification, List<String> deviceHandles) {
		return sendDirectNotificationWithResult(notification, deviceHandles).then();
	}
	
	private Mono<NotificationOutcome> sendDirectNotificationWithResult(Notification notification, List<String> deviceHandles) {
		try {
			URI uri = new URI(endpoint + hubPath + "/messages/$batch" + APIVERSION + "&direct");
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
		return Mono.create(sink -> {		    	
	    	HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{					        		
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
	    				
		        		if (httpStatusCode == 429) {
		        			throw new QuotaExceededException("Error: " + response.getStatusLine(), httpStatusCode);       
		        		}
		        		
		        		if (httpStatusCode != 201) {
		    				String msg = "";
		    				if (response.getEntity() != null&& response.getEntity().getContent() != null) {
		    					msg = IOUtils.toString(response.getEntity().getContent());
		    				}
		    				
		    				throw new NotificationHubsException("Error: " + response.getStatusLine()	+ " body: " + msg, httpStatusCode);
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
			});
	    });
	}
	
	@Override
	public NotificationTelemetry getNotificationTelemetry(String notificationId)
			throws NotificationHubsException {
		return getNotificationTelemetryAsync(notificationId).block();
	}

	@Override
	public Mono<NotificationTelemetry> getNotificationTelemetryAsync(String notificationId) {
		try {
			URI uri = new URI(endpoint + hubPath + "/messages/"	+ notificationId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			return  Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}	
	
	@Override
	public Mono<Void> createOrUpdateInstallationAsync(Installation installation){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installation.getInstallationId() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
						
			StringEntity entity = new StringEntity(installation.toJson(), ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);
			
			return  Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});
			});				
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public void createOrUpdateInstallation(Installation installation)  throws NotificationHubsException{
		createOrUpdateInstallationAsync(installation).block();
	}
	
	@Override
	public Mono<Void> patchInstallationAsync(String installationId, PartialUpdateOperation... operations) {
		return patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations));
	}
	
	@Override
	public void patchInstallation(String installationId, PartialUpdateOperation... operations)  throws NotificationHubsException{
		patchInstallationAsync(installationId, operations).block();
	}

	@Override
	public Mono<Void> patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations) {
		return patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations));
	}
	
	@Override
	public void patchInstallation(String installationId, List<PartialUpdateOperation> operations)  throws NotificationHubsException{
		patchInstallationAsync(installationId, operations).block();
	}

	private Mono<Void> patchInstallationInternalAsync(String installationId, String operationsJson){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			final HttpPatch patch = new HttpPatch(uri);
			patch.setHeader("Authorization", generateSasToken(uri));
						
			StringEntity entity = new StringEntity(operationsJson, ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			patch.setEntity(entity);

			return Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(patch, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});
			
		} catch (Exception e) {
			 return Mono.error(new RuntimeException(e));
		} 
	}	
	
	@Override
	public Mono<Void> deleteInstallationAsync(String installationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
			
			return Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 204) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});	
			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public void deleteInstallation(String installationId)  throws NotificationHubsException{
		deleteInstallationAsync(installationId).block();
	}

	@Override
	public Mono<Installation> getInstallationAsync(String installationId){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			return Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public Installation getInstallation(String installationId)  throws NotificationHubsException{
		return getInstallationAsync(installationId).block();
	}
	
	@Override
	public Mono<NotificationHubJob> submitNotificationHubJobAsync(NotificationHubJob job){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			StringEntity entity = new StringEntity(job.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);
			
			return Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 201) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public NotificationHubJob submitNotificationHubJob(NotificationHubJob job)  throws NotificationHubsException{
		return submitNotificationHubJobAsync(job).block();
	}

	@Override
	public Mono<NotificationHubJob> getNotificationHubJobAsync(String jobId){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs/"	+ jobId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			return Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});			
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public NotificationHubJob getNotificationHubJob(String jobId)  throws NotificationHubsException{		
		return getNotificationHubJobAsync(jobId).block();
	}

	@Override
	public Mono<List<NotificationHubJob>> getAllNotificationHubJobsAsync(){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			return Mono.create(sink -> {		
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode);
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
				});			
			});			
		} catch (Exception e) {
			 return Mono.error(new RuntimeException(e));
		} 
	}
	
	@Override
	public List<NotificationHubJob> getAllNotificationHubJobs()  throws NotificationHubsException{
		return getAllNotificationHubJobsAsync().block();
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
			byte[] keyBytes = SasKeyValue.getBytes("UTF-8");
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

			// Get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			// Compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(toSign.getBytes("UTF-8"));

			// Convert raw bytes to Hex
			String signature = URLEncoder.encode(
					Base64.encodeBase64String(rawHmac), "UTF-8");

			// construct authorization string
			String token = "SharedAccessSignature sr=" + targetUri + "&sig="
					+ signature + "&se=" + expires + "&skn=" + SasKeyName;
			return token;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
