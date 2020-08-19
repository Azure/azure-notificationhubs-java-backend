//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging.e2e;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.windowsazure.messaging.*;

public class RegistrationCrudsE2E {

	private static final String CHANNELURI = "https://bn1.notify.windows.com/?token=AgYAAADYej13M9aml3liD9nlfJw6FEgGXDvYmKDOfOwcS2ekCUm7hIrsJhGqkvU35pmJHFmXVbeUKJawqNHQKCtNJaI4z3uf3Gn04nrdSMUgzFapd%2fXYwzREnjz6%2fk9Pl6cy%2bdI%3d";
	private static final String CHANNELURI2 = "https://bn1.notify.windows.com/?token=12345ADYej13M9aml3liD9nlfJw6FEgGXDvYmKDOfOwcS2ekCUm7hIrsJhGqkv12345JHFmXVbeUKJawqNHQKCtNJaI4z3uf3Gn04nrdSMUgzFapd%2fXYwzREnjz6%2fk9Pl6cy%2bdI%3d";
	private static final String WNSRAWNOTIFICATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	private static final String WNSBODYTEMPLATE = "<toast><visual><binding template=\"ToastText01\"><text id=\"1\">From any .NET App!</text></binding></visual></toast>";
	private static final String WNSBODYTEMPLATE2 = "<toast><visual><binding template=\"ToastText01\"><text id=\"1\">From any .NET App! Second take!</text></binding></visual></toast>";
	private static final String DEVICETOKEN = "ABCDEF";
	private static final String DEVICETOKEN2 = "123456";
	private static final String APNSBODYTEMPLATE = "{\"aps\": {\"alert\": \"$(message)\"}}";
	private static final String APNSBODYTEMPLATE2 = "{\"aps\": {\"alert\": \"$(msg)\"}}";
	private static final String EXPIRYTEMPLATE = "$(expiry)";
	private static final String GCMREGID = "ABCDEF";
	private static final String GCMREGID2 = "123456";
	private static final String GCMBODYTEMPLATE = "{\"aps\": {\"alert\": \"$(message)\"}}";
	private static final String GCMBODYTEMPLATE2 = "{\"aps\": {\"alert\": \"$(msg)\"}}";
	private static final String MPNSCHANNELURI = "http://dm2.notify.live.net/throttledthirdparty/01.00/12G9Ed13dLb5RbCii5fWzpFpAgAAAAADAQAAAAQUZm52OkJCMjg1QTg1QkZDMkUxREQFBlVTTkMwMQ";
	private static final String MPNSCHANNELURI2 = "http://dm2.notify.live.net/throttledthirdparty/01.00/12G9Ed13-Lb5RbCii5fWzpFpAgAAAAADAQAAAAQUZm52OkJCMjg1QTg1QkZDMkUxREQFBlVTTkMwMQ";
	private static final String MPNSBODYTEMPLATE = "<wp:Notification xmlns:wp=\"WPNotification\"><wp:Toast><wp:Text1>$(message)</wp:Text1></wp:Toast></wp:Notification>";
	private static final String MPNSBODYTEMPLATE2 = "<wp:Notification xmlns:wp=\"WPNotification\"><wp:Toast><wp:Text1>$(msg)</wp:Text1></wp:Toast></wp:Notification>";
	private static final String ADMREGID = "ABCDEF";
	private static final String ADMREGID2 = "123456";
	private static final String ADMBODYTEMPLATE = "{\"data\":{\"key1\":\"$(value1)\"}}";
	private static final String ADMBODYTEMPLATE2 = "{\"data\":{\"key1\":\"$(value2)\"}}";
	private static final String BAIDUUSER1 = "baidu_user1";
	private static final String BAIDUCHANNEL1 = "baidu_channel1";
	private static final String BAIDUBODYTEMPLATE1= "{\"data\":{\"key1\":\"$(value1)\"}}";
	private static final String BAIDUUSER2 = "baidu_user2";
	private static final String BAIDUCHANNEL2 = "baidu_channel2";
	private static final String BAIDUBODYTEMPLATE2= "{\"data\":{\"key2\":\"$(value2)\"}}";

	private NotificationHub hub;
	private String hubPath;
	private NamespaceManager namespaceManager;
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

