package com.windowsazure.messaging;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;


public class HttpClientManager {

	/** Name of system property to activate httpAsyncClient create with system properties */
	public static final String SYSTEM_HTTP_CLIENT_CREATE = "http_client_type";

	private static CloseableHttpAsyncClient httpAsyncClient;

	public static CloseableHttpAsyncClient getHttpAsyncClient() {
		if(httpAsyncClient == null) {
			synchronized(HttpClientManager.class) {
				if(httpAsyncClient == null) {
					/* Create http client by type */
					HttpClientType clientType = HttpClientType.valueOf(System.getProperty(SYSTEM_HTTP_CLIENT_CREATE));
					CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
					if (HttpClientType.MINIMAL.equals(clientType)){
						client = HttpAsyncClients.createMinimal();
					} else if (HttpClientType.SYSTEM.equals(clientType)){
						client = HttpAsyncClients.createSystem();
					}else {
						client = HttpAsyncClients.createDefault();
					}
					client.start();
					httpAsyncClient = client;	    	   
				}
			}
		}
		  
		return httpAsyncClient;
	}

	public static void setHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient) {
		synchronized (HttpClientManager.class) {
			if (HttpClientManager.httpAsyncClient == null) {
				HttpClientManager.httpAsyncClient = httpAsyncClient;
			} else {
				throw new RuntimeException("HttpAsyncClient was already set before or default one is being used.");
			}
		}
	}

	/**
	 * Enum represent httpClients
	 * 
	 * @author manuel
	 *
	 */
	public static enum HttpClientType {
		DEFAULT, MINIMAL, SYSTEM;
	}

}
