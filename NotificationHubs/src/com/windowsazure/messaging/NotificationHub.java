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
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * 
 * Class implementing the INotificationHub interface.
 *
 */
public class NotificationHub implements INotificationHub {
	
	
	private static final String APIVERSION = "?api-version=2014-09";
	private static final String CONTENT_LOCATION_HEADER = "Location";
	private static final String TRACKING_ID_HEADER = "TrackingId";
	private String endpoint;
	private String hubPath;
	private String SasKeyName;
	private String SasKeyValue;
	
	public NotificationHub(String connectionString, String hubPath) {
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
	}
	
	@Override
	public void createRegistrationAsync(Registration registration, final FutureCallback<Registration> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations"	+ APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			StringEntity entity = new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);
			
			HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{		        		       		
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public Registration createRegistration(Registration registration)  throws NotificationHubsException{
		SyncCallback<Registration> callback = new SyncCallback<Registration>();
		createRegistrationAsync(registration, callback);
		return callback.getResult();
	}
	
	@Override
	public void createRegistrationIdAsync(final FutureCallback<String> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrationids"+ APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 201) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
		        			return;
		    			}	
		        		
			        	String location = response.getFirstHeader(CONTENT_LOCATION_HEADER).getValue();
						Pattern extractId = Pattern.compile("(\\S+)/registrationids/([^?]+).*");
						Matcher m = extractId.matcher(location);
						m.matches();
						String id = m.group(2);
						callback.completed(id);
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
	public String createRegistrationId()  throws NotificationHubsException{
		SyncCallback<String> callback = new SyncCallback<String>();
		createRegistrationIdAsync(callback);
		return callback.getResult();
	}

	@Override
	public void updateRegistrationAsync(Registration registration, final FutureCallback<Registration> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
			put.setHeader("If-Match", registration.getEtag() == null ? "*"	: "W/\"" + registration.getEtag() + "\"");
			put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));
			
			HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public Registration updateRegistration(Registration registration)  throws NotificationHubsException{
		SyncCallback<Registration> callback = new SyncCallback<Registration>();
		updateRegistrationAsync(registration, callback);
		return callback.getResult();
	}
	
	@Override
	public void upsertRegistrationAsync(Registration registration, final FutureCallback<Registration> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
			put.setEntity(new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML));
			
			HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public Registration upsertRegistration(Registration registration)  throws NotificationHubsException{
		SyncCallback<Registration> callback = new SyncCallback<Registration>();
		upsertRegistrationAsync(registration, callback);
		return callback.getResult();
	}

	@Override
	public void deleteRegistrationAsync(Registration registration, final FutureCallback<Object> callback){
		deleteRegistrationAsync(registration.getRegistrationId(), callback);
	}
	
	@Override
	public void deleteRegistration(Registration registration)  throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		deleteRegistrationAsync(registration, callback);
		callback.getResult();
	}	
	
