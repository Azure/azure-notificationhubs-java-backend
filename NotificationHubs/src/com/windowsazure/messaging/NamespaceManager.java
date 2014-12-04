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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class NamespaceManager {
	private static final String IFMATCH_HEADER_NAME = "If-Match";
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	private static final String HUBS_COLLECTION_PATH = "$Resources/NotificationHubs/";
	private static final String APIVERSION = "?api-version=2014-09";
	private static final String SKIP_TOP_PARAM = "&$skip=0&$top=2147483647";
	private String endpoint;
	private String SasKeyName;
	private String SasKeyValue;
	private HttpClient httpClient;

	public NamespaceManager(String connectionString) {
		this.httpClient = HttpClients.createDefault();
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
	
	public NotificationHubDescription getNotificationHub(String hubPath){
		HttpGet get = null;
		try {
			URI uri = new URI(endpoint + hubPath + APIVERSION);
			get = new HttpGet(uri);
			get.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			HttpResponse response = httpClient.execute(get);

			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException(getErrorString(response));

			return NotificationHubDescription.parseOne(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (get != null)
				get.releaseConnection();
		}
	}
	
	public List<NotificationHubDescription> getNotificationHubs(){
		HttpGet get = null;
		try {
			URI uri = new URI(endpoint + HUBS_COLLECTION_PATH + APIVERSION + SKIP_TOP_PARAM);
			get = new HttpGet(uri);
			get.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			HttpResponse response = httpClient.execute(get);

			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException(getErrorString(response));

			return NotificationHubDescription.parseCollection(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (get != null)
				get.releaseConnection();
		}
	}
	
	public NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription){
		return createOrUpdateNotificationHub(hubDescription, false);
	}
	
	public NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription){
		return createOrUpdateNotificationHub(hubDescription, true);
	}
	
	public void DeleteNotificationHub(String hubPath){
		HttpDelete delete = null;
		try {
			URI uri = new URI(endpoint + hubPath + APIVERSION);
			delete = new HttpDelete(uri);
			delete.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			HttpResponse response = httpClient.execute(delete);

			if (response.getStatusLine().getStatusCode() != 200 && 
					response.getStatusLine().getStatusCode() != 404)
				throw new RuntimeException(getErrorString(response));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (delete != null)
				delete.releaseConnection();
		}
	}	 
	
	private NotificationHubDescription createOrUpdateNotificationHub(NotificationHubDescription hubDescription, boolean isUpdate){
		HttpPut put = null;
		try {
			URI uri = new URI(endpoint + hubDescription.getPath() + APIVERSION);
			put = new HttpPut(uri);
			put.setHeader(AUTHORIZATION_HEADER_NAME, generateSasToken(uri));
			if(isUpdate){
				put.setHeader(IFMATCH_HEADER_NAME, "*");
			}
			
			StringEntity entity = new StringEntity(hubDescription.getXml(), ContentType.APPLICATION_ATOM_XML);
			entity.setContentEncoding("utf-8");
			put.setEntity(entity);
			HttpResponse response = httpClient.execute(put);

			if (response.getStatusLine().getStatusCode() != (isUpdate ? 200 : 201)) {
				throw new RuntimeException(getErrorString(response));
			}
			
			return NotificationHubDescription.parseOne(response.getEntity().getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (put != null)
				put.releaseConnection();
		}
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

	private String getErrorString(HttpResponse response)
			throws IllegalStateException, IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		String body = writer.toString();
		return "Error: " + response.getStatusLine() + " - " + body;
	}
}
