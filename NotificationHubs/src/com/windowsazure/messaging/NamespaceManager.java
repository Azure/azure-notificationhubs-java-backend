//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import static com.windowsazure.messaging.RetryUtil.getRetryPolicy;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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

import reactor.core.publisher.Mono;

import static com.windowsazure.messaging.RetryUtil.withRetry;

public class NamespaceManager implements NamespaceManagerClient {
	private static final String IF_MATCH_HEADER_NAME = "If-Match";
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	private static final String HUBS_COLLECTION_PATH = "$Resources/NotificationHubs/";
	private static final String API_VERSION = "?api-version=2020-06";
	private static final String SKIP_TOP_PARAM = "&$skip=0&$top=2147483647";
	private String endpoint;
	private String SasKeyName;
	private String SasKeyValue;
    private final RetryOptions retryOptions;
    private final RetryPolicy retryPolicy;

	public NamespaceManager(String connectionString, RetryOptions retryOptions) {
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
    public Mono<NotificationHubDescription> getNotificationHubAsync(String hubPath){
		try {
			URI uri = new URI(endpoint + hubPath + API_VERSION);
			final HttpGet get = new HttpGet(uri);
			get.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			return withRetry(Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
			    			}

			        		sink.success(NotificationHubDescription.parseOne(response.getEntity().getContent()));
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
			}), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
    public NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException{
		return getNotificationHubAsync(hubPath).block();
	}

	@Override
    public Mono<List<NotificationHubDescription>> getNotificationHubsAsync(){
		try {
			URI uri = new URI(endpoint + HUBS_COLLECTION_PATH + API_VERSION + SKIP_TOP_PARAM);
			final HttpGet get = new HttpGet(uri);
			get.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			return withRetry(Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(get, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
			    			}

			        		sink.success(NotificationHubDescription.parseCollection(response.getEntity().getContent()));
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
			}), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			 return Mono.error(new RuntimeException(e));
		}
	}

	@Override
    public List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException{
		return getNotificationHubsAsync().block();
	}

	@Override
    public Mono<NotificationHubDescription> createNotificationHubAsync(NotificationHubDescription hubDescription){
		return createOrUpdateNotificationHubAsync(hubDescription, false);
	}

	@Override
    public NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException{
		return createNotificationHubAsync(hubDescription).block();
	}

	@Override
    public Mono<NotificationHubDescription> updateNotificationHubAsync(NotificationHubDescription hubDescription) {
		return createOrUpdateNotificationHubAsync(hubDescription, true);
	}

	@Override
    public NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException{
		return updateNotificationHubAsync(hubDescription).block();
	}

	private Mono<NotificationHubDescription> createOrUpdateNotificationHubAsync(NotificationHubDescription hubDescription, final boolean isUpdate){
		try {
			URI uri = new URI(endpoint + hubDescription.getPath() + API_VERSION);
			final HttpPut put = new HttpPut(uri);
			put.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			if(isUpdate){
				put.setHeader(IF_MATCH_HEADER_NAME, "*");
			}

			StringEntity entity = new StringEntity(hubDescription.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);

			return withRetry(Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(put, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != (isUpdate ? 200 : 201)) {
			        			throw new NotificationHubsException(getErrorString(response), httpStatusCode, RetryUtil.parseRetryAfter(response));
			    			}

			        		sink.success(NotificationHubDescription.parseOne(response.getEntity().getContent()));
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
			}), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
    public Mono<Void> deleteNotificationHubAsync(String hubPath){
		try {
			URI uri = new URI(endpoint + hubPath + API_VERSION);
			final HttpDelete delete = new HttpDelete(uri);
			delete.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			return withRetry(Mono.create(sink -> {
				HttpClientManager.getHttpAsyncClient().execute(delete, new FutureCallback<HttpResponse>() {
			        public void completed(final HttpResponse response) {
			        	try{
			        		int httpStatusCode = response.getStatusLine().getStatusCode();
			        		if (httpStatusCode != 200 && httpStatusCode != 404) {
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
				});
			}), retryOptions.getTryTimeout(), retryPolicy);
		} catch (Exception e) {
			return Mono.error(new RuntimeException(e));
		}
	}

	@Override
    public void deleteNotificationHub(String hubPath) throws NotificationHubsException{
		deleteNotificationHubAsync(hubPath).block();
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

	private String getErrorString(HttpResponse response)
			throws IllegalStateException, IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		String body = writer.toString();
		return "Error: " + response.getStatusLine() + " - " + body;
	}
}
