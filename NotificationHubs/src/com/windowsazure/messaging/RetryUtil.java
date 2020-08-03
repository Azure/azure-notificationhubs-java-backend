//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Helper class to help with retry policies.
 */
public class RetryUtil {

    // So this class can't be instantiated.
    private RetryUtil() {
    }

    private static int[] retriableStatusCodes = new int[] {500, 503, 504, 408};
    
    /**
     * Given a set of {@link RetryOptions options}, creates the appropriate retry policy.
     *
     * @param options A set of options used to configure the retry policy.
     * @return A new retry policy configured with the given {@code options}.
     * @throws IllegalArgumentException If {@link RetryOptions#getMode()} is not a supported mode.
     */
    public static RetryPolicy getRetryPolicy(RetryOptions options) {
    	return new RetryPolicy(options);
    }

    /**
     * Given a {@link Flux} will apply the retry policy to it when the operation times out.
     *
     * @param source The publisher to apply the retry policy to.
     * @return A publisher that returns the results of the {@link Flux} if any of the retry attempts are successful.
     *         Otherwise, propagates a {@link TimeoutException}.
     */
    public static <T> Flux<T> withRetry(Flux<T> source, Duration operationTimeout, RetryPolicy retryPolicy) {
        return Flux.defer(() -> source.timeout(operationTimeout))
            .retryWhen(errors -> retry(errors, retryPolicy));
    }
    
    /**
     * Given a {@link Mono} will apply the retry policy to it when the operation times out.
     *
     * @param source The publisher to apply the retry policy to.
     * @return A publisher that returns the results of the {@link Flux} if any of the retry attempts are successful.
     *         Otherwise, propagates a {@link TimeoutException}.
     */
    public static <T> Mono<T> withRetry(Mono<T> source, Duration operationTimeout, RetryPolicy retryPolicy) {
        return Mono.defer(() -> source.timeout(operationTimeout)).retryWhen(errors -> retry(errors, retryPolicy));
    }

    private static Flux<Long> retry(Flux<Throwable> source, RetryPolicy retryPolicy) {
        return source.zipWith(Flux.range(1, retryPolicy.getMaxRetries() + 1),
            (error, attempt) -> {
                if (attempt > retryPolicy.getMaxRetries()) {
                	System.out.println("Retry attempts are exhausted. Current: " + attempt + ". Max: " + retryPolicy.getMaxRetries());
                    throw Exceptions.propagate(error);
                }

                if (error instanceof TimeoutException) {
                	System.out.println("TimeoutException error occurred. Retrying operation. Attempt: " + attempt); 
                    return retryPolicy.calculateRetryDelay(error, attempt);
                } else if (error instanceof QuotaExceededException) {
                	System.out.println("Retryable error occurred. Retrying operation. Attempt: " + attempt + ". Error: " + error);
                    return retryPolicy.calculateRetryDelay(error, attempt);
                } else if (error instanceof NotificationHubsException) {                    	
                    	int statusCode = ((NotificationHubsException) error).getHttpStatusCode();
                    	if (statusCode == 408 || statusCode == 500 || statusCode == 503 || statusCode == 504) {
                    		System.out.println("Retryable error occurred. Retrying operation. Attempt: " + attempt + ". Error: " + error);
                    		return retryPolicy.calculateRetryDelay(error, attempt);
                    	}
                    	
                    	System.out.println("Error is not a TimeoutException nor is it a retryable exception." + error);
                        throw Exceptions.propagate(error);
                } else {
                	System.out.println("Error is not a TimeoutException nor is it a retryable exception." + error);
                    throw Exceptions.propagate(error);
                }
            })
            .flatMap(Mono::delay);
    }

    public static Optional<Integer> parseRetryAfter(HttpResponse response)
    {
        Header retryAfter = response.getFirstHeader(HttpHeaders.RETRY_AFTER);
        if (retryAfter == null) {
            return Optional.empty();
        }
        String retryAfterValue = retryAfter.getValue();
        if (retryAfterValue == "") {
            return Optional.empty();
        }
        Integer retryAfterSeconds;
        try {
            retryAfterSeconds = Integer.parseInt(retryAfterValue);
        }
        catch (NumberFormatException e) {
            System.out.println("Failed to parse Retry-After header: '" + retryAfterValue + "'");
            return Optional.empty();
        }
        return Optional.of(retryAfterSeconds);
    }
}