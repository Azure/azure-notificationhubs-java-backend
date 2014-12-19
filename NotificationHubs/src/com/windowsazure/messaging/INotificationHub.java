package com.windowsazure.messaging;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.http.concurrent.FutureCallback;


/**
 * Interface for the REST wrapper of WIndows Azure Notification Hubs
 */
public interface INotificationHub {
	
	void createOrUpdateInstallation(Installation installation);
	void patchInstallation(String installationId, PartialUpdateOperation... operations);
	void patchInstallation(String installationId, List<PartialUpdateOperation> operations);
	void deleteInstallation(String installationId);
	Installation getInstallation(String installationId);
	
	void scheduleNotification(Notification notification, Date scheduledTime);
	void scheduleNotification(Notification notification, Set<String> tags, Date sheduledTime);
	void scheduleNotification(Notification notification, String tagExpression, Date sheduledTime);
	
	NotificationHubJob submitNotificationHubJob(NotificationHubJob job);
	NotificationHubJob getNotificationHubJob(String jobId);
	List<NotificationHubJob> getAllNotificationHubJobs();
	
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
		
	void scheduleNotificationAsync(Notification notification, String tagExpression, Date sheduledTime, FutureCallback<Object> callback);
	void sendNotificationAsync(Notification notification, FutureCallback<Object> callback);
	void sendNotificationAsync(Notification notification, Set<String> tags,	FutureCallback<Object> callback);
	void sendNotificationAsync(Notification notification, String tagExpression,	FutureCallback<Object> callback);
	void scheduleNotificationAsync(Notification notification, Date scheduledTime, FutureCallback<Object> callback);
	void scheduleNotificationAsync(Notification notification, Set<String> tags,	Date sheduledTime, FutureCallback<Object> callback);	
	void sendNotification(Notification notification, String tagExpression);
		
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
	String createRegistrationId();
	
	/**
	 * This method creates a new registration
	 * @param registration A registration object containing the description of the registration to create.
	 * ETag and registrationid are ignored
	 * 
	 * @return the created registration containing the read-only parameters (registrationid, etag, and expiration time).
	 */
	Registration createRegistration(Registration registration);
	
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
	Registration updateRegistration(Registration registration);
	
	/**
	 * This method updates or creates a new regiostration with the registration id specified.
	 * 
	 * @param registration A registration object containing the description of the registration to create or update.
	 * The registration id has to be populated.
	 * 
	 * @return the updated registration containing the read-only parameters (registrationid, etag, and expiration time).
	 */
	Registration upsertRegistration(Registration registration);
	
	/**
	 * Deletes a registration.
	 * 
	 * @param registration. Registration id has to be populated.
	 */
	void deleteRegistration(Registration registration);
	
	/**
	 * Deletes a registration.
	 * 
	 * @param registrationId
	 */
	void deleteRegistration(String registrationId);
	
	/**
	 * Retrieves the description of a registration based on the id.
	 * 
	 * @param registrationId
	 * @return A registration object
	 */
	Registration getRegistration(String registrationId);
	
	/**
	 * Return all registrations in this hub
	 * 
	 * @return Registration collection.
	 */
	CollectionResult getRegistrations();
	
	/**
	 * Returns all registrations in this hub
	 * 
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * 
	 * @return Registration collection.
	 */
	CollectionResult getRegistrations(int top, String continuationToken);
	
	/**
	 * Returns all registrations with a specific tag
	 * 
	 * @param tag
	 * 
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByTag(String tag);
	
	/**
	 * Returns all registrations with a specific tag
	 * 
	 * @param tag
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * 
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByTag(String tag, int top, String continuationToken);
	
	/**
	 * Returns all registration with a specific channel (e.g. ChannelURI, device token)
	 * @param channel
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByChannel(String channel);
	
	/**
	 * Returns all registration with a specific channel (e.g. ChannelURI, device token)
	 * @param channel
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * @return Registration Collection
	 */
	CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken);
	
	/**
	 * Sends a notification to all eligible registrations (i.e. only correct platform, if notification is platform specific)
	 * 
	 * @param notification
	 */
	void sendNotification(Notification notification);
	
	/**
	 * Sends a notifications to all eligible registrations with at least one of the specified tags
	 * 
	 * @param notification
	 * @param tags
	 */
	void sendNotification(Notification notification, Set<String> tags);
	
	/**
	 * Sends a notifications to all eligible registrations that satisfy the provided tag expression
	 * 
	 * @param notification
	 * @param tagExpression
	 */	
}
