package com.windowsazure.messaging;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

public class InstallationParseTest {
	
	@Test
	public void InstallationMinimal() throws IOException, SAXException, URISyntaxException {
		InputStream inputJson = this.getClass().getResourceAsStream("InstallationMinimal");		
		Installation installation = Installation.fromJson(inputJson);
		assertNotNull(installation);
		assertEquals("123", installation.getInstallationId());
		assertEquals(NotificationPlatform.Gcm, installation.getPlatform());
		assertEquals("qwe", installation.getPushChannel());
				
		String expectedResultJson = IOUtils.toString(this.getClass().getResourceAsStream("InstallationMinimalNoSpaces"));	
		String  actualResultJson = installation.toJson();
		assertEquals(expectedResultJson, actualResultJson);	
	}
	
	@Test
	public void InstallationWnsFull() throws IOException, SAXException, URISyntaxException {
		InputStream inputJson = this.getClass().getResourceAsStream("InstallationWnsFull");		
		Installation installation = Installation.fromJson(inputJson);
		assertNotNull(installation);
		assertEquals("123", installation.getInstallationId());
		assertEquals(NotificationPlatform.Wns, installation.getPlatform());
		assertEquals("wns-push-channel1", installation.getPushChannel());
		assertNotNull(installation.getTemplates());
		assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>", installation.getTemplates().get("template1").getBody());
		Date expiration = installation.getExpirationTime();
		TimeZone.setDefault(TimeZone.getTimeZone("PST"));
		assertTrue(expiration.toString().equalsIgnoreCase("Wed Nov 26 15:34:01 PST 2014"));
				
		String expectedResultJson = IOUtils.toString(this.getClass().getResourceAsStream("InstallationWnsFullNoSpaces"));	
		String  actualResultJson = installation.toJson();
		assertEquals(expectedResultJson, actualResultJson);	
	}
	
	@Test
	public void PartialUpdates() throws IOException, SAXException, URISyntaxException { 
		PartialUpdateOperation add = new PartialUpdateOperation(UpdateOperationType.Add, "/templates/template1", new InstallationTemplate("{\"data\":{\"key1\":\"value\"}}").toJson());
		PartialUpdateOperation remove = new PartialUpdateOperation(UpdateOperationType.Remove,"/remove/path");
		PartialUpdateOperation replace = new PartialUpdateOperation(UpdateOperationType.Replace,"/replace/path","replace-value");
			
		String expectedResultJson = IOUtils.toString(this.getClass().getResourceAsStream("PartialUpdatesNoSpaces"));	
		String  actualResultJson = PartialUpdateOperation.toJson(add,remove,replace);
		assertEquals(expectedResultJson, actualResultJson);	
	}
	
}
