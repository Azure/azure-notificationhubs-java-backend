package com.windowsazure.messaging.e2e;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.windowsazure.messaging.AdmCredential;
import com.windowsazure.messaging.GcmCredential;
import com.windowsazure.messaging.NamespaceManager;
import com.windowsazure.messaging.NotificationHubDescription;

public class HubCrudsE2E {
	private String connectionString;
	private String hubPath;
	private String gcmKey; 
	private String admId; 
	private String admSecret; 

	@Before
	public void setUp() throws Exception {
		Properties p = new Properties();
		p.load(this.getClass().getResourceAsStream("e2eSetup.properties"));		
		connectionString = p.getProperty("connectionstring");
		assertTrue(connectionString!=null && !connectionString.isEmpty());
		gcmKey = p.getProperty("gcmkey");
		admId = p.getProperty("admid");
		admSecret = p.getProperty("admsecret");
		hubPath = "JavaSDK_" + UUID.randomUUID().toString();	
	}
	
	@Test
	public void BasicCrudScenarioTest() throws Exception{
		NamespaceManager nsm = new NamespaceManager(connectionString);
				 	
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setGcmCredential(new GcmCredential(gcmKey));
		nsm.createNotificationHub(hub);
		
		Thread.sleep(1000);
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getGcmCredential());
		assertEquals(gcmKey, hub.getGcmCredential().getGoogleApiKey());
		
		hub.setAdmCredential(new AdmCredential(admId, admSecret));
		nsm.updateNotificationHub(hub);
		Thread.sleep(1000);
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getGcmCredential());
		assertEquals(gcmKey, hub.getGcmCredential().getGoogleApiKey());
		assertNotNull(hub.getAdmCredential());
		assertEquals(admId, hub.getAdmCredential().getClientId());
		assertEquals(admSecret, hub.getAdmCredential().getClientSecret());
		
		nsm.DeleteNotificationHub(hubPath);		
	}
	
	@Test
	public void GetCollectionTest() throws Exception{
		NamespaceManager nsm = new NamespaceManager(connectionString);
					
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setGcmCredential(new GcmCredential(gcmKey));
		nsm.createNotificationHub(hub);
		
		Thread.sleep(1000);
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
				
		nsm.DeleteNotificationHub(hubPath);	
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
}
