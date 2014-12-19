package com.windowsazure.messaging.e2e;

import static org.junit.Assert.*;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.junit.*;

import com.windowsazure.messaging.*;

public class ImportExportE2E {
	private INotificationHub hub;
	private NamespaceManager namespaceManager;
	private String hubPath;	
	private String storageAccountName;
	private String storageAccountKey;
	private String storageContainer;
	private String storageImportFile; 

	@Before
	public void setUp() throws Exception {
		Properties p = new Properties();		
		p.load(this.getClass().getResourceAsStream("e2eSetup.properties"));		
		
		storageAccountName = p.getProperty("storageaccountname");
		assertTrue(storageAccountName!=null && !storageAccountName.isEmpty());
		storageAccountKey =	p.getProperty("storageaccountkey");
		assertTrue(storageAccountKey!=null && !storageAccountKey.isEmpty());
		storageContainer =	p.getProperty("storagecontainer");
		assertTrue(storageContainer!=null && !storageContainer.isEmpty());
		storageImportFile = p.getProperty("storageimportfile");
		
		String connectionString = p.getProperty("connectionstring");
		assertTrue(connectionString!=null && !connectionString.isEmpty());
		
		hubPath = "JavaSDK_" + UUID.randomUUID().toString();			
		namespaceManager = new NamespaceManager(connectionString);
	 	NotificationHubDescription hubDescription = new NotificationHubDescription(hubPath);
	 	namespaceManager.createNotificationHub(hubDescription);		
		Thread.sleep(1000);
		
		hub = new NotificationHub(connectionString, hubPath);
	}
	
	@After
	public void cleanUp() throws Exception {
		assertNotNull(hubPath);
		namespaceManager.deleteNotificationHub(hubPath);
	}
	
	public static String createSasSignedUrl(String accountName, String accountKey, String containerName, String fileName, String permissions, Date expirationTimeUtc) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String expirationTime = df.format(expirationTimeUtc);				
		String resourceType = fileName == null ? "c" : "b";		
		String resourceName = "/" + accountName + "/" + containerName + (fileName == null ? "" : "/" + fileName);
		
		String stringToSign = String.format("%s\n%s\n%s\n%s\n%s\n%s",
				permissions,
                "", // start time
                expirationTime,
                resourceName,
                "", // access policy identifier
                "2012-02-12"// target storage version            
        );
			
		SecretKeySpec key = new SecretKeySpec(Base64.decodeBase64(accountKey), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(key);		
		byte[] utf8Bytes = stringToSign.getBytes("UTF-8");
        String sasToken = Base64.encodeBase64String(mac.doFinal(utf8Bytes));
		
		String url = String.format("https://%s.blob.core.windows.net/%s%s?sv=2012-02-12&amp;se=%s&amp;sr=%s&amp;sp=%s&amp;sig=%s",
				accountName,
				containerName,
				fileName == null ? "" : "/" + fileName,
				URLEncoder.encode(expirationTime, "UTF-8"),
				resourceType,
				permissions,
				URLEncoder.encode(sasToken, "UTF-8"));
		
		return url;
	}
	
	@Test
	public void ImportTest() throws Exception{
		assertTrue(storageImportFile!=null && !storageImportFile.isEmpty());
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 2);
		
		NotificationHubJob job = new NotificationHubJob();
		job.setJobType(NotificationHubJobType.ImportCreateRegistrations);
		job.setImportFileUri(createSasSignedUrl(storageAccountName, storageAccountKey, storageContainer, storageImportFile, "r", c.getTime()));
		job.setOutputContainerUri(createSasSignedUrl(storageAccountName, storageAccountKey, storageContainer, null, "rwl", c.getTime()));
				
		job = hub.submitNotificationHubJob(job);
		assertNotNull(job);
		assertNotNull(job.getJobId());
		
		// Check if get all works
		List<NotificationHubJob> jobs = hub.getAllNotificationHubJobs();
		assertNotNull(jobs);
		assertEquals(1, jobs.size());
		
		// Wait until job is done
		int n = 0;
		while(true){
			Thread.sleep(1000);
			assertTrue(++n<10);
			job = hub.getNotificationHubJob(job.getJobId());
			assertNotNull(job);
			assertNotNull(job.getJobId());			
			if(job.getJobStatus() == NotificationHubJobStatus.Completed)
				break;
		}		
	}	
	
	@Test
	public void ExportTest() throws Exception{
		assertTrue(storageImportFile!=null && !storageImportFile.isEmpty());
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 2);
		
		NotificationHubJob job = new NotificationHubJob();
		job.setJobType(NotificationHubJobType.ExportRegistrations);
		job.setOutputContainerUri(createSasSignedUrl(storageAccountName, storageAccountKey, storageContainer, null, "rwl", c.getTime()));
		job = hub.submitNotificationHubJob(job);
		assertNotNull(job);
		assertNotNull(job.getJobId());
		
		// Check if get all works
		List<NotificationHubJob> jobs = hub.getAllNotificationHubJobs();
		assertNotNull(jobs);
		assertEquals(1, jobs.size());
		
		// Wait until job is done
		int n = 0;
		while(true){
			Thread.sleep(1000);
			assertTrue(++n<10);
			job = hub.getNotificationHubJob(job.getJobId());
			assertNotNull(job);
			assertNotNull(job.getJobId());			
			if(job.getJobStatus() == NotificationHubJobStatus.Completed)
				break;
		}		
	}
}
