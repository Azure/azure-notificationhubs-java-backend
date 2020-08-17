//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Date;
import java.util.List;
import java.util.Set;

import reactor.core.publisher.Mono;

/**
 * Interface for the REST wrapper of Microsoft Azure Notification Hubs
 */
public interface NotificationHubClient {

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
	Mono<Void> patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations);
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
     * @throws NotificationHubsException if there is a client error creating the registration ID.
	 */
	String createRegistrationId() throws NotificationHubsException;

	/**
	 * This method creates a new registration
	 * @param registration A registration object containing the description of the registration to create.
	 * ETag and registrationId are ignored
	 *
	 * @return the created registration containing the read-only parameters (registrationId, etag, and expiration time).
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
	 * This method updates or creates a new registration with the registration id specified.
	 *
	 * @param registration A registration object containing the description of the registration to create or update.
	 * The registration id has to be populated.
	 *
	 * @return the updated registration containing the read-only parameters (registrationId, etag, and expiration time).
     * @throws NotificationHubsException if there is a client error upserting the registration
	 */
	Registration upsertRegistration(Registration registration) throws NotificationHubsException;

	/**
	 * Deletes a registration.
	 *
	 * @param registration Registration id has to be populated.
     * @throws NotificationHubsException if there is a client exception during the deleting registration.
	 */
	void deleteRegistration(Registration registration) throws NotificationHubsException;

	/**
	 * Deletes a registration.
	 *
	 * @param registrationId The registration ID from the registration
     * @throws NotificationHubsException if there is a client exception during the deleting registration.
	 */
	void deleteRegistration(String registrationId) throws NotificationHubsException;

	/**
	 * Retrieves the description of a registration based on the id.
	 *
	 * @param registrationId The registration ID
	 * @return A registration object
     * @throws NotificationHubsException if there is a client exception during getting the registration.
	 */
	Registration getRegistration(String registrationId) throws NotificationHubsException;

	/**
	 * Return all registrations in this hub
	 *
	 * @return Registration collection.
     * @throws NotificationHubsException if there is a client exception getting the registrations.
	 */
	CollectionResult getRegistrations() throws NotificationHubsException;

	/**
	 * Returns all registrations in this hub
	 *
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 *
	 * @return Registration collection.
     * @throws NotificationHubsException if there is a client exception getting the registrations.
	 */
	CollectionResult getRegistrations(int top, String continuationToken) throws NotificationHubsException;

	/**
	 * Returns all registrations with a specific tag
	 *
	 * @param tag the tag to find the registrations for.
	 *
	 * @return Registration Collection
     * @throws NotificationHubsException if there is a client exception getting the registrations.
	 */
	CollectionResult getRegistrationsByTag(String tag) throws NotificationHubsException;

	/**
	 * Returns all registrations with a specific tag
	 *
	 * @param tag the tag to find the registrations for.
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 *
	 * @return Registration Collection
     * @throws NotificationHubsException if there is a client exception getting the registrations by tag.
	 */
	CollectionResult getRegistrationsByTag(String tag, int top, String continuationToken) throws NotificationHubsException;

	/**
	 * Returns all registration with a specific channel (e.g. ChannelURI, device token)
	 * @param channel The registration channel for the PNS type.
	 * @return Registration Collection
     * @throws NotificationHubsException if there is a client error.
	 */
	CollectionResult getRegistrationsByChannel(String channel) throws NotificationHubsException;

	/**
	 * Returns all registration with a specific channel (e.g. ChannelURI, device token)
	 * @param channel The registration channel for the PNS type.
	 * @param top The maximum number of registrations to return (max 100)
	 * @param continuationToken If not-null, continues iterating through a previously requested query.
	 * @return Registration Collection
     * @throws NotificationHubsException if there is a client exception getting the registrations by channel
	 */
	CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken) throws NotificationHubsException;

	/**
	 * Sends a notification to all eligible registrations (i.e. only correct platform, if notification is platform specific)
	 *
	 * @param notification The notification to be sent
     * @return The notification outcome
     * @throws NotificationHubsException if there is a client exception send a notification
	 */
	NotificationOutcome sendNotification(Notification notification) throws NotificationHubsException;

	/**
	 * Sends a notifications to all eligible registrations with at least one of the specified tags
	 *
	 * @param notification The notification to send
	 * @param tags the tags used for targeting the notification
     * @return The notification outcome
     * @throws NotificationHubsException if there is a client exception sending the notification
	 */
	NotificationOutcome sendNotification(Notification notification, Set<String> tags) throws NotificationHubsException;

	/**
	 * Sends a notifications to all eligible registrations that satisfy the provided tag expression
	 *
	 * @param notification The notification to send
	 * @param tagExpression The tag expression used for targeting
     * @return The notification outcome
     * @throws NotificationHubsException if there is a client exception sending a notification.
	 */
	NotificationOutcome sendNotification(Notification notification, String tagExpression) throws NotificationHubsException;
}
