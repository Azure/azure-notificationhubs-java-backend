package com.windowsazure.messaging;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

public class HttpClientManager {
	private static CloseableHttpAsyncClient httpAsyncClient;
	
	public static CloseableHttpAsyncClient getHttpAsyncClient() {
		if(httpAsyncClient == null) {
			synchronized(HttpClientManager.class) {
				if(httpAsyncClient == null) {
					CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
					client.start();
					httpAsyncClient = client;	    	   
				}
			}
		}
		  
		return httpAsyncClient;
	}
		
	public static void setHttpAsyncClient(CloseableHttpAsyncClient httpAsyncClient) {
		synchronized(HttpClientManager.class) {
			if(HttpClientManager.httpAsyncClient == null) {
				HttpClientManager.httpAsyncClient = httpAsyncClient;
			}
			else{
				throw new RuntimeException("HttpAsyncClient was already set before or default one is being used.");
			}
		}
	}
}
