package com.windowsazure.messaging;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NotificationHubSendTest {
    private static final String CONNECTION_STRING = "Endpoint=sb://test-namespace.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=JHadkDHkdhi74jaHdakhy/rZ6KEdfhasYdahO8JOx/1sZXTUlc=";
    private static final String HUB_NAME = "test-hub";

    private static final String GCMBODYTEMPLATE = "{\"aps\": {\"alert\": \"$(message)\"}}";

    private NotificationHub hub;

    @Before
    public void setup() {
        hub = spy(new NotificationHub(CONNECTION_STRING, HUB_NAME));
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            Consumer<SimpleHttpResponse> consumer = (Consumer<SimpleHttpResponse>) args[3];
            consumer.accept(mock(SimpleHttpResponse.class));
            return invocationOnMock.getMock();
        }).when(hub).executeRequest(any(), any(), any(), any());
    }

    @Test
    public void testDirectBatchSend() throws NotificationHubsException, URISyntaxException {
        Notification n = Notification.createFcmNotification(GCMBODYTEMPLATE);
        NotificationOutcome o = hub.sendDirectNotification(n, Arrays.asList("Foo", "Bar"));

        ArgumentCaptor<SimpleHttpRequest> requestCaptor = ArgumentCaptor.forClass(SimpleHttpRequest.class);
        verify(hub).executeRequest(requestCaptor.capture(), any(), any(), any());

        SimpleHttpRequest request = requestCaptor.getValue();
        String uriPath = request.getUri().getPath();
        String method = request.getMethod();
        String boundary = request.getContentType().getParameter("boundary");

        assertEquals("/" + HUB_NAME +"/messages/$batch", uriPath);
        assertEquals("POST", method);
        assertEquals("nh-batch-multipart-boundary", boundary);
    }
}
