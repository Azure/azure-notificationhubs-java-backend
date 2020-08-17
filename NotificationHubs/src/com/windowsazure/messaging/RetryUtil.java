//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;

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

    private static final int[] retriableStatusCodes = new int[] {500, 503, 504, 408, 429, 403};

    /**
     * Given a set of {@link RetryOptions options}, creates the appropriate retry policy.
     *
     * @param options A set of options used to configure the retry policy.
     * @return A new retry policy configured with the given {@code options}.
     * @throws IllegalArgumentException If {@link RetryOptions#getMode()} is not a supported mode.
     */
    public static RetryPolicy getRetryPolicy(RetryOptions options) {
    	switch(options.getMode()) {
    	case FIXED:
    		return new FixedRetryPolicy(options);
		case EXPONENTIAL:
			return new ExponentialRetryPolicy(options);
		default:
			throw new IllegalArgumentException("options contained an unknown RetryMode");
    	}
    }

    /**
     * Given a {@link Flux} will apply the retry policy to it when the operation times out.
     * @param <T> The type of the Flux source.
     * @param source The publisher to apply the retry policy to.
     * @param operationTimeout The timeout of the operation
     * @param retryPolicy The retry policy to apply to the retry operation
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
     * @param <T> The type of the Flux source.
     * @param source The publisher to apply the retry policy to.
     * @param operationTimeout The timeout of the operation
     * @param retryPolicy The retry policy to apply to the retry operation     
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
                    throw Exceptions.propagate(error);
                }

                if (error instanceof TimeoutException) {
                    return retryPolicy.calculateRetryDelay(error, attempt);
                } else if (error instanceof QuotaExceededException) {
                    return retryPolicy.calculateRetryDelay(error, attempt);
                } else if (error instanceof NotificationHubsException) {
                    	int statusCode = ((NotificationHubsException) error).getHttpStatusCode();
                    	if (Arrays.stream(retriableStatusCodes).anyMatch(code -> code == statusCode)) {
                    		return retryPolicy.calculateRetryDelay(error, attempt);
                    	}
                        throw Exceptions.propagate(error);
                } else {
                    throw Exceptions.propagate(error);
                }
            })
            .flatMap(Mono::delay);
    }

    static Optional<Duration> parseRetryAfter(HttpResponse response)
    {
        Header retryAfter = response.getFirstHeader(HttpHeaders.RETRY_AFTER);
        if (retryAfter == null) {
            return Optional.empty();
        }
        String retryAfterValue = retryAfter.getValue();
        if (retryAfterValue.equals("")) {
            return Optional.empty();
        }

        try {
            long retryAfterSeconds = Long.parseLong(retryAfterValue);
            return Optional.of(Duration.ofSeconds(retryAfterSeconds));
        }
        catch (NumberFormatException e) {
            throw new UnsupportedOperationException(String.format("\"%s\" must be an integer number of seconds", retryAfterValue));
        }
    }
}
