package com.windowsazure.messaging;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class NamespaceManager {
	private static final String IFMATCH_HEADER_NAME = "If-Match";
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	private static final String HUBS_COLLECTION_PATH = "$Resources/NotificationHubs/";
	private static final String APIVERSION = "?api-version=2014-09";
	private static final String SKIP_TOP_PARAM = "&$skip=0&$top=2147483647";
	private String endpoint;
	private String SasKeyName;
	private String SasKeyValue;

	public NamespaceManager(String connectionString) {
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
	
	public void getNotificationHubAsync(String hubPath, final FutureCallback<NotificationHubDescription> callback){
		try {
			URI uri = new URI(endpoint + hubPath + APIVERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	
	public NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException{
		SyncCallback<NotificationHubDescription> callback = new SyncCallback<NotificationHubDescription>();
		getNotificationHubAsync(hubPath, callback);
		return callback.getResult();
	}
	
	public void getNotificationHubsAsync(final FutureCallback<List<NotificationHubDescription>> callback){
		try {
			URI uri = new URI(endpoint + HUBS_COLLECTION_PATH + APIVERSION + SKIP_TOP_PARAM);
			final HttpGet get = new HttpGet(uri);
			get.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200) {
		        			callback.failed(new NotificationHubsException(getErrorString(response), httpStatusCode));
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
	
	public List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException{
		SyncCallback<List<NotificationHubDescription>> callback = new SyncCallback<List<NotificationHubDescription>>();
		getNotificationHubsAsync(callback);
		return callback.getResult();
	}
	
	public void createNotificationHubAsync(NotificationHubDescription hubDescription, final FutureCallback<NotificationHubDescription> callback){
		createOrUpdateNotificationHubAsync(hubDescription, false, callback);
	}
	
	public NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException{
		SyncCallback<NotificationHubDescription> callback = new SyncCallback<NotificationHubDescription>();
		createNotificationHubAsync(hubDescription, callback);
		return callback.getResult();
	}
	
	public void updateNotificationHubAsync(NotificationHubDescription hubDescription, FutureCallback<NotificationHubDescription> callback){
		createOrUpdateNotificationHubAsync(hubDescription, true, callback);
	}
	
	public NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException{
		SyncCallback<NotificationHubDescription> callback = new SyncCallback<NotificationHubDescription>();
		updateNotificationHubAsync(hubDescription, callback);
		return callback.getResult();
	}
	
	private void createOrUpdateNotificationHubAsync(NotificationHubDescription hubDescription, final boolean isUpdate, final FutureCallback<NotificationHubDescription> callback){
		try {
			URI uri = new URI(endpoint + hubDescription.getPath() + APIVERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			if(isUpdate){
				put.setHeader(IFMATCH_HEADER_NAME, "*");
			}
			
			StringEntity entity = new StringEntity(hubDescription.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);
			
			HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != (isUpdate ? 200 : 201)) {
		        			callback.failed(new NotificationHubsException(getErrorString(response) ,httpStatusCode));
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
	
	public void deleteNotificationHubAsync(String hubPath, final FutureCallback<Object> callback){
		try {
			URI uri = new URI(endpoint + hubPath + APIVERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			
			HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
		        public void completed(final HttpResponse response) {
		        	try{
		        		int httpStatusCode = response.getStatusLine().getStatusCode();
		        		if (httpStatusCode != 200 && httpStatusCode != 404) {
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
	
	public void deleteNotificationHub(String hubPath) throws NotificationHubsException{
		SyncCallback<Object> callback = new SyncCallback<Object>();
		deleteNotificationHubAsync(hubPath, callback);
		callback.getResult();
	}	 
		
	private String generateSasToken(URI uri) {
		String targetUri;
		try {
			targetUri = URLEncoder
					.encode(uri.toString().toLowerCase(), "UTF-8")
					.toLowerCase();

			long expiresOnDate = System.currentTimeMillis();
			long dif=SdkGlobalSettings.getAuthorizationTokenExpirationInMinutes() * 60 * 1000;
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

	private String getErrorString(HttpResponse response)
			throws IllegalStateException, IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		String body = writer.toString();
		return "Error: " + response.getStatusLine() + " - " + body;
	}
}
