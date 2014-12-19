package com.windowsazure.messaging.e2e;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.windowsazure.messaging.*;

public class HubCrudsE2E {
	private String connectionString;
	private String hubPath;
	private String gcmkey;
	private String admid;
	private String admsecret;
	private String apnscert;
	private String apnskey;
	private String mpnscert;
	private String mpnskey;
	private String winsid;
	private String winkey; 
	private String baidukey;
	private String baidusecret; 

	@Before
	public void setUp() throws Exception {
		Properties p = new Properties();
		p.load(this.getClass().getResourceAsStream("e2eSetup.properties"));		
		connectionString = p.getProperty("connectionstring");
		assertTrue(connectionString!=null && !connectionString.isEmpty());
		gcmkey = p.getProperty("gcmkey");
		admid = p.getProperty("admid");
		admsecret = p.getProperty("admsecret");
		apnscert = p.getProperty("apnscert");
		apnskey = p.getProperty("apnskey");
		mpnscert = p.getProperty("mpnscert");
		mpnskey = p.getProperty("mpnskey");
		winsid = p.getProperty("winsid");
		winkey = p.getProperty("winkey");
		baidukey = p.getProperty("baidukey");
		baidusecret = p.getProperty("baidusecret");
		hubPath = "JavaSDK_" + UUID.randomUUID().toString();	
	}	
	
	@Test
	public void GcmCrudsTest() throws Exception{
	 	assertTrue(gcmkey!=null && !gcmkey.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
		
		// Create new
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setGcmCredential(new GcmCredential(gcmkey));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getGcmCredential());
		assertEquals(gcmkey, hub.getGcmCredential().getGoogleApiKey());
		
		// Get by path
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getGcmCredential());
		assertEquals(gcmkey, hub.getGcmCredential().getGoogleApiKey());
		
		// Update
		hub = nsm.updateNotificationHub(hub);
		assertNotNull(hub);
		assertNotNull(hub.getGcmCredential());
		assertEquals(gcmkey, hub.getGcmCredential().getGoogleApiKey());
		
		// Get collection
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
		
		// Delete
		nsm.deleteNotificationHub(hubPath);		
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
	
	@Test
	public void AdmCrudsTest() throws Exception{
		assertTrue(admid!=null && !admid.isEmpty() && admsecret!=null && !admsecret.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
		
		// Create new
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setAdmCredential(new AdmCredential(admid, admsecret));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getAdmCredential());
		assertEquals(admid, hub.getAdmCredential().getClientId());
		assertEquals(admsecret, hub.getAdmCredential().getClientSecret());
		
		// Get by path
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getAdmCredential());
		assertEquals(admid, hub.getAdmCredential().getClientId());
		assertEquals(admsecret, hub.getAdmCredential().getClientSecret());
		
		// Update
		hub = nsm.updateNotificationHub(hub);
		assertNotNull(hub);
		assertNotNull(hub.getAdmCredential());
		assertEquals(admid, hub.getAdmCredential().getClientId());
		assertEquals(admsecret, hub.getAdmCredential().getClientSecret());
		
		// Get collection
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
		
		// Delete
		nsm.deleteNotificationHub(hubPath);		
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
	
	@Test
	public void ApnsCrudsTest() throws Exception{
	 	assertTrue(apnscert!=null && !apnscert.isEmpty() && apnskey!=null && !apnskey.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
		
		// Create new
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setApnsCredential(new ApnsCredential(apnscert, apnskey));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getApnsCredential());
		assertEquals(apnscert, hub.getApnsCredential().getApnsCertificate());
		assertEquals(apnskey, hub.getApnsCredential().getCertificateKey());
		
		// Get by path
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getApnsCredential());
		assertEquals(apnscert, hub.getApnsCredential().getApnsCertificate());
		assertEquals(apnskey, hub.getApnsCredential().getCertificateKey());
		
		// Update
		hub = nsm.updateNotificationHub(hub);
		assertNotNull(hub);
		assertNotNull(hub.getApnsCredential());
		assertEquals(apnscert, hub.getApnsCredential().getApnsCertificate());
		assertEquals(apnskey, hub.getApnsCredential().getCertificateKey());
		
		// Get collection
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
		
		// Delete
		nsm.deleteNotificationHub(hubPath);		
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
	
	@Test
	public void MpnsCrudsTest() throws Exception{
	 	assertTrue(mpnscert!=null && !mpnscert.isEmpty() && mpnskey!=null && !mpnskey.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
		
		// Create new
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setMpnsCredential(new MpnsCredential(mpnscert, mpnskey));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getMpnsCredential());
		assertEquals(mpnscert, hub.getMpnsCredential().getMpnsCertificate());
		assertEquals(mpnskey, hub.getMpnsCredential().getCertificateKey());
		
		// Get by path
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getMpnsCredential());
		assertEquals(mpnscert, hub.getMpnsCredential().getMpnsCertificate());
		assertEquals(mpnskey, hub.getMpnsCredential().getCertificateKey());
		
