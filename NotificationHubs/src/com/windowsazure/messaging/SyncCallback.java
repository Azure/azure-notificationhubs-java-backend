package com.windowsazure.messaging;

import java.util.concurrent.CountDownLatch;

import org.apache.http.concurrent.FutureCallback;

public class SyncCallback<T> implements FutureCallback<T> {
	private T result;
	private RuntimeException exception;
	private CountDownLatch waitLatch = new CountDownLatch(1);
	    
    public T getResult() { 
    	try {
			waitLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
    	
    	if(this.exception != null)	
    		throw exception;
    	
    	return result; 
    }
    
	@Override
	public void completed(T result) {
		this.result = result;
		waitLatch.countDown();
    }
	
	@Override
    public void failed(final Exception ex) {
        exception = new RuntimeException(ex);
        waitLatch.countDown();
    }
    
	@Override
    public void cancelled() {
        exception = new RuntimeException("Operation was cancelled.");      
        waitLatch.countDown();  
    }
}