		String connectionString = p.getProperty("connectionstring");
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
		NotificationHubDescription hubDescription = new NotificationHubDescription(hubPath);
	 	if(admid!=null && !admid.isEmpty() && admsecret!=null && !admsecret.isEmpty())
	 		hubDescription.setAdmCredential(new AdmCredential(admid,admsecret));
	 	if(gcmkey!=null && !gcmkey.isEmpty())
	 		hubDescription.setGcmCredential(new GcmCredential(gcmkey));
	 	if(apnscert!=null && !apnscert.isEmpty() && apnskey!=null && !apnskey.isEmpty())
	 		hubDescription.setApnsCredential(new ApnsCredential(apnscert,apnskey));
	 	if(mpnscert!=null && !mpnscert.isEmpty() && mpnskey!=null && !mpnskey.isEmpty())
	 		hubDescription.setMpnsCredential(new MpnsCredential(mpnscert,mpnskey));
	 	if(winsid!=null && !winsid.isEmpty() && winkey!=null && !winkey.isEmpty())
	 		hubDescription.setWindowsCredential(new WindowsCredential(winsid,winkey));
	 	if(baidukey!=null && !baidukey.isEmpty() && baidusecret!=null && !baidusecret.isEmpty())
	 		hubDescription.setBaiduCredential(new BaiduCredential(baidukey,baidusecret));

	 	namespaceManager = new NamespaceManager(connectionString, new RetryOptions());
	 	namespaceManager.createNotificationHubAsync(hubDescription).block();
		Thread.sleep(1000);

