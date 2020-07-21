//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Objects;

public class RetryOptions {
	 private int maxRetries;
	 private Duration delay;
	 private Duration maxDelay;
	 private Duration tryTimeout;
	 private RetryMode retryMode;
	 
	 /**
	 * Creates an instance with the default retry options set.
	 */
	 public RetryOptions() {
		 maxRetries = 3;
		 delay = Duration.ofMillis(800);
		 maxDelay = Duration.ofMinutes(1);
		 tryTimeout = Duration.ofMinutes(1);
		 retryMode = RetryMode.EXPONENTIAL;
	 }
	    
	    /**
	     * Creates an instance configured with {@code retryOptions}. This is not thread-safe.
	     *
	     * @param retryOptions Retry options to configure new instance with.
	     * @throws NullPointerException if {@code retryOptions} is null.
	     */
	    public RetryOptions(RetryOptions retryOptions) {
	        this.maxDelay = retryOptions.getMaxDelay();
	        this.delay = retryOptions.getDelay();
	        this.maxRetries = retryOptions.getMaxRetries();
	        this.retryMode = retryOptions.getMode();
	        this.tryTimeout = retryOptions.getTryTimeout();
	    }

	    /**
	     * Sets the approach to use for calculating retry delays.
	     *
	     * @param retryMode The retry approach to use for calculating delays.
	     * @return The updated {@link RetryOptions} object.
	     */
	    public RetryOptions setMode(RetryMode retryMode) {
	        this.retryMode = retryMode;
	        return this;
	    }

	    /**
	     * Sets the maximum number of retry attempts before considering the associated operation to have failed.
	     *
	     * @param numberOfRetries The maximum number of retry attempts.
	     * @return The updated {@link RetryOptions} object.
	     */
	    public RetryOptions setMaxRetries(int numberOfRetries) {
	        this.maxRetries = numberOfRetries;
	        return this;
	    }

	    /**
	     * Gets the delay between retry attempts for a fixed approach or the delay on which to base calculations for a
	     * backoff-approach.
	     *
	     * @param delay The delay between retry attempts.
	     * @return The updated {@link RetryOptions} object.
	     */
	    public RetryOptions setDelay(Duration delay) {
	        this.delay = delay;
	        return this;
	    }

	    /**
	     * Sets the maximum permissible delay between retry attempts.
	     *
	     * @param maximumDelay The maximum permissible delay between retry attempts.
	     * @return The updated {@link RetryOptions} object.
	     */
	    public RetryOptions setMaxDelay(Duration maximumDelay) {
	        this.maxDelay = maximumDelay;
	        return this;
	    }

	    /**
	     * Sets the maximum duration to wait for completion of a single attempt, whether the initial attempt or a retry.
	     *
	     * @param tryTimeout The maximum duration to wait for completion.
	     * @return The updated {@link RetryOptions} object.
	     */
	    public RetryOptions setTryTimeout(Duration tryTimeout) {
	        this.tryTimeout = tryTimeout;
	        return this;
	    }

	    /**
	     * Gets the approach to use for calculating retry delays.
	     *
	     * @return The approach to use for calculating retry delays.
	     */
	    public RetryMode getMode() {
	        return retryMode;
	    }

	    /**
	     * The maximum number of retry attempts before considering the associated operation to have failed.
	     *
	     * @return The maximum number of retry attempts before considering the associated operation to have failed.
	     */
	    public int getMaxRetries() {
	        return maxRetries;
	    }

	    /**
	     * Gets the delay between retry attempts for a fixed approach or the delay on which to base calculations for a
	     * backoff-approach.
	     *
	     * @return The delay between retry attempts.
	     */
	    public Duration getDelay() {
	        return delay;
	    }

	    /**
	     * Gets the maximum permissible delay between retry attempts.
	     *
	     * @return The maximum permissible delay between retry attempts.
	     */
	    public Duration getMaxDelay() {
	        return maxDelay;
	    }

	    /**
	     * Gets the maximum duration to wait for completion of a single attempt, whether the initial attempt or a retry.
	     *
	     * @return The maximum duration to wait for completion of a single attempt, whether the initial attempt or a retry.
	     */
	    public Duration getTryTimeout() {
	        return tryTimeout;
	    }

	    /**
	     * {@inheritDoc}
	     */
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	            return true;
	        }

	        if (!(obj instanceof RetryOptions)) {
	            return false;
	        }

	        final RetryOptions other = (RetryOptions) obj;

	        return this.getMaxRetries() == other.getMaxRetries()
	            && this.getMode() == other.getMode()
	            && Objects.equals(this.getMaxDelay(), other.getMaxDelay())
	            && Objects.equals(this.getDelay(), other.getDelay())
	            && Objects.equals(this.getTryTimeout(), other.getTryTimeout());
	    }

	    /**
	     * {@inheritDoc}
	     */
	    @Override
	    public int hashCode() {
	        return Objects.hash(maxRetries, retryMode, maxDelay, delay, tryTimeout);
	    }
}