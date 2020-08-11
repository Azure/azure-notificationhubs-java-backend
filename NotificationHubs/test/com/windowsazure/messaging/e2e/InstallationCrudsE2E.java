//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging.e2e;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.*;

import com.windowsazure.messaging.*;

public class InstallationCrudsE2E {
	private INotificationHub hub;
	private NamespaceManager namespaceManager;
	private String hubPath;

	@Before
	public void setUp() throws Exception {
		Properties p = new Properties();
		p.load(this.getClass().getResourceAsStream("e2eSetup.properties"));		
		String connectionString = p.getProperty("connectionstring");
		assertTrue(connectionString!=null && !connectionString.isEmpty());
		
		hubPath = "JavaSDK_" + UUID.randomUUID().toString();			
		namespaceManager = new NamespaceManager(connectionString, new RetryOptions());
	 	NotificationHubDescription hubDescription = new NotificationHubDescription(hubPath);
	 	namespaceManager.createNotificationHub(hubDescription);		
		Thread.sleep(1000);
		
		hub = new NotificationHub(connectionString, hubPath, new RetryOptions());
	}
	
	@After
	public void cleanUp() throws Exception {
		assertNotNull(hubPath);
		namespaceManager.deleteNotificationHub(hubPath);
	}
	
	@Test
	public void BasicCrudScenarioTest() throws Exception{
		Installation installation = new Installation("installation-id", NotificationPlatform.Adm, "adm-push-channel", "user-id");
		hub.createOrUpdateInstallation(installation);
		Thread.sleep(3000);
		
		installation = hub.getInstallation(installation.getInstallationId());
		assertNotNull(installation);
		assertEquals("installation-id", installation.getInstallationId());
		assertEquals("user-id", installation.getUserId());
		assertEquals(NotificationPlatform.Adm, installation.getPlatform());
		assertEquals("adm-push-channel", installation.getPushChannel());
		assertNull(installation.getTags());
		assertNull(installation.getTemplates());
		assertNull(installation.getSecondaryTiles());
		
		installation.addTag("foo");
		installation.addTemplate("template1", new InstallationTemplate("{\"data\":{\"key1\":\"value1\"}}"));
		hub.createOrUpdateInstallation(installation);
		Thread.sleep(3000);
		
		installation = hub.getInstallation(installation.getInstallationId());
		assertEquals("installation-id", installation.getInstallationId());
		assertEquals("user-id", installation.getUserId());
		assertEquals(NotificationPlatform.Adm, installation.getPlatform());
		assertEquals("adm-push-channel", installation.getPushChannel());
		assertNotNull(installation.getTags());
		assertEquals(1, installation.getTags().size());
		assertTrue(installation.getTags().get(0).equalsIgnoreCase("foo"));
		assertNotNull(installation.getTemplates());
		assertEquals(1, installation.getTemplates().size());
		assertTrue(installation.getTemplates().get("template1").getBody().equalsIgnoreCase("{\"data\":{\"key1\":\"value1\"}}"));
		List<String> templateTags = installation.getTemplates().get("template1").getTags();
		assertNotNull(templateTags);
		assertEquals(1, templateTags.size());
		assertTrue(templateTags.get(0).equalsIgnoreCase("template1"));
		assertNull(installation.getSecondaryTiles());
		
		PartialUpdateOperation addChannel = new PartialUpdateOperation(UpdateOperationType.Replace, "/pushChannel", "adm-push-channel2");
		PartialUpdateOperation addTag = new PartialUpdateOperation(UpdateOperationType.Add, "/tags", "bar");
		PartialUpdateOperation replaceTemplate = new PartialUpdateOperation(UpdateOperationType.Replace, "/templates/template1", new InstallationTemplate("{\"data\":{\"key2\":\"value2\"}}").toJson());
		PartialUpdateOperation replaceUserId = new PartialUpdateOperation(UpdateOperationType.Replace, "/userId", "user-id-patched");
		hub.patchInstallation(installation.getInstallationId(), addChannel, addTag, replaceTemplate, replaceUserId);
		Thread.sleep(3000);
		
		installation = hub.getInstallation(installation.getInstallationId());
		assertNotNull(installation);
		assertEquals("installation-id", installation.getInstallationId());
		assertEquals("user-id-patched", installation.getUserId());
		assertEquals(NotificationPlatform.Adm, installation.getPlatform());
		assertEquals("adm-push-channel2", installation.getPushChannel());
		assertNotNull(installation.getTags());
		assertEquals(2, installation.getTags().size());
 		assertTrue(installation.getTags().contains("foo"));
 		assertTrue(installation.getTags().contains("bar"));
		assertNotNull(installation.getTemplates());
		assertEquals(1, installation.getTemplates().size());
		assertTrue(installation.getTemplates().get("template1").getBody().equalsIgnoreCase("{\"data\":{\"key2\":\"value2\"}}"));
		templateTags = installation.getTemplates().get("template1").getTags();
		assertNotNull(templateTags);
		assertEquals(1, templateTags.size());
		assertTrue(templateTags.get(0).equalsIgnoreCase("template1"));
		assertNull(installation.getSecondaryTiles());
		
		PartialUpdateOperation removeUserId = new PartialUpdateOperation(UpdateOperationType.Remove, "/userId");
		hub.patchInstallation(installation.getInstallationId(), removeUserId);
		Thread.sleep(3000);
		
		installation = hub.getInstallation(installation.getInstallationId());
		assertNotNull(installation);
		assertNull(installation.getUserId());
		
		hub.deleteInstallation(installation.getInstallationId());
		Thread.sleep(3000);
		
		assertEquals(0, hub.getRegistrationsByTag("$InstallationId:%7B"+ installation.getInstallationId() +"%7D").getRegistrations().size());
	}
}
