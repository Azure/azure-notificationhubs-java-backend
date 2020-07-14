//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Date;
import java.util.List;
import java.util.Set;

import reactor.core.publisher.Mono;


/**
 * Interface for the REST wrapper of WIndows Azure Notification Hubs
 */
public interface INotificationHub {
	
	void createOrUpdateInstallation(Installation installation) throws NotificationHubsException;
	void patchInstallation(String installationId, PartialUpdateOperation... operations) throws NotificationHubsException;
	void patchInstallation(String installationId, List<PartialUpdateOperation> operations) throws NotificationHubsException;
	void deleteInstallation(String installationId) throws NotificationHubsException;
	Installation getInstallation(String installationId) throws NotificationHubsException;
	
	NotificationOutcome scheduleNotification(Notification notification, Date scheduledTime) throws NotificationHubsException;
	NotificationOutcome scheduleNotification(Notification notification, Set<String> tags, Date scheduledTime) throws NotificationHubsException;
	NotificationOutcome scheduleNotification(Notification notification, String tagExpression, Date scheduledTime) throws NotificationHubsException;
	NotificationOutcome sendDirectNotification(Notification notification, String deviceHandle) throws NotificationHubsException;
	NotificationOutcome sendDirectNotification(Notification notification, List<String> deviceHandles) throws NotificationHubsException;	
	void cancelScheduledNotification(String notificationId) throws NotificationHubsException;
	
	NotificationHubJob submitNotificationHubJob(NotificationHubJob job) throws NotificationHubsException;
	NotificationHubJob getNotificationHubJob(String jobId) throws NotificationHubsException;
	List<NotificationHubJob> getAllNotificationHubJobs() throws NotificationHubsException;
	
	NotificationTelemetry getNotificationTelemetry(String notificationId) throws NotificationHubsException;
	
	Mono<String> createRegistrationIdAsync();
	Mono<Registration> createRegistrationAsync(Registration registration);
	Mono<Registration> updateRegistrationAsync(Registration registration);
	Mono<Registration> upsertRegistrationAsync(Registration registration);
	Mono<Void> deleteRegistrationAsync(Registration registration);
	Mono<Void> deleteRegistrationAsync(String registrationId);
	Mono<Registration> getRegistrationAsync(String registrationId);
	Mono<CollectionResult> getRegistrationsByChannelAsync(String channel, int top, String continuationToken);
	Mono<CollectionResult> getRegistrationsByTagAsync(String tag, int top, String continuationToken);
	Mono<CollectionResult> getRegistrationsByTagAsync(String tag);
	Mono<CollectionResult> getRegistrationsByChannelAsync(String channel);
	Mono<CollectionResult> getRegistrationsAsync(int top, String continuationToken);	

