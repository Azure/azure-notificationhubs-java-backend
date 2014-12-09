package com.windowsazure.messaging;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;


public class NotificationHubJobParseTest {
		
	@Test
	public void testParseNotificationHubJob() throws IOException, SAXException, URISyntaxException {
		InputStream inputXml = this.getClass().getResourceAsStream("NotificationHubJobIncoming");		
		NotificationHubJob job = NotificationHubJob.parseOne(inputXml);
		assertNotNull(job);
		assertEquals("1", job.getJobId());
		assertEquals(99.99, job.getProgress(), 0.001);
		assertEquals(NotificationHubJobType.ImportCreateRegistrations, job.getJobType());
		assertEquals(NotificationHubJobStatus.Completed, job.getJobStatus());
		assertEquals("https://test.blob.core.windows.net/testjobs", job.getOutputContainerUri());
		assertEquals("https://test.blob.core.windows.net/testjobs/CreateFile.txt", job.getImportFileUri());
		assertTrue(job.getCreatedAt().before(job.getUpdatedAt()));
		assertNotNull(job.getOutputProperties());
		assertEquals(2, job.getOutputProperties().size());
		assertEquals("test//hub/1/Output.txt", job.getOutputProperties().get("OutputFilePath"));
		assertEquals("test//hub/1/Failed.txt", job.getOutputProperties().get("FailedFilePath"));
				
		String expectedResultXml = IOUtils.toString(this.getClass().getResourceAsStream("NotificationHubJobOutgoing"));	
		String  actualResultXml = job.getXml();
		assertEquals(expectedResultXml, actualResultXml);	
	}	
	
	@Test
	public void testParseNotificationHubsFeed() throws Exception {
		InputStream inputXml = this.getClass().getResourceAsStream("NotificationHubJobFeed");		
		List<NotificationHubJob> jobs = NotificationHubJob.parseCollection(inputXml);
		assertNotNull(jobs);
		assertEquals(3, jobs.size());
	}
}
