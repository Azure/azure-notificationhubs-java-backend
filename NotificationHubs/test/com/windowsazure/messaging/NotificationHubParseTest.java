package com.windowsazure.messaging;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;


public class NotificationHubParseTest {
		
	@Test
	public void testParseNotificationHubWithAllCredentials() throws IOException, SAXException, URISyntaxException {
		InputStream inputXml = this.getClass()
				.getResourceAsStream("NotificationHubDescriptionWithAllCredentials");		
		NotificationHubDescription hub = NotificationHubDescription.parseOne(inputXml);
		assertNotNull(hub);
		assertEquals("test-hub", hub.getPath());
				
		String expectedResultXml = IOUtils.toString(this.getClass()
				.getResourceAsStream("NotificationHubDescriptionWithAllCredentialsNoSpaces"));	
		String  actualResultXml = hub.getXml();
		assertEquals(expectedResultXml, actualResultXml);	
	}
	
	@Test
	public void testParseNotificationHubsFeed() throws IOException, SAXException, URISyntaxException {
		InputStream inputXml = this.getClass()
				.getResourceAsStream("NotificationHubDescriptionRealLifeFeed");		
		List<NotificationHubDescription> hubs = NotificationHubDescription.parseCollection(inputXml);
		assertNotNull(hubs);
		assertEquals(3, hubs.size());
	}
}
