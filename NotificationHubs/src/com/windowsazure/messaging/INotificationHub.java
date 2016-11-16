package com.windowsazure.messaging;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.http.concurrent.FutureCallback;


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

	
	NotificationHubJob submitNotificationHubJob(NotificationHubJob job) throws NotificationHubsException;
	NotificationHubJob getNotificationHubJob(String jobId) throws NotificationHubsException;
	List<NotificationHubJob> getAllNotificationHubJobs() throws NotificationHubsException;
	
	void createRegistrationIdAsync(FutureCallback<String> callback);
	void createRegistrationAsync(Registration registration, FutureCallback<Registration> callback);
	void updateRegistrationAsync(Registration registration, FutureCallback<Registration> callback);
	void upsertRegistrationAsync(Registration registration, FutureCallback<Registration> callback);
	void deleteRegistrationAsync(Registration registration, FutureCallback<Object> callback);
	void deleteRegistrationAsync(String registrationId, FutureCallback<Object> callback);
	void getRegistrationAsync(String registrationId, FutureCallback<Registration> callback);
	void getRegistrationsByChannelAsync(String channel, int top, String continuationToken, FutureCallback<CollectionResult> callback);
	void getRegistrationsByTagAsync(String tag, int top, String continuationToken, FutureCallback<CollectionResult> callback);
	void getRegistrationsByTagAsync(String tag,	FutureCallback<CollectionResult> callback);
	void getRegistrationsByChannelAsync(String channel,	FutureCallback<CollectionResult> callback);
	void getRegistrationsAsync(int top, String continuationToken, FutureCallback<CollectionResult> callback);	

	void scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime, FutureCallback<NotificationOutcome> callback);
	void sendNotificationAsync(Notification notification, FutureCallback<NotificationOutcome> callback);
	void sendNotificationAsync(Notification notification, Set<String> tags,	FutureCallback<NotificationOutcome> callback);
	void sendNotificationAsync(Notification notification, String tagExpression,	FutureCallback<NotificationOutcome> callback);
	void scheduleNotificationAsync(Notification notification, Date scheduledTime, FutureCallback<NotificationOutcome> callback);
	void scheduleNotificationAsync(Notification notification, Set<String> tags,	Date scheduledTime, FutureCallback<NotificationOutcome> callback);	
	NotificationOutcome sendNotification(Notification notification, String tagExpression) throws NotificationHubsException;

		
	void createOrUpdateInstallationAsync(Installation installation,	FutureCallback<Object> callback);
	void patchInstallationAsync(String installationId,	List<PartialUpdateOperation> operations, FutureCallback<Object> callback);
	void patchInstallationAsync(String installationId,	FutureCallback<Object> callback, PartialUpdateOperation... operations);
	void deleteInstallationAsync(String installationId,	FutureCallback<Object> callback);
	void getInstallationAsync(String installationId, FutureCallback<Installation> callback);
		
	void submitNotificationHubJobAsync(NotificationHubJob job,	FutureCallback<NotificationHubJob> callback);
	void getNotificationHubJobAsync(String jobId, FutureCallback<NotificationHubJob> callback);
	void getAllNotificationHubJobsAsync(FutureCallback<List<NotificationHubJob>> callback);	
	
	
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
	 * @throws Runtime exception if the registration already exists
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
}
