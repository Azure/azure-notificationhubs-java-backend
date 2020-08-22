//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * This class represents a notification hub job description.
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

    private static final AtomicReference<Digester> singleEntryParser = new AtomicReference<>();
    private static final AtomicReference<Digester> collectionParser = new AtomicReference<>();

    static {
        singleEntryParser.set(new Digester());
        setupSingleEntryParser(singleEntryParser.get());

        collectionParser.set(new Digester());
        setupCollectionParser(collectionParser.get());
    }

    /**
     * Gets the notification hub job ID.
     *
     * @return The notification hub job ID.
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the notification hub job ID.
     *
     * @param jobId THe notification hub job ID.
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the progress percentage for the job.
     *
     * @return The progress percentage for the job.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Sets the progress percentage for the job as a string.
     *
     * @param progress The progress to be parsed as a double.
     */
    public void setProgressFromString(String progress) {
        this.progress = Double.parseDouble(progress);
    }

    /**
     * Gets the notification hub job type.
     *
     * @return The notification hub job type.
     */
    public NotificationHubJobType getJobType() {
        return jobType;
    }

    /**
     * Sets the notification hub job type.
     *
     * @param jobType The notification hub job type.
     */
    public void setJobType(NotificationHubJobType jobType) {
        this.jobType = jobType;
    }

    /**
     * Sets the notification hub job type as a string.
     *
     * @param jobType The job type to be parsed as a notification hub job type.
     */
    public void setJobTypeFromString(String jobType) {
        this.jobType = Enum.valueOf(NotificationHubJobType.class, jobType);
    }

    /**
     * Gets the notification hub job status.
     *
     * @return The notification hub job status.
     */
    public NotificationHubJobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * Sets the notification hub job status from a string.
     *
     * @param jobStatus The string to be parsed as a notification hub job status.
     */
    public void setJobStatusFromString(String jobStatus) {
        this.jobStatus = Enum.valueOf(NotificationHubJobStatus.class, jobStatus);
    }

    /**
     * Gets the notification hub job output container URI
     *
     * @return The notification hub job output container URI.
     */
    public String getOutputContainerUri() {
        return outputContainerUri;
    }

    /**
     * Sets the notification hub job output container URI.
     *
     * @param outputContainerUri The notification hub job output container URI.
     */
    public void setOutputContainerUri(String outputContainerUri) {
        this.outputContainerUri = outputContainerUri;
    }

    /**
     * Gets the import file URI
     *
     * @return The import file URI.
     */
    public String getImportFileUri() {
        return importFileUri;
    }

    /**
     * Sets the import file URI.
     *
     * @param importFileUri The import file URI.
     */
    public void setImportFileUri(String importFileUri) {
        this.importFileUri = importFileUri;
    }

    /**
     * Gets the notification hub job failure message.
     *
     * @return The notification hub job failure message.
     */
    public String getFailure() {
        return failure;
    }

    /**
     * Sets the notification hub job failure message.
     *
     * @param failure The notification hub job failure message.
     */
    public void setFailure(String failure) {
        this.failure = failure;
    }

    /**
     * Gets the output properties from the notification hub job.
     *
     * @return The output properties from the notification hub job.
     */
    public Map<String, String> getOutputProperties() {
        return outputProperties;
    }

    /**
     * Sets the output properties for the notification hub job.
     *
     * @param outputProperties The output properties for the notification hub job.
     */
    public void setOutputProperties(Map<String, String> outputProperties) {
        this.outputProperties = outputProperties;
    }

    /**
     * Gets the notification hub job created at time.
     *
     * @return The notification hub job created at time.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at time for the notification hub job to be parsed as a date.
     *
     * @param createdAt The created at time for the notification hub job to be
     *                  parsed as a date.
     */
    public void setCreatedAtFromString(String createdAt) {
        this.createdAt = javax.xml.bind.DatatypeConverter.parseDateTime(createdAt).getTime();
    }

    /**
     * Gets the updated at time for the notification hub job.
     *
     * @return The updated at time for the notification hub job.
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the updated at time for the notification hub job as a string parsed to
     * be a date.
     *
     * @param updatedAt The string for updated at to be parsed as a date.
     */
    public void setUpdatedAtFromString(String updatedAt) {
        this.updatedAt = javax.xml.bind.DatatypeConverter.parseDateTime(updatedAt).getTime();
    }

    /**
     * Creates a notification hub job from the incoming stream content.
     *
     * @param content The stream content containing the notification hub job.
     * @return A parsed notification hub job from the incoming stream.
     * @throws IOException  If there is an exception reading from the stream.
     * @throws SAXException If there is an exception parsing the XML.
     */
    public static NotificationHubJob parseOne(InputStream content) throws IOException, SAXException {
        return singleEntryParser.get().parse(content);
    }

    /**
     * Creates a list of notification hub jobs from the incoming stream content.
     *
     * @param content The stream content containing the notification hub jobs.
     * @return A list of parsed notification hub jobs.
     * @throws IOException  If there is an exception reading from the stream.
     * @throws SAXException If there is an exception parsing the XML.
     */
    public static List<NotificationHubJob> parseCollection(InputStream content) throws IOException, SAXException {
        return collectionParser.get().parse(content);
    }

    /**
     * Gets the XML from the notification hub job.
     *
     * @return An XML representation of the notification hub job.
     */
    public String getXml() {
        StringBuilder buf = new StringBuilder();
        buf.append(XML_HEADER);
        if (this.jobType != null)
            buf.append("<Type>" + this.jobType.name() + "</Type>");
        if (this.outputContainerUri != null)
            buf.append("<OutputContainerUri>" + this.outputContainerUri + "</OutputContainerUri>");
        if (this.importFileUri != null)
            buf.append("<ImportFileUri>" + this.importFileUri + "</ImportFileUri>");
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
