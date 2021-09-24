//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.concurrent.CountDownLatch;

/**
 * This class represents getting a synchronous value from an asynchronous operation.
 * @param <T> The type returned from the asynchronous operation.
 */
public class SyncCallback<T> implements FutureCallback<T> {
    private T result;
    private RuntimeException runtimeException;
    private NotificationHubsException nhException;
    private final CountDownLatch waitLatch = new CountDownLatch(1);

    /**
     * Gets a synchronous value from an asynchronous operation.
     * @return The synchronous value from an asynchronous operation.
     * @throws NotificationHubsException If there is an error with the operation.
     */
    public T getResult() throws NotificationHubsException {
        try {
            waitLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (runtimeException != null)
            throw runtimeException;

        if (nhException != null)
            throw nhException;

        return result;
    }

    @Override
    public void completed(T result) {
        this.result = result;
        this.waitLatch.countDown();
    }

    @Override
    public void failed(final Exception ex) {
        if (ex instanceof NotificationHubsException) {
            nhException = (NotificationHubsException) ex;
        } else {
            runtimeException = new RuntimeException(ex);
        }

        this.waitLatch.countDown();
    }

    @Override
    public void cancelled() {
        runtimeException = new RuntimeException("Operation was cancelled.");
        this.waitLatch.countDown();
    }
}
