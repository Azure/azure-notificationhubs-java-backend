package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

public class NotificationHubJob {
	private static final String XML_HEADER="<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/atom+xml;type=entry;charset=utf-8\"><NotificationHubJob xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">"; 
	private static final String XML_FOOTER="</NotificationHubJob></content></entry>";
	
	private String jobId;
	private double progress;
	private NotificationHubJobType jobType;
	private NotificationHubJobStatus jobStatus;
	private String outputContainerUri;
	private String importFileUri;
	private String failure;
	private Map<String, String> outputProperties;
	private Date createdAt;
	private Date updatedAt;
	
	private static final ThreadLocal<Digester> singleEntryParser;
	private static final ThreadLocal<Digester> collectionParser;
	
	static {
		singleEntryParser = new ThreadLocal<Digester>(){
			@Override protected Digester initialValue() {
				Digester instance = new Digester();
				setupSingleEntryParser(instance);
                return instance;
             }
		};
		
		collectionParser = new ThreadLocal<Digester>(){
			@Override protected Digester initialValue() {
				Digester instance = new Digester();
				setupCollectionParser(instance);
                return instance;
             }
		};
	}	
	
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgressFromString(String progress) {
		this.progress = Double.parseDouble(progress);
	}

	public NotificationHubJobType getJobType() {
		return jobType;
	}
	
	public void setJobType(NotificationHubJobType jobType) {
		this.jobType = jobType;
	}

	public void setJobTypeFromString(String jobType) {
		this.jobType = Enum.valueOf(NotificationHubJobType.class, jobType);
	}

	public NotificationHubJobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatusFromString(String jobStatus) {
		this.jobStatus = Enum.valueOf(NotificationHubJobStatus.class, jobStatus);
	}

	public String getOutputContainerUri() {
		return outputContainerUri;
	}

	public void setOutputContainerUri(String outputContainerUri) {
		this.outputContainerUri = outputContainerUri;
	}

	public String getImportFileUri() {
		return importFileUri;
	}

	public void setImportFileUri(String importFileUri) {
		this.importFileUri = importFileUri;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public Map<String, String> getOutputProperties() {
		return outputProperties;
	}

	public void setOutputProperties(Map<String,String> outputProperties) {
		this.outputProperties = outputProperties;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAtFromString(String createdAt) {
		this.createdAt = javax.xml.bind.DatatypeConverter.parseDateTime(createdAt).getTime();
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAtFromString(String updatedAt) {
		this.updatedAt = javax.xml.bind.DatatypeConverter.parseDateTime(updatedAt).getTime();
	}	
		
	public static NotificationHubJob parseOne(InputStream content) throws IOException,	SAXException {
		return singleEntryParser.get().parse(content);
	}
	
	public static List<NotificationHubJob> parseCollection(InputStream content) throws IOException,	SAXException {
		return collectionParser.get().parse(content);
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append(XML_HEADER);
		if(this.jobType!=null) buf.append("<Type>" + this.jobType.name()+ "</Type>");
		if(this.outputContainerUri!=null) buf.append("<OutputContainerUri>" + this.outputContainerUri + "</OutputContainerUri>");
		if(this.importFileUri!=null) buf.append("<ImportFileUri>" + this.importFileUri + "</ImportFileUri>");
		buf.append(XML_FOOTER);
		return buf.toString();
	}	
	
	private static void setupCollectionParser(Digester digester){	
		digester.addObjectCreate("*/feed", LinkedList.class);
		setupSingleEntryParser(digester);
		digester.addSetNext("*/entry", "add", NotificationHubJob.class.getName());
	}
	
	private static void setupSingleEntryParser(Digester digester){
		digester.addObjectCreate("*/entry", NotificationHubJob.class);
		digester.addCallMethod("*/JobId", "setJobId",1);
		digester.addCallParam("*/JobId", 0);
		digester.addCallMethod("*/Progress", "setProgressFromString",1);
		digester.addCallParam("*/Progress", 0);
		digester.addCallMethod("*/Type", "setJobTypeFromString",1);
		digester.addCallParam("*/Type", 0);
		digester.addCallMethod("*/Status", "setJobStatusFromString",1);
		digester.addCallParam("*/Status", 0);
		digester.addCallMethod("*/OutputContainerUri", "setOutputContainerUri",1);
		digester.addCallParam("*/OutputContainerUri", 0);
		digester.addCallMethod("*/ImportFileUri", "setImportFileUri",1);
		digester.addCallParam("*/ImportFileUri", 0);
		digester.addCallMethod("*/Failure", "setFailure",1);
		digester.addCallParam("*/Failure", 0);
		digester.addCallMethod("*/CreatedAt", "setCreatedAtFromString",1);
		digester.addCallParam("*/CreatedAt", 0);
		digester.addCallMethod("*/UpdatedAt", "setUpdatedAtFromString",1);
		digester.addCallParam("*/UpdatedAt", 0);		
		digester.addObjectCreate("*/OutputProperties", HashMap.class);
		digester.addCallMethod("*/d3p1:KeyValueOfstringstring", "put",2);
		digester.addCallParam("*/d3p1:Key", 0);
		digester.addCallParam("*/d3p1:Value", 1);
		digester.addSetNext("*/OutputProperties", "setOutputProperties", Map.class.getName());
	}
}
