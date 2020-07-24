//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * Policy to govern retrying of operations.
 */
public class RetryPolicy {
    static final long NANOS_PER_SECOND = 1000_000_000L;

    private static final double JITTER_FACTOR = 0.08;

    private final RetryOptions retryOptions;
    private final Duration baseJitter;

    private final double retryFactor;
    
    /**
     * Creates an instance with the given retry options. If {@link RetryOptions#getMaxDelay()}, {@link
     * RetryOptions#getDelay()}, or {@link RetryOptions#getMaxRetries()} is equal to {@link Duration#ZERO} or
     * zero, requests failing with a retriable exception will not be retried.
     *
     * @param retryOptions The options to set on this retry policy.
     * @throws NullPointerException if {@code retryOptions} is {@code null}.
     */
    public RetryPolicy(RetryOptions retryOptions) {
        Objects.requireNonNull(retryOptions, "'retryOptions' cannot be null.");

        this.retryOptions = retryOptions;

        // 1 second = 1.0 * 10^9 nanoseconds.
        final double jitterInNanos = retryOptions.getDelay().getSeconds() * JITTER_FACTOR * NANOS_PER_SECOND;
        baseJitter = Duration.ofNanos((long) jitterInNanos);
        
        this.retryFactor = computeRetryFactor();
    }

    /**
     * Gets the set of options used to configure this retry policy.
     *
     * @return The set of options used to configure this retry policy.
     */
    protected RetryOptions getRetryOptions() {
        return retryOptions;
    }

    /**
     * Gets the maximum number of retry attempts.
     *
     * @return The maximum number of retry attempts.
     */
    public int getMaxRetries() {
        return retryOptions.getMaxRetries();
    }

    /**
     * Calculates the amount of time to delay before the next retry attempt.
     *
     * @param lastException The last exception that was observed for the operation to be retried.
     * @param retryCount The number of attempts that have been made, including the initial attempt before any retries.
     * @return The amount of time to delay before retrying the associated operation; if {@code null}, then the operation
     * is no longer eligible to be retried.
     */
    public Duration calculateRetryDelay(Throwable lastException, int retryCount) {
        if (retryOptions.getMaxRetries() <= 0
        	|| retryOptions.getDelay() == Duration.ZERO
            || retryOptions.getMaxDelay() == Duration.ZERO
            || retryCount > retryOptions.getMaxRetries()) {
            return null;
        }

        Duration retryAfterDelay = null;
        if (lastException instanceof NotificationHubsException && ((NotificationHubsException) lastException).retryAfter.isPresent()) {
            retryAfterDelay = Duration.ofSeconds(((NotificationHubsException) lastException).retryAfter.get());
        } 

        Duration delay = null;
        if (lastException instanceof QuotaExceededException) {
        	delay = calculateFixedRetryDelay(
        			retryCount, 
        			retryAfterDelay == null ? QuotaExceededException.DefaultDelay : retryAfterDelay, 
        			baseJitter, 
        			ThreadLocalRandom.current());
        } else if (lastException instanceof NotificationHubsException) {
        	switch(retryOptions.getMode()) {
        	case FIXED:
        		delay = calculateFixedRetryDelay(retryCount, retryOptions.getDelay(), baseJitter, ThreadLocalRandom.current());
        	case EXPONENTIAL:
        		delay = calculateExponentialRetryDelay(retryCount, retryOptions.getDelay(), baseJitter, ThreadLocalRandom.current());
        	}
        }
        
        if (delay == null) {
        	return null;
        }

        // If delay is smaller or equal to the maximum delay, return the maximum delay.
        return delay.compareTo(retryOptions.getMaxDelay()) <= 0
            ? delay
            : retryOptions.getMaxDelay();
    }

    public int hashCode() {
        return Objects.hash(retryOptions);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RetryPolicy)) {
            return false;
        }

        final RetryPolicy other = (RetryPolicy) obj;
        return retryOptions.equals(other.retryOptions);
    }   

    /**
     * Calculates the delay for a fixed backoff.
     *
     * @param retryCount The number of attempts that have been made, including the initial attempt before any
     *     retries.
     * @param baseDelay The delay to use for the fixed backoff.
     * @param baseJitter The duration to use for the basis of the random jitter value.
     * @param random The random number generator used to calculate the jitter.
     * @return The duration to delay before retrying a request.
     */
    protected Duration calculateFixedRetryDelay(int retryCount, Duration baseDelay, Duration baseJitter,
                                           ThreadLocalRandom random) {
        final Double jitterNanos = random.nextDouble() * baseJitter.getSeconds() * RetryPolicy.NANOS_PER_SECOND;
        final Duration jitter = Duration.ofNanos(jitterNanos.longValue());

        return baseDelay.plus(jitter);
    }
    
    /**
     * Calculates the retry delay using exponential backoff.
     *
     * @param retryCount The number of attempts that have been made, including the initial attempt before any
     *         retries.
     * @param baseDelay The delay to use for the basis of the exponential backoff.
     * @param baseJitter The duration to use for the basis of the random jitter value.
     * @param random The random number generator used to calculate the jitter.
     * @return The duration to delay before retrying a request.
     */
    private Duration calculateExponentialRetryDelay(int retryCount, Duration baseDelay, Duration baseJitter,
                                           ThreadLocalRandom random) {
        final double jitterSeconds = random.nextDouble() * baseJitter.getSeconds();
        final double nextRetrySeconds = Math.pow(retryFactor, (double) retryCount);
        final Double nextRetryNanos = (jitterSeconds + nextRetrySeconds) * NANOS_PER_SECOND;

        return baseDelay.plus(Duration.ofNanos(nextRetryNanos.longValue()));
    }
    
    private double computeRetryFactor() {
        final RetryOptions options = getRetryOptions();
        final Duration maxBackoff = options.getMaxDelay();
        final Duration minBackoff = options.getDelay();
        final int maximumRetries = options.getMaxRetries();
        final long deltaBackoff = maxBackoff.minus(minBackoff).getSeconds();

        if (deltaBackoff <= 0 || maximumRetries <= 0) {
            return 0;
        }

        return Math.log(deltaBackoff) / Math.log(maximumRetries);
    }
}