		hub = new NotificationHub(connectionString, hubPath, new RetryOptions());
	}

	@After
	public void cleanUp() throws Exception {
		assertNotNull(hubPath);
		namespaceManager.deleteNotificationHubAsync(hubPath).block();
	}

	@Test
	public void testCreateRegistrationId() {
		String id = hub.createRegistrationIdAsync().block();

		assertNotNull(id);
	}

	@Test
	public void testCreateRegistrationIdAndUpsert() throws URISyntaxException {
		String id = hub.createRegistrationIdAsync().block();
		assertNotNull(id);

		WindowsRegistration reg = new WindowsRegistration(id, new URI(CHANNELURI));

		WindowsRegistration reg2 = (WindowsRegistration) hub.upsertRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(new URI(CHANNELURI), reg2.getChannelUri());
		assertEquals(id, reg2.getRegistrationId());

		reg2.setChannelUri(CHANNELURI2);

		WindowsRegistration reg3 = (WindowsRegistration) hub.upsertRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(new URI(CHANNELURI2), reg3.getChannelUri());
		assertEquals(id, reg3.getRegistrationId());

		hub.deleteRegistrationAsync(reg3.getRegistrationId()).block();
	}

	// create + update + get + delete
	@Test
	public void testCreateAndDeleteNativeRegistration() throws URISyntaxException {
		WindowsRegistration reg = new WindowsRegistration(new URI(CHANNELURI));
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		WindowsRegistration reg2 = (WindowsRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(new URI(CHANNELURI), reg2.getChannelUri());
		assertEquals(2, reg2.getTags().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setChannelUri(CHANNELURI2);
		reg2.getTags().remove("myTag");

		WindowsRegistration reg3 = (WindowsRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(new URI(CHANNELURI2), reg3.getChannelUri());
		assertEquals(1, reg3.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		WindowsRegistration reg4 = (WindowsRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(new URI(CHANNELURI2), reg4.getChannelUri());
		assertEquals(1, reg4.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteTemplateRegistration() throws URISyntaxException {
		WindowsTemplateRegistration reg = new WindowsTemplateRegistration(new URI(CHANNELURI), WNSBODYTEMPLATE);
		reg.getHeaders().put("X-WNS-Type", "wns/toast");
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		WindowsTemplateRegistration reg2 = (WindowsTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(new URI(CHANNELURI), reg2.getChannelUri());
		assertEquals(2, reg2.getTags().size());
		assertEquals(WNSBODYTEMPLATE, reg2.getBodyTemplate());
		assertEquals(1, reg2.getHeaders().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setChannelUri(CHANNELURI2);
		reg2.getTags().remove("myTag");
		reg2.setBodyTemplate(WNSBODYTEMPLATE2);

		WindowsTemplateRegistration reg3 = (WindowsTemplateRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(new URI(CHANNELURI2), reg3.getChannelUri());
		assertEquals(1, reg3.getTags().size());
		assertEquals(WNSBODYTEMPLATE2, reg3.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		WindowsTemplateRegistration reg4 = (WindowsTemplateRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(new URI(CHANNELURI2), reg4.getChannelUri());
		assertEquals(1, reg4.getTags().size());
		assertEquals(WNSBODYTEMPLATE2, reg4.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteAppleNativeRegistration() {
		AppleRegistration reg = new AppleRegistration(DEVICETOKEN);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		AppleRegistration reg2 = (AppleRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(DEVICETOKEN, reg2.getDeviceToken());
		assertEquals(2, reg2.getTags().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setDeviceToken(DEVICETOKEN2);
		reg2.getTags().remove("myTag");

		AppleRegistration reg3 = (AppleRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(DEVICETOKEN2, reg3.getDeviceToken());
		assertEquals(1, reg3.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		AppleRegistration reg4 = (AppleRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(DEVICETOKEN2, reg4.getDeviceToken());
		assertEquals(1, reg4.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteAppleTemplateRegistration() {
		AppleTemplateRegistration reg = new AppleTemplateRegistration(DEVICETOKEN, APNSBODYTEMPLATE);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		AppleTemplateRegistration reg2 = (AppleTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(DEVICETOKEN, reg2.getDeviceToken());
		assertEquals(2, reg2.getTags().size());
		assertEquals(APNSBODYTEMPLATE, reg2.getBodyTemplate());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setDeviceToken(DEVICETOKEN2);
		reg2.getTags().remove("myTag");
		reg2.setBodyTemplate(APNSBODYTEMPLATE2);
		reg2.setExpiry(EXPIRYTEMPLATE);

		AppleTemplateRegistration reg3 = (AppleTemplateRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(DEVICETOKEN2, reg3.getDeviceToken());
		assertEquals(1, reg3.getTags().size());
		assertEquals(EXPIRYTEMPLATE, reg3.getExpiry());
		assertEquals(APNSBODYTEMPLATE2, reg3.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		AppleTemplateRegistration reg4 = (AppleTemplateRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(DEVICETOKEN2, reg4.getDeviceToken());
		assertEquals(1, reg4.getTags().size());
		assertEquals(EXPIRYTEMPLATE, reg3.getExpiry());
		assertEquals(APNSBODYTEMPLATE2, reg4.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteGcmNativeRegistration() {
		GcmRegistration reg = new GcmRegistration(GCMREGID);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		GcmRegistration reg2 = (GcmRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(GCMREGID, reg2.getGcmRegistrationId());
		assertEquals(2, reg2.getTags().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setGcmRegistrationId(GCMREGID2);
		reg2.getTags().remove("myTag");

		GcmRegistration reg3 = (GcmRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(GCMREGID2, reg3.getGcmRegistrationId());
		assertEquals(1, reg3.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		GcmRegistration reg4 = (GcmRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(GCMREGID2, reg4.getGcmRegistrationId());
		assertEquals(1, reg4.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteGcmTemplateRegistration() {
		GcmTemplateRegistration reg = new GcmTemplateRegistration(GCMREGID, GCMBODYTEMPLATE);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		GcmTemplateRegistration reg2 = (GcmTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(GCMREGID, reg2.getGcmRegistrationId());
		assertEquals(2, reg2.getTags().size());
		assertEquals(GCMBODYTEMPLATE, reg2.getBodyTemplate());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setGcmRegistrationId(GCMREGID2);
		reg2.getTags().remove("myTag");
		reg2.setBodyTemplate(GCMBODYTEMPLATE2);

		GcmTemplateRegistration reg3 = (GcmTemplateRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(GCMREGID2, reg3.getGcmRegistrationId());
		assertEquals(1, reg3.getTags().size());
		assertEquals(GCMBODYTEMPLATE2, reg3.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		GcmTemplateRegistration reg4 = (GcmTemplateRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(GCMREGID2, reg4.getGcmRegistrationId());
		assertEquals(1, reg4.getTags().size());
		assertEquals(GCMBODYTEMPLATE2, reg4.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteAdmNativeRegistration() {
		AdmRegistration reg = new AdmRegistration(ADMREGID);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		AdmRegistration reg2 = (AdmRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(ADMREGID, reg2.getAdmRegistrationId());
		assertEquals(2, reg2.getTags().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setAdmRegistrationId(GCMREGID2);
		reg2.getTags().remove("myTag");

		AdmRegistration reg3 = (AdmRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(ADMREGID2, reg3.getAdmRegistrationId());
		assertEquals(1, reg3.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		AdmRegistration reg4 = (AdmRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(ADMREGID2, reg4.getAdmRegistrationId());
		assertEquals(1, reg4.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteAdmTemplateRegistration() {
		AdmTemplateRegistration reg = new AdmTemplateRegistration(ADMREGID, ADMBODYTEMPLATE);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		AdmTemplateRegistration reg2 = (AdmTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(ADMREGID, reg2.getAdmRegistrationId());
		assertEquals(2, reg2.getTags().size());
		assertEquals(ADMBODYTEMPLATE, reg2.getBodyTemplate());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setAdmRegistrationId(ADMREGID2);
		reg2.getTags().remove("myTag");
		reg2.setBodyTemplate(ADMBODYTEMPLATE2);

		AdmTemplateRegistration reg3 = (AdmTemplateRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(ADMREGID2, reg3.getAdmRegistrationId());
		assertEquals(1, reg3.getTags().size());
		assertEquals(ADMBODYTEMPLATE2, reg3.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		AdmTemplateRegistration reg4 = (AdmTemplateRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(ADMREGID2, reg4.getAdmRegistrationId());
		assertEquals(1, reg4.getTags().size());
		assertEquals(ADMBODYTEMPLATE2, reg4.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testGetAllRegistration() throws URISyntaxException {
		WindowsTemplateRegistration reg = new WindowsTemplateRegistration(new URI(CHANNELURI), WNSBODYTEMPLATE);
		reg.getHeaders().put("X-WNS-Type", "wns/toast");
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		WindowsTemplateRegistration reg2 = (WindowsTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);

		WindowsRegistration reg3 = new WindowsRegistration(new URI(CHANNELURI2));
		reg3.getTags().add("myTag");

		WindowsRegistration reg4 = (WindowsRegistration) hub.createRegistrationAsync(reg3).block();
		assertNotNull(reg4);

		CollectionResult allRegs = hub.getRegistrationsAsync().block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNull(allRegs.getContinuationToken());
		assertEquals(2, allRegs.getRegistrations().size());

		allRegs = hub.getRegistrationsByTagAsync("myTag").block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNull(allRegs.getContinuationToken());
		assertEquals(2, allRegs.getRegistrations().size());

		allRegs = hub.getRegistrationsByTagAsync("myOtherTag").block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNull(allRegs.getContinuationToken());
		assertEquals(1, allRegs.getRegistrations().size());

		allRegs = hub.getRegistrationsByChannelAsync(CHANNELURI).block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNull(allRegs.getContinuationToken());
		assertEquals(1, allRegs.getRegistrations().size());


		hub.deleteRegistrationAsync(reg2.getRegistrationId()).block();
		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testGetAllRegistrationTopANdContinuation() throws URISyntaxException {
		CollectionResult allRegs = hub.getRegistrationsAsync(1, null).block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNull(allRegs.getContinuationToken());
		assertEquals(0, allRegs.getRegistrations().size());

		WindowsTemplateRegistration reg = new WindowsTemplateRegistration(new URI(CHANNELURI), WNSBODYTEMPLATE);
		reg.getHeaders().put("X-WNS-Type", "wns/toast");
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		WindowsTemplateRegistration reg2 = (WindowsTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);

		WindowsRegistration reg3 = new WindowsRegistration(new URI(CHANNELURI2));
		reg3.getTags().add("myTag");

		WindowsRegistration reg4 = (WindowsRegistration) hub.createRegistrationAsync(reg3).block();
		assertNotNull(reg4);

		allRegs = hub.getRegistrationsAsync(1, null).block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNotNull(allRegs.getContinuationToken());
		assertEquals(1, allRegs.getRegistrations().size());

		allRegs = hub.getRegistrationsAsync(1, allRegs.getContinuationToken()).block();
		assertNotNull(allRegs);
		assertNotNull(allRegs.getRegistrations());
		assertNull(allRegs.getContinuationToken());
		assertEquals(1, allRegs.getRegistrations().size());


		hub.deleteRegistrationAsync(reg2.getRegistrationId()).block();
		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteMpnsNativeRegistration() throws URISyntaxException {
		MpnsRegistration reg = new MpnsRegistration(new URI(MPNSCHANNELURI));
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		MpnsRegistration reg2 = (MpnsRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(new URI(MPNSCHANNELURI), reg2.getChannelUri());
		assertEquals(2, reg2.getTags().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setChannelUri(MPNSCHANNELURI2);
		reg2.getTags().remove("myTag");

		MpnsRegistration reg3 = (MpnsRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(new URI(MPNSCHANNELURI2), reg3.getChannelUri());
		assertEquals(1, reg3.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		MpnsRegistration reg4 = (MpnsRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(new URI(MPNSCHANNELURI2), reg4.getChannelUri());
		assertEquals(1, reg4.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteMpnsTemplateRegistration() throws URISyntaxException {
		MpnsTemplateRegistration reg = new MpnsTemplateRegistration(new URI(MPNSCHANNELURI), MPNSBODYTEMPLATE);
		reg.getHeaders().put("X-WindowsPhone-Target", "toast");
		reg.getHeaders().put("X-NotificationClass", "2");
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		MpnsTemplateRegistration reg2 = (MpnsTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(new URI(MPNSCHANNELURI), reg2.getChannelUri());
		assertEquals(2, reg2.getTags().size());
		assertEquals(MPNSBODYTEMPLATE, reg2.getBodyTemplate());
		assertEquals(2, reg2.getHeaders().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setChannelUri(MPNSCHANNELURI2);
		reg2.getTags().remove("myTag");
		reg2.setBodyTemplate(MPNSBODYTEMPLATE2);

		MpnsTemplateRegistration reg3 = (MpnsTemplateRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(new URI(MPNSCHANNELURI2), reg3.getChannelUri());
		assertEquals(1, reg3.getTags().size());
		assertEquals(MPNSBODYTEMPLATE2, reg3.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		MpnsTemplateRegistration reg4 = (MpnsTemplateRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(new URI(MPNSCHANNELURI2), reg4.getChannelUri());
		assertEquals(1, reg4.getTags().size());
		assertEquals(MPNSBODYTEMPLATE2, reg4.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	public void testCreateAndDeleteBaiduNativeRegistration() {
		BaiduRegistration reg = new BaiduRegistration(BAIDUUSER1, BAIDUCHANNEL1);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		BaiduRegistration reg2 = (BaiduRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(BAIDUUSER1, reg2.getBaiduUserId());
		assertEquals(BAIDUCHANNEL1, reg2.getBaiduChannelId());
		assertEquals(2, reg2.getTags().size());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setBaiduUserId(BAIDUUSER2);
		reg2.setBaiduChannelId(BAIDUCHANNEL2);
		reg2.getTags().remove("myTag");

		BaiduRegistration reg3 = (BaiduRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(BAIDUUSER2, reg3.getBaiduUserId());
		assertEquals(BAIDUCHANNEL2, reg3.getBaiduChannelId());
		assertEquals(1, reg3.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		BaiduRegistration reg4 = (BaiduRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(BAIDUUSER2, reg4.getBaiduUserId());
		assertEquals(BAIDUCHANNEL2, reg4.getBaiduChannelId());
		assertEquals(1, reg4.getTags().size());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}

	@Test
	public void testCreateAndDeleteBaiduTemplateRegistration() {
		BaiduTemplateRegistration reg = new BaiduTemplateRegistration(BAIDUUSER1, BAIDUCHANNEL1, BAIDUBODYTEMPLATE1);
		reg.getTags().add("myTag");
		reg.getTags().add("myOtherTag");

		BaiduTemplateRegistration reg2 = (BaiduTemplateRegistration) hub.createRegistrationAsync(reg).block();
		assertNotNull(reg2);
		assertEquals(BAIDUUSER1, reg2.getBaiduUserId());
		assertEquals(BAIDUCHANNEL1, reg2.getBaiduChannelId());
		assertEquals(2, reg2.getTags().size());
		assertEquals(BAIDUBODYTEMPLATE1, reg2.getBodyTemplate());
		assertNotNull(reg2.getRegistrationId());
		assertNotNull(reg2.getEtag());

		reg2.setBaiduUserId(BAIDUUSER2);
		reg2.setBaiduChannelId(BAIDUCHANNEL2);
		reg2.setBodyTemplate(BAIDUBODYTEMPLATE2);
		reg2.getTags().remove("myTag");

		BaiduTemplateRegistration reg3 = (BaiduTemplateRegistration) hub.updateRegistrationAsync(reg2).block();
		assertNotNull(reg3);
		assertEquals(BAIDUUSER2, reg3.getBaiduUserId());
		assertEquals(BAIDUCHANNEL2, reg3.getBaiduChannelId());
		assertEquals(1, reg3.getTags().size());
		assertEquals(BAIDUBODYTEMPLATE2, reg3.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg3.getRegistrationId());
		assertNotSame(reg2.getEtag(), reg3.getEtag());

		BaiduTemplateRegistration reg4 = (BaiduTemplateRegistration) hub.getRegistrationAsync(reg3.getRegistrationId()).block();
		assertNotNull(reg4);
		assertEquals(BAIDUUSER2, reg4.getBaiduUserId());
		assertEquals(BAIDUCHANNEL2, reg4.getBaiduChannelId());
		assertEquals(1, reg4.getTags().size());
		assertEquals(BAIDUBODYTEMPLATE2, reg4.getBodyTemplate());
		assertEquals(reg2.getRegistrationId(), reg4.getRegistrationId());
		assertEquals(reg3.getEtag(), reg4.getEtag());

		hub.deleteRegistrationAsync(reg4.getRegistrationId()).block();
	}


	// send
	@Test
	public void testSendWindowsNotification() {
		assertTrue(winsid!=null && !winsid.isEmpty() && winkey!=null && !winkey.isEmpty());

		Notification n = Notification.createWindowsNotification(WNSBODYTEMPLATE);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();
		hub.sendNotificationAsync(n, "foo && ! bar").block();
		hub.sendNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString())).block();
		hub.sendNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString())).block();
	}

	@Test
	public void testScheduleWindowsNotification() {
		assertTrue(winsid!=null && !winsid.isEmpty() && winkey!=null && !winkey.isEmpty());

		Notification n = Notification.createWindowsNotification(WNSBODYTEMPLATE);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
	}

	@Test
	public void testSendRawWindowsNotification() {
		assertTrue(winsid!=null && !winsid.isEmpty() && winkey!=null && !winkey.isEmpty());

		Notification n = Notification.createWindowsRawNotification(WNSRAWNOTIFICATION);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();
		hub.sendNotificationAsync(n, "foo && ! bar").block();
		hub.sendNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString())).block();
		hub.sendNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString())).block();
	}

	@Test
	public void testSendAppleNotification() {

		assertTrue(apnscert!=null && !apnscert.isEmpty() && apnskey!=null && !apnskey.isEmpty());
		Notification n = Notification.createAppleNotification(APNSBODYTEMPLATE);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();
		hub.sendNotificationAsync(n, "foo && ! bar").block();
		hub.sendNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString())).block();
		hub.sendNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString())).block();
	}

	@Test
	public void testScheduleAppleNotification()  throws NotificationHubsException{

		assertTrue(apnscert!=null && !apnscert.isEmpty() && apnskey!=null && !apnskey.isEmpty());
		Notification n = Notification.createAppleNotification(APNSBODYTEMPLATE);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
	}

	@Test
	public void testNotificationOutcomeOnSend() {
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());
		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);
		NotificationOutcome o = hub.sendNotificationAsync(n).block();

		assertNotNull(o);
		assertNotNull(o.getTrackingId());
		assertNotNull(o.getNotificationId());
	}

	@Test
	public void testNotificationOutcomeOnSchedule() {
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());

		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		NotificationOutcome o = hub.scheduleNotificationAsync(n, c.getTime()).block();

		assertNotNull(o);
		assertNotNull(o.getTrackingId());
		assertNotNull(o.getNotificationId());
	}

	@Test
	public void testCancelScheduledNotification()  throws NotificationHubsException{
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());

		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		NotificationOutcome o = hub.scheduleNotificationAsync(n, c.getTime()).block();

		assertNotNull(o);
		assertNotNull(o.getTrackingId());
		assertNotNull(o.getNotificationId());

		hub.cancelScheduledNotificationAsync(o.getNotificationId()).block();
	}

	@Test
	public void testSendDirectNotification()  throws NotificationHubsException{
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());
		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);
		NotificationOutcome o = hub.sendDirectNotificationAsync(n, GCMREGID).block();

		assertNotNull(o);
		assertNotNull(o.getTrackingId());
		assertNotNull(o.getNotificationId());
	}

	@Test
	public void testSendBatchDirectNotification()  throws NotificationHubsException{
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());
		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);
		NotificationOutcome o = hub.sendDirectNotificationAsync(n, Arrays.asList(GCMREGID,GCMREGID2)).block();

		assertNotNull(o);
		assertNotNull(o.getTrackingId());
		assertNotNull(o.getNotificationId());
	}

	@Test
	public void testGetNotificationTelemetry()  throws NotificationHubsException, InterruptedException{
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());
		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);
		NotificationOutcome o = hub.sendDirectNotificationAsync(n, Arrays.asList(GCMREGID,GCMREGID2)).block();

		assertNotNull(o);
		assertNotNull(o.getTrackingId());
		assertNotNull(o.getNotificationId());

		Thread.sleep(3000);
		NotificationTelemetry t = hub.getNotificationTelemetryAsync(o.getNotificationId()).block();
		assertNotNull(t);
		assertNotNull(t.getGcmOutcomeCounts());
		assertTrue(t.getGcmOutcomeCounts().containsKey("BadChannel"));
		assertTrue(t.getGcmOutcomeCounts().get("BadChannel")==2);
	}

	@Test
	public void testSendGcmNotification() {
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());

		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();
		hub.sendNotificationAsync(n, "foo && ! bar").block();
		hub.sendNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString())).block();
		hub.sendNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString())).block();
	}

	@Test
	public void testScheduleGcmNotification() {
		assertTrue(gcmkey!=null && !gcmkey.isEmpty());

		Notification n = Notification.createGcmNotification(GCMBODYTEMPLATE);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
	}

	@Test
	public void testSendAdmNotification() {
		assertTrue(admid!=null && !admid.isEmpty() && admsecret!=null && !admsecret.isEmpty());

		Notification n = Notification.createAdmNotification(ADMBODYTEMPLATE);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();

		hub.sendNotificationAsync(n, "foo && ! bar").block();
	}

	@Test
	public void testScheduleAdmNotification() {
		assertTrue(admid!=null && !admid.isEmpty() && admsecret!=null && !admsecret.isEmpty());

		Notification n = Notification.createAdmNotification(ADMBODYTEMPLATE);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
	}

	@Test
	public void testSendMpnsNotification() {
		assertTrue(mpnscert!=null && !mpnscert.isEmpty() && mpnskey!=null && !mpnskey.isEmpty());

		Notification n = Notification.createMpnsNotification(MPNSBODYTEMPLATE);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();

		hub.sendNotificationAsync(n, "foo && ! bar").block();
	}

	@Test
	public void testScheduleMpnsNotification() {
		assertTrue(mpnscert!=null && !mpnscert.isEmpty() && mpnskey!=null && !mpnskey.isEmpty());

		Notification n = Notification.createMpnsNotification(MPNSBODYTEMPLATE);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
	}

	public void testSendBaiduNotification()  throws NotificationHubsException{
		assertTrue(baidukey!=null && !baidukey.isEmpty() && baidusecret!=null && !baidusecret.isEmpty());

		Notification n = Notification.createBaiduNotification(BAIDUBODYTEMPLATE1);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();

		hub.sendNotificationAsync(n, "foo && ! bar").block();
	}

	@Test
	public void testScheduleBaiduNotification() {
		assertTrue(baidukey!=null && !baidukey.isEmpty() && baidusecret!=null && !baidusecret.isEmpty());

		Notification n = Notification.createBaiduNotification(BAIDUBODYTEMPLATE1);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
	}

	@Test
	public void testSendTemplateNotification() {
		Map<String, String> prop =  new HashMap<String, String>();
		prop.put("prop1", "v1");
		prop.put("prop2", "v2");
		Notification n = Notification.createTemplateNotification(prop);

		hub.sendNotificationAsync(n).block();

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.sendNotificationAsync(n, tags).block();
		hub.sendNotificationAsync(n, "foo && ! bar").block();
		hub.sendNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString())).block();
		hub.sendNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString())).block();
	}

	@Test
	public void testScheduleTemplateNotification() {
		Map<String, String> prop =  new HashMap<String, String>();
		prop.put("prop1", "v1");
		prop.put("prop2", "v2");
		Notification n = Notification.createTemplateNotification(prop);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);

		Set<String> tags = new HashSet<String>();
		tags.add("boo");
		tags.add("foo");

		hub.scheduleNotificationAsync(n, c.getTime()).block();
		hub.scheduleNotificationAsync(n, tags, c.getTime()).block();
		hub.scheduleNotificationAsync(n, "foo && ! bar", c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$UserId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
		hub.scheduleNotificationAsync(n, String.format("$InstallationId:{%s}", UUID.randomUUID().toString()), c.getTime()).block();
	}
}