		// Update
		hub = nsm.updateNotificationHub(hub);
		assertNotNull(hub);
		assertNotNull(hub.getMpnsCredential());
		assertEquals(mpnscert, hub.getMpnsCredential().getMpnsCertificate());
		assertEquals(mpnskey, hub.getMpnsCredential().getCertificateKey());
		
		// Get collection
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
		
		// Delete
		nsm.deleteNotificationHub(hubPath);		
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
	
	@Test
	public void WnsCrudsTest() throws Exception{
	 	assertTrue(winsid!=null && !winsid.isEmpty() && winkey!=null && !winkey.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
		
		// Create new
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setWindowsCredential(new WindowsCredential(winsid,winkey));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getWindowsCredential());
		assertEquals(winsid, hub.getWindowsCredential().getPackageSid());
		assertEquals(winkey, hub.getWindowsCredential().getSecretKey());
		
		// Get by path
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getWindowsCredential());
		assertEquals(winsid, hub.getWindowsCredential().getPackageSid());
		assertEquals(winkey, hub.getWindowsCredential().getSecretKey());
		
		// Update
		hub = nsm.updateNotificationHub(hub);
		assertNotNull(hub);
		assertNotNull(hub.getWindowsCredential());
		assertEquals(winsid, hub.getWindowsCredential().getPackageSid());
		assertEquals(winkey, hub.getWindowsCredential().getSecretKey());
		
		// Get collection
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
		
		// Delete
		nsm.deleteNotificationHub(hubPath);		
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
	
	@Test
	public void BaiduCrudsTest() throws Exception{
	 	assertTrue(baidukey!=null && !baidukey.isEmpty() && baidusecret!=null && !baidusecret.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
		
		// Create new
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setBaiduCredential(new BaiduCredential(baidukey, baidusecret));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getBaiduCredential());
		assertEquals(baidukey, hub.getBaiduCredential().getBaiduApiKey());
		assertEquals(baidusecret, hub.getBaiduCredential().getBaiduSecretKey());
		
		// Get by path
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getBaiduCredential());
		assertEquals(baidukey, hub.getBaiduCredential().getBaiduApiKey());
		assertEquals(baidusecret, hub.getBaiduCredential().getBaiduSecretKey());
		
		// Update
		hub = nsm.updateNotificationHub(hub);
		assertNotNull(hub);
		assertNotNull(hub.getBaiduCredential());
		assertEquals(baidukey, hub.getBaiduCredential().getBaiduApiKey());
		assertEquals(baidusecret, hub.getBaiduCredential().getBaiduSecretKey());
		
		// Get collection
		List<NotificationHubDescription> hubs = nsm.getNotificationHubs();
		assertTrue(hubs.size() > 0);
		boolean found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath().equalsIgnoreCase(hubPath);
		assertTrue(found);
		
		// Delete
		nsm.deleteNotificationHub(hubPath);		
		Thread.sleep(1000);
		hubs = nsm.getNotificationHubs();
		found = false;
		for (NotificationHubDescription h : hubs) found |=h.getPath() == hubPath;
		assertFalse(found);
	}
	
	@Test
	public void CreateWithBaiduThenAddAdmTest() throws Exception{		
		assertTrue(admid!=null && !admid.isEmpty() && admsecret!=null && !admsecret.isEmpty());
		assertTrue(baidukey!=null && !baidukey.isEmpty() && baidusecret!=null && !baidusecret.isEmpty());
		
		NamespaceManager nsm = new NamespaceManager(connectionString);
				 	
		NotificationHubDescription hub = new NotificationHubDescription(hubPath);
		hub.setBaiduCredential(new BaiduCredential(baidukey, baidusecret));
		hub = nsm.createNotificationHub(hub);
		Thread.sleep(1000);		
		assertNotNull(hub);
		assertNotNull(hub.getBaiduCredential());
		assertEquals(baidukey, hub.getBaiduCredential().getBaiduApiKey());
		assertEquals(baidusecret, hub.getBaiduCredential().getBaiduSecretKey());
		
		Thread.sleep(1000);
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getBaiduCredential());
		assertEquals(baidukey, hub.getBaiduCredential().getBaiduApiKey());
		assertEquals(baidusecret, hub.getBaiduCredential().getBaiduSecretKey());
		
		hub.setAdmCredential(new AdmCredential(admid, admsecret));
		nsm.updateNotificationHub(hub);
		Thread.sleep(1000);
		hub = nsm.getNotificationHub(hubPath);
		assertNotNull(hub);
		assertNotNull(hub.getBaiduCredential());
		assertEquals(baidukey, hub.getBaiduCredential().getBaiduApiKey());
		assertEquals(baidusecret, hub.getBaiduCredential().getBaiduSecretKey());
		assertNotNull(hub.getAdmCredential());
		assertEquals(admid, hub.getAdmCredential().getClientId());
		assertEquals(admsecret, hub.getAdmCredential().getClientSecret());
		
		nsm.deleteNotificationHub(hubPath);		
	}
}
