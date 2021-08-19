package com.windowsazure.messaging;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * This class represents a basic retry strategy for Azure Notification Hubs.
 */
public class BasicRetryStrategy implements HttpRequestRetryStrategy {
    public static final BasicRetryStrategy INSTANCE = new BasicRetryStrategy();

    private final long maxRetries;
    private final TimeValue delay;
    private final Set<Class<? extends IOException>> nonRetriableIOExceptionClasses;
    private final Set<Integer> retriableCodes;

    private static final ThreadLocal<Random> randomNumberGenerator;

    static {
        randomNumberGenerator = ThreadLocal.withInitial(() -> new Random(System.currentTimeMillis()));
    }

    /**
     * Creates a new instance of the BasicRetryStrategy with default parameters.
     */
    public BasicRetryStrategy() {
        this(
            3L,
            TimeValue.ofSeconds(1),
            Arrays.asList(
                InterruptedIOException.class,
                UnknownHostException.class,
                ConnectException.class,
                ConnectionClosedException.class,
                SSLException.class),
            Arrays.asList(
                HttpStatus.SC_GATEWAY_TIMEOUT,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_REQUEST_TIMEOUT,
                HttpStatus.SC_FORBIDDEN,
                HttpStatus.SC_TOO_MANY_REQUESTS,
                HttpStatus.SC_SERVICE_UNAVAILABLE)
        );
    }

    /**
     * Creates a new instance of the BasicRetryStrategy class.
     * @param maxRetries The number of maximum retries.
     * @param delay The time delay between requests.
     * @param nonRetriableIOExceptionClasses The non-retriable IOException class instances.
     * @param retriableCodes The retriable HTTP status codes.
     */
    public BasicRetryStrategy(
        long maxRetries,
        TimeValue delay,
        Collection<Class<? extends IOException>> nonRetriableIOExceptionClasses,
        Collection<Integer> retriableCodes) {
        this.maxRetries = maxRetries;
        this.delay = delay;
        this.nonRetriableIOExceptionClasses = new HashSet<>(nonRetriableIOExceptionClasses);
        this.retriableCodes = new HashSet<>(retriableCodes);
    }

    @Override
    public boolean retryRequest(final HttpRequest httpRequest, final IOException e, final int i, final HttpContext httpContext) {
        if (i > this.maxRetries) {
            return false;
        }
        if (this.nonRetriableIOExceptionClasses.contains(e.getClass())) {
            return false;
        } else {
            for (final Class<? extends IOException> rejectException : this.nonRetriableIOExceptionClasses) {
                if (rejectException.isInstance(e)) {
                    return false;
                }
            }
        }
        if (httpRequest instanceof CancellableDependency && ((CancellableDependency) httpRequest).isCancelled()) {
            return false;
        }

        return handleAsIdempotent(httpRequest);
    }

    @Override
    public boolean retryRequest(final HttpResponse httpResponse, final int i, final HttpContext httpContext) {
        return i <= this.maxRetries && retriableCodes.contains(httpResponse.getCode());
    }

    @Override
    public TimeValue getRetryInterval(final HttpResponse httpResponse, final int i, final HttpContext httpContext) {
        final Header header = httpResponse.getFirstHeader(HttpHeaders.RETRY_AFTER);
        TimeValue retryAfter = null;
        if (header != null) {
            final String value = header.getValue();
            try {
                retryAfter = TimeValue.ofSeconds(Long.parseLong(value));
            } catch (final NumberFormatException ignore) {
                final Date retryAfterDate = DateUtils.parseDate(value);
                if (retryAfterDate != null) {
                    retryAfter =
                        TimeValue.ofMilliseconds(retryAfterDate.getTime() - System.currentTimeMillis());
                }
            }

            if (TimeValue.isPositive(retryAfter)) {
                return retryAfter;
            }
        } else {
            double jitterFactor = 0.08;
            double baseJitterSeconds = delay.toSeconds() * jitterFactor;
            double retryDelayValue = delay.toSeconds() + randomNumberGenerator.get().nextDouble() * baseJitterSeconds;
            return TimeValue.ofSeconds((long)retryDelayValue);
        }

        return TimeValue.ofSeconds(1);
    }

    protected boolean handleAsIdempotent(final HttpRequest request) {
        return Method.isIdempotent(request.getMethod());
    }
}
