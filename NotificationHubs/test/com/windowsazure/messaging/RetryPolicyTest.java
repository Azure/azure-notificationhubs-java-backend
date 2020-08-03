package com.windowsazure.messaging;

import org.apache.http.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.BeforeClass;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import reactor.core.Exceptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.Future;

@RunWith(Theories.class)
public class RetryPolicyTest {
    static CloseableHttpAsyncClient closeableHttpAsyncClientMock;

    @BeforeClass
    public static void setUpClass() {
        closeableHttpAsyncClientMock = Mockito.mock(CloseableHttpAsyncClient.class);
        HttpClientManager.setHttpAsyncClient(closeableHttpAsyncClientMock);
    }

    @Before
    public void setUp() {
        reset(closeableHttpAsyncClientMock);
    }

    @Theory
    public void EmitRetryWhenBackendResponseHaveRetriableStatusCode(@TestedOn(ints = {408, 500, 503, 504}) final int statusCode) {

        RetryOptions retryOptions = new RetryOptions();
        retryOptions.setMaxRetries(2);

        NamespaceManager manager = new NamespaceManager("Endpoint=sb://address/;SharedAccessKeyName=keyName;SharedAccessKey=key", retryOptions);

        when(closeableHttpAsyncClientMock
                .<HttpResponse>execute(any(HttpUriRequest.class), any(FutureCallback.class)))
                .thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
                    FutureCallback<HttpResponse> futureCallback = (FutureCallback<HttpResponse>) invocationOnMock.getArguments()[1];

                    StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("https", 1, 1), statusCode, "");
                    HttpResponse httpResponse = new BasicHttpResponse(statusLine);
                    InputStream stream = new ByteArrayInputStream("error".getBytes("utf-8"));
                    httpResponse.setEntity(new InputStreamEntity(stream));

                    futureCallback.completed(httpResponse);

                    return null;
                });

        try {
            manager.getNotificationHub("hubname");
        } catch (Exception e) {
            assertEquals(Exceptions.unwrap(e).getClass(), NotificationHubsException.class);
        }

        Mockito.verify(closeableHttpAsyncClientMock, Mockito.times(3))
                .execute(any(HttpUriRequest.class), any(FutureCallback.class));
    }

    @Theory
    public void NoEmitRetryWhenBackendResponseHaveNoRetriableStatusCode(@TestedOn(ints = {400, 401, 404, 405, 409, 410, 412, 413}) final int statusCode) {
        NamespaceManager manager = new NamespaceManager("Endpoint=sb://address/;SharedAccessKeyName=keyName;SharedAccessKey=key", new RetryOptions());

        when(closeableHttpAsyncClientMock
                .<HttpResponse>execute(any(HttpUriRequest.class), any(FutureCallback.class)))
                .thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
                    FutureCallback<HttpResponse> futureCallback = (FutureCallback<HttpResponse>) invocationOnMock.getArguments()[1];

                    StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("https", 1, 1), statusCode, "");
                    HttpResponse httpResponse = new BasicHttpResponse(statusLine);
                    InputStream stream = new ByteArrayInputStream("error".getBytes("utf-8"));
                    httpResponse.setEntity(new InputStreamEntity(stream));

                    futureCallback.completed(httpResponse);

                    return null;
                });

        try {
            manager.getNotificationHub("hubname");
        } catch (Exception e) {
            assertEquals(Exceptions.unwrap(e).getClass(), NotificationHubsException.class);
        }

        Mockito.verify(closeableHttpAsyncClientMock, Mockito.times(1))
                .execute(any(HttpUriRequest.class), any(FutureCallback.class));
    }

    @Theory
    public void NoEmitRetryWhenBackendResponseHaveSuccessStatusCode(@TestedOn(ints = {200, 201, 204}) final int statusCode) {
        NamespaceManager manager = new NamespaceManager("Endpoint=sb://address/;SharedAccessKeyName=keyName;SharedAccessKey=key", new RetryOptions());
        NotificationHubDescription expectedHub = new NotificationHubDescription();

        when(closeableHttpAsyncClientMock
                .<HttpResponse>execute(any(HttpUriRequest.class), any(FutureCallback.class)))
                .thenAnswer((Answer<Future<HttpResponse>>) invocationOnMock -> {
                    FutureCallback<HttpResponse> futureCallback = (FutureCallback<HttpResponse>) invocationOnMock.getArguments()[1];

                    StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("https", 1, 1), statusCode, "");
                    HttpResponse httpResponse = new BasicHttpResponse(statusLine);
                    InputStream stream = new ByteArrayInputStream(expectedHub.getXml().getBytes("utf-8"));
                    httpResponse.setEntity(new InputStreamEntity(stream));

                    futureCallback.completed(httpResponse);

                    return null;
                });

        try {
            manager.getNotificationHub("hubname");
        } catch (Exception e) {
            assertEquals(Exceptions.unwrap(e).getClass(), NotificationHubsException.class);
        }

        Mockito.verify(closeableHttpAsyncClientMock, Mockito.times(1))
                .execute(any(HttpUriRequest.class), any(FutureCallback.class));
    }
}
