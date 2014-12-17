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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * 
 * Class implementing the INotificationHub interface.
 *
 */
public class NotificationHub implements INotificationHub {
	
	private static CloseableHttpAsyncClient httpAsyncClient;
	private static final String APIVERSION = "?api-version=2014-09";
	private static final String CONTENT_LOCATION_HEADER = "Location";
	private String endpoint;
	private String hubPath;
	private String SasKeyName;
	private String SasKeyValue;

	private HttpClient httpClient;
	
	public NotificationHub(String connectionString, String hubPath) {
		this.httpClient = HttpClients.createDefault();
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
	
	private static CloseableHttpAsyncClient getHttpAsyncClient() {
	  if(httpAsyncClient == null) {
	     synchronized(NotificationHub.class) {
	       if(httpAsyncClient == null) {
	    	   CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
	    	   client.start();
	    	   httpAsyncClient = client;	    	   
	       }
	     }
	  }
		  
	  return httpAsyncClient;
	}
	
	public void setHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient) {
		synchronized(NotificationHub.class) {
			if(httpAsyncClient == null) {
				NotificationHub.httpAsyncClient = httpAsyncClient;
			}
			else{
				throw new RuntimeException("HttpAsyncClient was already set before or default one is being used.");
			}
		}
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpClient getHttpClient() {
		return this.httpClient;
	}		

	private String generateSasToken(URI uri) {
		String targetUri;
		try {
			targetUri = URLEncoder
					.encode(uri.toString().toLowerCase(), "UTF-8")
					.toLowerCase();

			long expiresOnDate = System.currentTimeMillis();
			int expiresInMins = 60; // 1 hour
			expiresOnDate += expiresInMins * 60 * 1000;
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

	@Override
	public void createRegistrationAsync(Registration registration, final FutureCallback<Registration> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations"	+ APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			StringEntity entity = new StringEntity(registration.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);
			
			getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public Registration createRegistration(Registration registration) {
		SyncCallback<Registration> callback = new SyncCallback<Registration>();
		createRegistrationAsync(registration, callback);
		return callback.getResult();
	}

	private String getErrorString(HttpResponse response)
			throws IllegalStateException, IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		String body = writer.toString();
		return "Error: " + response.getStatusLine() + " - " + body;
	}
	
	
	@Override
	public void createRegistrationIdAsync(final FutureCallback<String> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrationids"+ APIVERSION);
			final HttpPost post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			getHttpAsyncClient().execute(post, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 201) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public String createRegistrationId() {
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
			
			getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public Registration updateRegistration(Registration registration) {
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
			
			getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public Registration upsertRegistration(Registration registration) {
		SyncCallback<Registration> callback = new SyncCallback<Registration>();
		upsertRegistrationAsync(registration, callback);
		return callback.getResult();
	}

	@Override
	public void deleteRegistrationAsync(Registration registration, final FutureCallback<Object> callback){
		try {
			URI uri = new URI(endpoint + hubPath + "/registrations/" + registration.getRegistrationId() + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
			delete.setHeader("If-Match", registration.getEtag() == null ? "*" : "W/\"" + registration.getEtag() + "\"");
			
			getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200 && 
		    					response.getStatusLine().getStatusCode() != 404) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public void deleteRegistration(Registration registration) {
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
			
			getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200 && 
		    					response.getStatusLine().getStatusCode() != 404) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public void deleteRegistration(String registrationId) {
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
			
			getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	public Registration getRegistration(String registrationId) {
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
	public CollectionResult getRegistrations(int top, String continuationToken) {
		String queryUri = endpoint + hubPath + "/registrations" + APIVERSION + getQueryString(top, continuationToken);
		return retrieveRegistrationCollection(queryUri);
	}

	private void retrieveRegistrationCollectionAsync(String queryUri, final FutureCallback<CollectionResult> callback) {
		try {
			URI uri = new URI(queryUri);
			final HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
			
			getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		if (response.getStatusLine().getStatusCode() != 200) {
		        			callback.failed(new RuntimeException(getErrorString(response)));
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
	
	private CollectionResult retrieveRegistrationCollection(String queryUri) {
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		retrieveRegistrationCollectionAsync(queryUri, callback);
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

	@Override
	public void getRegistrationsByTagAsync(String tag, int top,	String continuationToken, final FutureCallback<CollectionResult> callback) {
		String queryUri = endpoint + hubPath + "/tags/" + tag
				+ "/registrations" + APIVERSION
				+ getQueryString(top, continuationToken);
		retrieveRegistrationCollectionAsync(queryUri, callback);
	}
	
	@Override
	public CollectionResult getRegistrationsByTag(String tag, int top,	String continuationToken) {
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsByTagAsync(tag, top, continuationToken, callback);
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
	public CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken) {
		SyncCallback<CollectionResult> callback = new SyncCallback<CollectionResult>();
		getRegistrationsByChannelAsync(channel, top, continuationToken, callback);
		return callback.getResult();
	}

	@Override
	public void sendNotification(Notification notification) {
		scheduleNotification(notification, "", null);
	}

	@Override
	public void sendNotification(Notification notification, Set<String> tags) {
		scheduleNotification(notification, tags, null);
	}

	@Override
	public void sendNotification(Notification notification, String tagExpression) {
		scheduleNotification(notification, tagExpression, null);
	}
	
	@Override
	public void scheduleNotification(Notification notification,	Date scheduledTime) {
		scheduleNotification(notification, "", scheduledTime);
	}

	@Override
	public void scheduleNotification(Notification notification,	Set<String> tags, Date sheduledTime) {
		if (tags.isEmpty())
			throw new IllegalArgumentException(
					"tags has to contain at least an element");

		StringBuffer exp = new StringBuffer();
		for (Iterator<String> iterator = tags.iterator(); iterator.hasNext();) {
			exp.append(iterator.next());
			if (iterator.hasNext())
				exp.append(" || ");
		}

		scheduleNotification(notification, exp.toString(), sheduledTime);		
	}

	@Override
	public void scheduleNotification(Notification notification,	String tagExpression, Date sheduledTime) {
		HttpPost post = null;
		try {
			URI uri = new URI(endpoint + hubPath + (sheduledTime == null ? "/messages" : "/schedulednotifications") + APIVERSION);
			post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));
			
			if(sheduledTime != null){
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				String scheduledTimeHeader = df.format(sheduledTime);
				post.setHeader("ServiceBusNotification-ScheduleTime", scheduledTimeHeader);
			}

			if (tagExpression != null && !"".equals(tagExpression)) {
				post.setHeader("ServiceBusNotification-Tags", tagExpression);
			}

			for (String header : notification.getHeaders().keySet()) {
				post.setHeader(header, notification.getHeaders().get(header));
			}

			post.setEntity(new StringEntity(notification.getBody(), notification.getContentType()));
			HttpResponse response = httpClient.execute(post);

			if (response.getStatusLine().getStatusCode() != 201) {
				String msg = "";
				if (response.getEntity() != null
						&& response.getEntity().getContent() != null) {
					msg = IOUtils.toString(response.getEntity().getContent());
				}
				throw new RuntimeException("Error: " + response.getStatusLine()
						+ " body: " + msg);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (post != null)
				post.releaseConnection();
		}		
	}	
	
	@Override
	public CollectionResult getRegistrations() {
		return getRegistrations(0, null);
	}

	@Override
	public CollectionResult getRegistrationsByTag(String tag) {
		return getRegistrationsByTag(tag, 0, null);
	}

	@Override
	public CollectionResult getRegistrationsByChannel(String channel) {
		return getRegistrationsByChannel(channel, 0, null);
	}

	@Override
	public void CreateOrUpdateInstallation(Installation installation) {
		HttpPut put = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installation.getInstallationId() + APIVERSION);
			put = new HttpPut(uri);
			put.setHeader("Authorization", generateSasToken(uri));
						
			StringEntity entity = new StringEntity(installation.toJson(), ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);
			HttpResponse response = httpClient.execute(put);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException(getErrorString(response));
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (put != null)
				put.releaseConnection();
		}
	}

	@Override
	public void PatchInstallation(String installationId, PartialUpdateOperation... operations) {
		patchInstallationInternal(installationId, PartialUpdateOperation.toJson(operations));
	}

	@Override
	public void PatchInstallation(String installationId, List<PartialUpdateOperation> operations) {
		patchInstallationInternal(installationId, PartialUpdateOperation.toJson(operations));
	}

	@Override
	public void DeleteInstallation(String installationId) {
		HttpDelete delete = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			delete = new HttpDelete(uri);
			delete.setHeader("Authorization", generateSasToken(uri));
						
			HttpResponse response = httpClient.execute(delete);
			if (response.getStatusLine().getStatusCode() != 204) {
				throw new RuntimeException(getErrorString(response));
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (delete != null)
				delete.releaseConnection();
		}
	}

	@Override
	public Installation GetInstallation(String installationId) {
		HttpGet get = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));
						
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException(getErrorString(response));
			}
			
			return Installation.fromJson(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (get != null)
				get.releaseConnection();
		}
	}
	
	public void patchInstallationInternal(String installationId, String operationsJson) {
		HttpPatch patch = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/installations/" + installationId + APIVERSION);
			patch = new HttpPatch(uri);
			patch.setHeader("Authorization", generateSasToken(uri));
						
			StringEntity entity = new StringEntity(operationsJson, ContentType.APPLICATION_JSON);
			entity.setContentEncoding("utf-8");
			patch.setEntity(entity);
			HttpResponse response = httpClient.execute(patch);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException(getErrorString(response));
			}			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (patch != null)
				patch.releaseConnection();
		}
	}

	@Override
	public NotificationHubJob submitNotificationHubJob(NotificationHubJob job) {
		HttpPost post = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + APIVERSION);
			post = new HttpPost(uri);
			post.setHeader("Authorization", generateSasToken(uri));

			StringEntity entity = new StringEntity(job.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);

			if (response.getStatusLine().getStatusCode() != 201) {
				throw new RuntimeException(getErrorString(response));
			}
			
			return NotificationHubJob.parseOne(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}

	@Override
	public NotificationHubJob getNotificationHubJob(String jobId) {
		HttpGet get = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs/"	+ jobId + APIVERSION);
			get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));

			HttpResponse response = httpClient.execute(get);

			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException(getErrorString(response));

			return NotificationHubJob.parseOne(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (get != null)
				get.releaseConnection();
		}
	}

	@Override
	public List<NotificationHubJob> getAllNotificationHubJobs() {
		HttpGet get = null;
		try {
			URI uri = new URI(endpoint + hubPath + "/jobs" + APIVERSION);
			get = new HttpGet(uri);
			get.setHeader("Authorization", generateSasToken(uri));

			HttpResponse response = httpClient.execute(get);

			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException(getErrorString(response));

			return NotificationHubJob.parseCollection(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (get != null)
				get.releaseConnection();
		}
	}	
}