	@Override
	public void deleteRegistrationAsync(String registrationId, final FutureCallback<Object> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
			delete.setHeader("If-Match", "*");
			
			HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200 && httpStatusCode!=404) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public void deleteRegistration(String registrationId)  throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		deleteRegistrationAsync(registrationId, callback);
		callback.getResult();
	}

	@Override
	public void getRegistrationAsync(String registrationId, final FutureCallback<Registration> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registrationId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public Registration getRegistration(String registrationId)  throws NotificationHubsException{
		SyncCallback<Registration> callback = new SyncCallback<Registration>();
		getRegistrationAsync(registrationId, callback);
		return callback.getResult();
	}

	@Override
	public void getRegistrationsAsync(int top, String continuationToken, final FutureCallback<CollectionResult> callback) {
		String queryUri = endpoint + hubPath + "/registrations" + APIVERSION + getQueryString(top, continuationToken);
		retrieveRegistrationCollectionAsync(queryUri, callback);
	}
	
	@Override
	public CollectionResult getRegistrations(int top, String continuationToken)  throws NotificationHubsException{
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsAsync(top, continuationToken, callback);
		return callback.getResult();
	}
	
	@Override
	public CollectionResult getRegistrations()  throws NotificationHubsException{
		return getRegistrations(0, null);
	}
	
	@Override
	public void getRegistrationsByTagAsync(String tag, int top,	String continuationToken, final FutureCallback<CollectionResult> callback) {
		String queryUri = endpoint + hubPath + "/tags/" + tag
				+ "/registrations" + APIVERSION
				+ getQueryString(top, continuationToken);
		retrieveRegistrationCollectionAsync(queryUri, callback);
	}
	
	@Override
	public CollectionResult getRegistrationsByTag(String tag, int top,	String continuationToken)  throws NotificationHubsException{
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsByTagAsync(tag, top, continuationToken, callback);
		return callback.getResult();
	}
	
	@Override
	public void getRegistrationsByTagAsync(String tag, final FutureCallback<CollectionResult> callback) {
		getRegistrationsByTagAsync(tag, 0, null, callback);
	}
	
	@Override
	public CollectionResult getRegistrationsByTag(String tag)  throws NotificationHubsException{
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsByTagAsync(tag, callback);
		return callback.getResult();
	}
	
	@Override
	public void getRegistrationsByChannelAsync(String channel, int top, String continuationToken, final FutureCallback<CollectionResult> callback) {
		String queryUri = null;
		try {
			String channelQuery = URLEncoder.encode("ChannelUri eq '" + channel	+ "'", "UTF-8");
			queryUri = endpoint + hubPath + "/registrations" + APIVERSION
					+ "&$filter=" + channelQuery
					+ getQueryString(top, continuationToken);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		retrieveRegistrationCollectionAsync(queryUri, callback);
	}

	@Override
	public CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken)  throws NotificationHubsException{
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsByChannelAsync(channel, top, continuationToken, callback);
		return callback.getResult();
	}

	@Override
	public void getRegistrationsByChannelAsync(String channel, final FutureCallback<CollectionResult> callback) {
		getRegistrationsByChannelAsync(channel, 0, null, callback);
	}
	
	@Override
	public CollectionResult getRegistrationsByChannel(String channel)  throws NotificationHubsException{
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsByChannelAsync(channel, callback);
		return callback.getResult();
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
	
	private void retrieveRegistrationCollectionAsync(String queryUri, final FutureCallback<CollectionResult> callback) {
		try {
			URI uri = new URI(queryUri);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public NotificationOutcome sendNotification(Notification notification)  throws NotificationHubsException{
		SyncCallback<NotificationOutcome> callback = new SyncCallback<NotificationOutcome>();
		sendNotificationAsync(notification, callback);
		return callback.getResult();		
	}

	@Override
	public void sendNotificationAsync(Notification notification, Set<String> tags, FutureCallback<NotificationOutcome> callback) {
		scheduleNotificationAsync(notification, tags, null, callback);
	}
	
	@Override
	public NotificationOutcome sendNotification(Notification notification, Set<String> tags)  throws NotificationHubsException{
		SyncCallback<NotificationOutcome> callback = new SyncCallback<NotificationOutcome>();
		sendNotificationAsync(notification, tags, callback);
		return callback.getResult();
	}
	
	@Override
	public void sendNotificationAsync(Notification notification, String tagExpression, FutureCallback<NotificationOutcome> callback) {
		scheduleNotificationAsync(notification, tagExpression, null, callback);
	}

	@Override
	public NotificationOutcome sendNotification(Notification notification, String tagExpression)  throws NotificationHubsException{
		SyncCallback<NotificationOutcome> callback = new SyncCallback<NotificationOutcome>();
		sendNotificationAsync(notification, tagExpression, callback);
		return callback.getResult();
	}
	
	@Override
	public void scheduleNotificationAsync(Notification notification, Date scheduledTime, FutureCallback<NotificationOutcome> callback) {
		scheduleNotificationAsync(notification, "", scheduledTime, callback);
	}
	
	@Override
	public NotificationOutcome scheduleNotification(Notification notification,	Date scheduledTime)  throws NotificationHubsException{
		SyncCallback<NotificationOutcome> callback = new SyncCallback<NotificationOutcome>();
		scheduleNotificationAsync(notification, scheduledTime, callback);
		return callback.getResult();
	}
	
	@Override
	public void scheduleNotificationAsync(Notification notification, Set<String> tags, Date scheduledTime, FutureCallback<NotificationOutcome> callback) {
		if (tags.isEmpty())
			throw new IllegalArgumentException(
					"tags has to contain at least an element");

		StringBuffer exp = new StringBuffer();
		for (Iterator<String> iterator = tags.iterator(); iterator.hasNext();) {
			exp.append(iterator.next());
			if (iterator.hasNext())
				exp.append(" || ");
		}

		scheduleNotificationAsync(notification, exp.toString(), scheduledTime, callback);
	}

	@Override
	public NotificationOutcome scheduleNotification(Notification notification,	Set<String> tags, Date scheduledTime)  throws NotificationHubsException{
		SyncCallback<NotificationOutcome> callback = new SyncCallback<NotificationOutcome>();
		scheduleNotificationAsync(notification, tags, scheduledTime, callback);
		return callback.getResult();		
	}

	@Override
	public void scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime, final FutureCallback<NotificationOutcome> callback){
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
			
			HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();		        		
		        		if (httpStatusCode != 201) {
		    				String msg = "";
		    				if (response.getEntity() != null&& response.getEntity().getContent() != null) {
		    					msg = IOUtils.toString(response.getEntity().getContent());
		    				}
		    				callback.failed(new NotificationHubsException("Error: " + response.getStatusLine()	+ " body: " + msg, httpStatusCode));
		    				return;
		    			}
		        		
		        		String notificationId = null;
		        		Header locationHeader = response.getFirstHeader(CONTENT_LOCATION_HEADER);		        		
		        		if(locationHeader != null){
		        			URI location = new URI(locationHeader.getValue());
		        			String[] segments = location.getPath().split("/");
		        			notificationId = segments[segments.length-1];
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
	public NotificationOutcome scheduleNotification(Notification notification,	String tagExpression, Date scheduledTime)  throws NotificationHubsException{
		SyncCallback<NotificationOutcome> callback = new SyncCallback<NotificationOutcome>();
		scheduleNotificationAsync(notification, tagExpression, scheduledTime, callback);
		return callback.getResult();
	}	
	
	@Override
	public void createOrUpdateInstallationAsync(Installation installation, final FutureCallback<Object> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installation.getInstallationId() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
						
			StringEntity entity = new StringEntity(installation.toJson(), ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);
			
			HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public void createOrUpdateInstallation(Installation installation)  throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		createOrUpdateInstallationAsync(installation, callback);
		callback.getResult();
	}
	
	@Override
	public void patchInstallationAsync(String installationId, FutureCallback<Object> callback, PartialUpdateOperation... operations) {
		patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations), callback);
	}
	
	@Override
	public void patchInstallation(String installationId, PartialUpdateOperation... operations)  throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		patchInstallationAsync(installationId, callback, operations);
		callback.getResult();
	}

	@Override
	public void patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations, FutureCallback<Object> callback) {
		patchInstallationInternalAsync(installationId, PartialUpdateOperation.toJson(operations), callback);
	}
	
	@Override
	public void patchInstallation(String installationId, List<PartialUpdateOperation> operations)  throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		patchInstallationAsync(installationId, operations, callback);
		callback.getResult();
	}

	private void patchInstallationInternalAsync(String installationId, String operationsJson, final FutureCallback<Object> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			final HttpPatch patch = new HttpPatch(uri);
			patch.setHeader("Authorization", generateSasToken(uri));
						
			StringEntity entity = new StringEntity(operationsJson, ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			patch.setEntity(entity);
			
			HttpClientManager.getHttpAsyncClient().execute(patch, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public void deleteInstallationAsync(String installationId, final FutureCallback<Object> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 204) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public void deleteInstallation(String installationId)  throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		deleteInstallationAsync(installationId, callback);
		callback.getResult();
	}

	@Override
	public void getInstallationAsync(String installationId, final FutureCallback<Installation> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public Installation getInstallation(String installationId)  throws NotificationHubsException{
		SyncCallback<Installation> callback = new SyncCallback<Installation>();
		getInstallationAsync(installationId, callback);
		return callback.getResult();
	}
	
	@Override
	public void submitNotificationHubJobAsync(NotificationHubJob job, final FutureCallback<NotificationHubJob> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			StringEntity entity = new StringEntity(job.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);
			
			HttpClientManager.getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 201) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public NotificationHubJob submitNotificationHubJob(NotificationHubJob job)  throws NotificationHubsException{
		SyncCallback<NotificationHubJob> callback = new SyncCallback<NotificationHubJob>();
		submitNotificationHubJobAsync(job, callback);
		return callback.getResult();
	}

	@Override
	public void getNotificationHubJobAsync(String jobId, final FutureCallback<NotificationHubJob> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs/"	+ jobId + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public NotificationHubJob getNotificationHubJob(String jobId)  throws NotificationHubsException{
		SyncCallback<NotificationHubJob> callback = new SyncCallback<NotificationHubJob>();
		getNotificationHubJobAsync(jobId, callback);
		return callback.getResult();
	}

	@Override
	public void getAllNotificationHubJobsAsync(final FutureCallback<List<NotificationHubJob>> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	public List<NotificationHubJob> getAllNotificationHubJobs()  throws NotificationHubsException{
		SyncCallback<List<NotificationHubJob>> callback = new SyncCallback<List<NotificationHubJob>>();
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
