//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NotificationHubParseTest {

	@ParameterizedTest
	@ValueSource(strings = {"NotificationHubDescriptionWithAllCredentials", "NotificationHubDescriptionWithAllCredentialsLowercaseNames"})
    public void testParseNotificationHubWithAllCredentials(String inputFileName) throws IOException, SAXException {
        InputStream inputXml = this.getClass()
            .getResourceAsStream(inputFileName);
        NotificationHubDescription hub = NotificationHubDescription.parseOne(inputXml);
        assertNotNull(hub);
        assertEquals("test-hub", hub.getPath());

        String expectedResultXml = IOUtils.toString(this.getClass()
            .getResourceAsStream("NotificationHubDescriptionWithAllCredentialsNoSpaces"), StandardCharsets.UTF_8);
        String actualResultXml = hub.getXml();
        assertEquals(expectedResultXml, actualResultXml);
    }

    @Test
    public void testParseNotificationHubsFeed() throws IOException, SAXException {
        InputStream inputXml = this.getClass()
            .getResourceAsStream("NotificationHubDescriptionRealLifeFeed");
        List<NotificationHubDescription> hubs = NotificationHubDescription.parseCollection(inputXml);
        assertNotNull(hubs);
        assertEquals(3, hubs.size());
    }
}
