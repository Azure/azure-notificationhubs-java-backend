//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * This class represents an Azure Notification Hubs job.
 */
public class NotificationHubJob {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/atom+xml;type=entry;charset=utf-8\"><NotificationHubJob xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String XML_FOOTER = "</NotificationHubJob></content></entry>";

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
        singleEntryParser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            setupSingleEntryParser(instance);
            return instance;
        });

        collectionParser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            setupCollectionParser(instance);
            return instance;
        });
    }

    /**
     * Gets the Azure Notification Hubs job ID.
     * @return The Azure Notification Hubs job ID.
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the Azure Notification Hubs job ID.
     * @param value The Azure Notification Hubs job ID to set.
     */
    public void setJobId(String value) {
        jobId = value;
    }

    /**
     * Gets the Azure Notification Hubs job progress.
     * @return The Azure Notification Hubs job progress.
     */
    public double getProgress() { return progress; }

    /**
     * Sets the Azure Notification Hubs job progress.
     * @param value The Azure Notification Hubs job progress value to set.
     */
    public void setProgress(double value) { progress = value; }

    /**
     * Sets the Azure Notification Hubs job progress fom a string value.
     * @param value The Azure Notification Hubs job progress value to set from a string.
     */
    public void setProgressFromString(String value) { progress = Double.parseDouble(value); }

    /**
     * Gets the Azure Notification Hubs job type.
     * @return The Azure Notification Hubs job type.
     */
    public NotificationHubJobType getJobType() {
        return jobType;
    }

    /**
     * Sets the Azure Notification Hubs job type.
     * @param value The Azure Notification Hubs job type value to set.
     */
    public void setJobType(NotificationHubJobType value) {
        jobType = value;
    }

    /**
     * Sets the Azure Notification Hubs job type from a string value.
     * @param value The Azure Notification Hubs job type string value to set.
     */
    public void setJobTypeFromString(String value) { jobType = Enum.valueOf(NotificationHubJobType.class, value); }

    /**
     * Gets the Azure Notification Hubs job status.
     * @return The Azure Notification Hubs job status.
     */
    public NotificationHubJobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * Sets the Azure Notification Hubs job status.
     * @param value The Azure Notification Hubs job status value to set.
     */
    public void setJobStatus(NotificationHubJobStatus value) { jobStatus = value; }

    /**
     * Sets the Azure Notification Hubs job status from a string value.
     * @param value The Azure Notification Hubs job status string value to set.
     */
    public void setJobStatusFromString(String value) { jobStatus = Enum.valueOf(NotificationHubJobStatus.class, value); }

    /**
     * Gets the Azure Notification Hubs job output container URI.
     * @return The Azure Notification Hubs job output container URI.
     */
    public String getOutputContainerUri() { return outputContainerUri; }

    /**
     * Sets the Azure Notification Hubs job output container URI.
     * @param value The Azure Notification Hubs job output container URI value to set.
     */
    public void setOutputContainerUri(String value) { outputContainerUri = value; }

    /**
     * Gets the Azure Notification Hubs job file import URI.
     * @return The Azure Notification Hubs job file import URI.
     */
    public String getImportFileUri() { return importFileUri; }

    /**
     * Sets the Azure Notification Hubs job file import URI.
     * @param value The Azure Notification Hubs job file import URI value to set.
     */
    public void setImportFileUri(String value) { importFileUri = value; }

    /**
     * Gets the Azure Notification Hubs job failure message.
     * @return The Azure Notification Hubs job failure message.
     */
    public String getFailure() { return failure; }

    /**
     * Sets the Azure Notification Hubs job failure message.
     * @param value The Azure Notification Hubs job failure message value to set.
     */
    public void setFailure(String value) { failure = value; }

    /**
     * Gets the Azure Notification Hubs job output properties.
     * @return The Azure Notification Hubs job output properties.
     */
    public Map<String, String> getOutputProperties() {
        return outputProperties;
    }

    /**
     * Sets the Azure Notification Hubs job output properties.
     * @param value The Azure Notification Hubs job output properties value to set.
     */
    public void setOutputProperties(Map<String, String> value) { outputProperties = value; }

    /**
     * Gets the Azure Notification Hubs job created time.
     * @return The Azure Notification Hubs job created time.
     */
    public Date getCreatedAt() { return createdAt; }

    /**
     * Sets the Azure Notification Hubs job created time.
     * @param value The Azure Notification Hubs job created time value to set.
     */
    public void setCreatedAt(Date value) { createdAt = value; }

    /**
     * Sets the Azure Notification Hubs job created time from a string value.
     * @param value The Azure Notification Hubs job created time string value to set.
     */
    public void setCreatedAtFromString(String value) { createdAt = javax.xml.bind.DatatypeConverter.parseDateTime(value).getTime(); }

    /**
     * Gets the Azure Notification Hubs job updated time.
     * @return The Azure Notification Hubs job updated time.
     */
    public Date getUpdatedAt() { return updatedAt; }

    /**
     * Sets the Azure Notification Hubs job updated time.
     * @param value The Azure Notification Hubs job updated time value to set.
     */
    public void setUpdatedAt(Date value) { updatedAt = value; }

    /**
     * Sets the Azure Notification Hubs job updated time from a string value.
     * @param value The Azure Notification Hubs job updated time string value to set.
     */
    public void setUpdatedAtFromString(String value) { updatedAt = javax.xml.bind.DatatypeConverter.parseDateTime(value).getTime(); }

    public static NotificationHubJob parseOne(InputStream inputStream) throws IOException, SAXException {
        return singleEntryParser.get().parse(inputStream);
    }

    public static NotificationHubJob parseOne(byte[] bodyBytes) throws IOException, SAXException {
        return singleEntryParser.get().parse(new ByteArrayInputStream(bodyBytes));
    }

    public static List<NotificationHubJob> parseCollection(InputStream inputStream) throws IOException, SAXException {
        return collectionParser.get().parse(inputStream);
    }

    public static List<NotificationHubJob> parseCollection(byte[] bodyBytes) throws IOException, SAXException {
        return collectionParser.get().parse(new ByteArrayInputStream(bodyBytes));
    }

    public String getXml() {
        StringBuilder buf = new StringBuilder();
        buf.append(XML_HEADER);
        if (this.jobType != null) {
            buf.append("<Type>").append(this.jobType.name()).append("</Type>");
        }
        if (this.outputContainerUri != null) {
            buf.append("<OutputContainerUri><![CDATA[").append(this.outputContainerUri).append("]]></OutputContainerUri>");
        }
        if (this.importFileUri != null) {
            buf.append("<ImportFileUri>").append(this.importFileUri).append("</ImportFileUri>");
        }
        buf.append(XML_FOOTER);
        return buf.toString();
    }

    private static void setupCollectionParser(Digester digester) {
        digester.addObjectCreate("*/feed", LinkedList.class);
        setupSingleEntryParser(digester);
        digester.addSetNext("*/entry", "add", NotificationHubJob.class.getName());
    }

    private static void setupSingleEntryParser(Digester digester) {
        digester.addObjectCreate("*/entry", NotificationHubJob.class);
        digester.addCallMethod("*/JobId", "setJobId", 1);
        digester.addCallParam("*/JobId", 0);
        digester.addCallMethod("*/Progress", "setProgressFromString", 1);
        digester.addCallParam("*/Progress", 0);
        digester.addCallMethod("*/Type", "setJobTypeFromString", 1);
        digester.addCallParam("*/Type", 0);
        digester.addCallMethod("*/Status", "setJobStatusFromString", 1);
        digester.addCallParam("*/Status", 0);
        digester.addCallMethod("*/OutputContainerUri", "setOutputContainerUri", 1);
        digester.addCallParam("*/OutputContainerUri", 0);
        digester.addCallMethod("*/ImportFileUri", "setImportFileUri", 1);
        digester.addCallParam("*/ImportFileUri", 0);
        digester.addCallMethod("*/Failure", "setFailure", 1);
        digester.addCallParam("*/Failure", 0);
        digester.addCallMethod("*/CreatedAt", "setCreatedAtFromString", 1);
        digester.addCallParam("*/CreatedAt", 0);
        digester.addCallMethod("*/UpdatedAt", "setUpdatedAtFromString", 1);
        digester.addCallParam("*/UpdatedAt", 0);
        digester.addObjectCreate("*/OutputProperties", HashMap.class);
        digester.addCallMethod("*/d3p1:KeyValueOfstringstring", "put", 2);
        digester.addCallParam("*/d3p1:Key", 0);
        digester.addCallParam("*/d3p1:Value", 1);
        digester.addSetNext("*/OutputProperties", "setOutputProperties", Map.class.getName());
    }
}
