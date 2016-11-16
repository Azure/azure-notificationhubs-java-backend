package com.windowsazure.messaging;

import java.util.concurrent.CountDownLatch;

import org.apache.http.concurrent.FutureCallback;

public class SyncCallback<T> implements FutureCallback<T> {
	private T result;
	private RuntimeException runtimeException;
	private NotificationHubsException nhException; 
	private CountDownLatch waitLatch = new CountDownLatch(1);
	    
    public T getResult() throws NotificationHubsException { 
    	try {
    		this.waitLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
    	
    	if(this.runtimeException != null)	
    		throw this.runtimeException;
    	
    	if(this.nhException !=null )
    		throw this.nhException;
    	
    	return result; 
    }
    
	@Override
	public void completed(T result) {
		this.result = result;
		this.waitLatch.countDown();
    }
	
	@Override
    public void failed(final Exception ex) {
		if(ex instanceof NotificationHubsException){
			this.nhException=(NotificationHubsException)ex;
		}
		else{
			this.runtimeException = new RuntimeException(ex);
		}
		
		this.waitLatch.countDown();
    }
	
    
	@Override
    public void cancelled() {
		runtimeException = new RuntimeException("Operation was cancelled.");      
		this.waitLatch.countDown();  
    }
}