	Mono<Void> sendNotificationAsync(Notification notification);
	Mono<Void> sendNotificationAsync(Notification notification, Set<String> tags);
	Mono<Void> sendNotificationAsync(Notification notification, String tagExpression);
	Mono<Void> sendDirectNotificationAsync(Notification notification, String deviceHandle);
	Mono<Void> sendDirectNotificationAsync(Notification notification, List<String> deviceHandles);	
	Mono<Void> scheduleNotificationAsync(Notification notification, Date scheduledTime);
	Mono<Void> scheduleNotificationAsync(Notification notification, Set<String> tags,	Date scheduledTime);	
	Mono<Void> scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime);
	Mono<Void> cancelScheduledNotificationAsync(String notificationId);
			
	Mono<Void> createOrUpdateInstallationAsync(Installation installation);
	Mono<Void> patchInstallationAsync(String installationId,	List<PartialUpdateOperation> operations);
	Mono<Void> patchInstallationAsync(String installationId, PartialUpdateOperation... operations);
	Mono<Void> deleteInstallationAsync(String installationId);
	Mono<Installation> getInstallationAsync(String installationId);
		
	Mono<NotificationHubJob> submitNotificationHubJobAsync(NotificationHubJob job);
	Mono<NotificationHubJob> getNotificationHubJobAsync(String jobId);
	Mono<List<NotificationHubJob>> getAllNotificationHubJobsAsync();	
	
	Mono<NotificationTelemetry> getNotificationTelemetryAsync(String notificationId);
	
	
	/**
	 * Create a registrationId, without creating an actual registration. To create use upsert.
	 * This method is used when the registration id is stored only on the device.
	 *  
	 * @return a registration id.
	 */
	String createRegistrationId() throws NotificationHubsException;
	
	/**
	 * This method creates a new registration
	 * @param registration A registration object containing the description of the registration to create.
	 * ETag and registrationid are ignored
	 * 
	 * @return the created registration containing the read-only parameters (registrationid, etag, and expiration time).
	 */
	Registration createRegistration(Registration registration) throws NotificationHubsException;
	
	/**
	 * This methods updates an existing registration
	 * 
	 * @param registration A registration object containing the description of the registration to update.
	 * The registration id has to be populated.
	 * 
	 * @throws NotificationHubsException 
	 * runtime exception if the registration already exists
	 * 
	 * @return the updated registration containing the read-only parameters (registrationid, etag, and expiration time).
	 */
	Registration updateRegistration(Registration registration) throws NotificationHubsException;
	
	/**
	 * This method updates or creates a new regiostration with the registration id specified.
	 * 
	 * @param registration A registration object containing the description of the registration to create or update.
	 * The registration id has to be populated.
	 * 
	 * @return the updated registration containing the read-only parameters (registrationid, etag, and expiration time).
	 */
	Registration upsertRegistration(Registration registration) throws NotificationHubsException;
	
	/**
	 * Deletes a registration.
	 * 
	 * @param registration. Registration id has to be populated.
	 */
	void deleteRegistration(Registration registration) throws NotificationHubsException;
	
	/**
	 * Deletes a registration.
	 * 
	 * @param registrationId
	 */
	void deleteRegistration(String registrationId) throws NotificationHubsException;
	
	/**
	 * Retrieves the description of a registration based on the id.
	 * 
	 * @param registrationId
	 * @return A registration object
	 */
	Registration getRegistration(String registrationId) throws NotificationHubsException;
	
	/**
	 * Return all registrations in this hub
	 * 
	 * @return Registration collection.
	 */
	CollectionResult getRegistrations() throws NotificationHubsException;
	
	/**
	 * Returns all registrations in this hub
	 * 
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * 
	 * @return Registration collection.
	 */
	CollectionResult getRegistrations(int top, String continuationToken) throws NotificationHubsException;
	
	/**
	 * Returns all registrations with a specific tag
	 * 
	 * @param tag
	 * 
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByTag(String tag) throws NotificationHubsException;
	
	/**
	 * Returns all registrations with a specific tag
	 * 
	 * @param tag
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * 
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByTag(String tag, int top, String continuationToken) throws NotificationHubsException;
	
	/**
	 * Returns all registration with a specific channel (e.g. ChannelURI, device token)
	 * @param channel
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByChannel(String channel) throws NotificationHubsException;
	
	/**
	 * Returns all registration with a specific channel (e.g. ChannelURI, device token)
	 * @param channel
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken) throws NotificationHubsException;
	
	/**
	 * Sends a notification to all eligible registrations (i.e. only correct platform, if notification is platform specific)
	 * 
	 * @param notification
	 */
	NotificationOutcome sendNotification(Notification notification) throws NotificationHubsException;
	
	/**
	 * Sends a notifications to all eligible registrations with at least one of the specified tags
	 * 
	 * @param notification
	 * @param tags
	 */
	NotificationOutcome sendNotification(Notification notification, Set<String> tags) throws NotificationHubsException;
	
	/**
	 * Sends a notifications to all eligible registrations that satisfy the provided tag expression
	 * 
	 * @param notification
	 * @param tagExpression
	 */	
	NotificationOutcome sendNotification(Notification notification, String tagExpression) throws NotificationHubsException;
}
