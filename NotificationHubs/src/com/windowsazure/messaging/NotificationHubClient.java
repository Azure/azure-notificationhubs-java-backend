//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Date;
import java.util.List;
import java.util.Set;

import reactor.core.publisher.Mono;

/**
 * Interface for all operations for Microsoft Azure Notification Hubs
 */
public interface NotificationHubClient {

     /**
     * Creates or updates an installation.
     * @param installation The installation to create or update.
     * @return A Mono Void for the asynchronous operation.
     */
    Mono<Void> createOrUpdateInstallationAsync(Installation installation);

    /**
     * Patches an installation.
     * @param installationId The installation ID.
     * @param operations The patch operations to perform on the installation.
     * @return a Mono Void representing the asynchronous operation.
     */
    Mono<Void> patchInstallationAsync(String installationId, PartialUpdateOperation... operations);

    /**
     * Patches an installation.
     * @param installationId The installation ID.
     * @param operations A list of patch operations to perform on the installation.
     * @return a Mono Void representing the asynchronous operation.
     */
    Mono<Void> patchInstallationAsync(String installationId, List<PartialUpdateOperation> operations);

    /**
     * Deletes an installation by the installation ID.
     * @param installationId The installation ID.
     * @return a Mono Void representing the asynchronous operation.
     */
    Mono<Void> deleteInstallationAsync(String installationId);

    /**
     * Gets an installation by the installation ID.
     * @param installationId The installation ID for the installation to get.
     * @return The installation that matches the installation ID.
     */
    Mono<Installation> getInstallationAsync(String installationId);

    /**
     * Schedules a notification at the scheduled time.
     * @param notification The notification to send at the scheduled time.
     * @param scheduledTime The time to send the notification.
     * @return The notification outcome for the scheduled notification.
     */
    Mono<NotificationOutcome> scheduleNotificationAsync(Notification notification, Date scheduledTime);

    /**
     * Schedules a notification with a list of tags at the scheduled time.
     * @param notification The notification to send at the scheduled time.
     * @param tags The tags for the notification.
     * @param scheduledTime The scheduled time for the notification.
     * @return The notification outcome for the scheduled operation.
     */
    Mono<NotificationOutcome> scheduleNotificationAsync(Notification notification, Set<String> tags, Date scheduledTime);

    /**
     * Schedules a notification with a tag expression at the scheduled time.
     * @param notification The notification to send at the scheduled time.
     * @param tagExpression The tag expression for the notification.
     * @param scheduledTime The scheduled time for the notification.
     * @return The notification outcome for the scheduled operation.
     */
    Mono<NotificationOutcome> scheduleNotificationAsync(Notification notification, String tagExpression, Date scheduledTime);

    /**
     * Sends a direction notification with the given device handle.
     * @param notification The notification to send to the device.
     * @param deviceHandle The device handle to send the notification.
     * @return The notification outcome for the direct send operation.
     */
    Mono<NotificationOutcome> sendDirectNotificationAsync(Notification notification, String deviceHandle);

    /**
     * Sends a direct notification to the given device handles.
     * @param notification The notification to send to the devices.
     * @param deviceHandles The device handles to send the notification.
     * @return The notification outcome for the direct send operation.
     */
    Mono<NotificationOutcome> sendDirectNotificationAsync(Notification notification, List<String> deviceHandles);

    /**
     * Cancels a scheduled notification.
     * @param notificationId The notification ID for the notification to cancel.
     * @return A Mono Void representing the asynchronous operation.
     */
    Mono<Void> cancelScheduledNotificationAsync(String notificationId);

    /**
     * Submits a notification hub job such as import or export.
     * @param job The job to submit for either import or export.
     * @return The created notification hub job.
     */
    Mono<NotificationHubJob> submitNotificationHubJobAsync(NotificationHubJob job);

    /**
     * Gets a notification job by the job ID.
     * @param jobId The ID of the notification job.
     * @return The notification job with the given job ID.
     */
    Mono<NotificationHubJob> getNotificationHubJobAsync(String jobId);

    /**
     * Gets all notification hub jobs for the notification hub.
     * @return All notification hub jobs for the notification hub.
     */
    Mono<List<NotificationHubJob>> getAllNotificationHubJobsAsync();

    /**
     * Gets the notification telemetry by the notification ID.
     * @param notificationId The notification ID of the notification telemetry.
     * @return The telemetry for the given notification ID.
     */
    Mono<NotificationTelemetry> getNotificationTelemetryAsync(String notificationId);

    /**
     * Create a registrationId, without creating an actual registration. To create use upsert.
     * This method is used when the registration id is stored only on the device.
     *
     * @return The registration ID wrapped as a Reactor Mono.
     */
	Mono<String> createRegistrationIdAsync();

    /**
     * Creates a registration asynchronously with the given registration information.
     * @param registration A registration object containing the description of the registration to create.
     * ETag and registrationId are ignored
     * @return the created registration containing the read-only parameters (registrationId, ETag, and expiration time).
     */
	Mono<Registration> createRegistrationAsync(Registration registration);

    /**
     * This methods updates an existing registration
     *
     * @param registration A registration object containing the description of the registration to update.
     * The registration id has to be populated.
     *
     * @return The updated registration containing the read-only parameters (registration ID, ETag, and expiration time).
     */
	Mono<Registration> updateRegistrationAsync(Registration registration);

    /**
     * This method updates or creates a new registration with the registration ID specified.
     *
     * @param registration A registration object containing the description of the registration to create or update.
     * The registration id has to be populated.
     *
     * @return the updated registration containing the read-only parameters (registrationId, ETag, and expiration time).
     */
	Mono<Registration> upsertRegistrationAsync(Registration registration);

    /**
     * Deletes a registration asynchronously with the given registration information.
     * @param registration The registration information used to delete the registration including registrationId.
     * @return A Mono Void for the asynchronous operation.
     */
	Mono<Void> deleteRegistrationAsync(Registration registration);

    /**
     * Deletes a registration asynchronously with the given registration ID.
     * @param registrationId The registration ID for the registration to delete.
     * @return A Mono Void for the asynchronous operation.
     */
	Mono<Void> deleteRegistrationAsync(String registrationId);

    /**
     * Gets a registration by the given registration ID asynchronously.
     *
     * @param registrationId The registration ID of the registration to get.
     * @return The matching registration to the given registration ID.
     */
	Mono<Registration> getRegistrationAsync(String registrationId);

    /**
     /**
     * Return all registrations in this notification hub.
     *
     * @return Registration collection for the hub.
     */
    Mono<CollectionResult> getRegistrationsAsync();

    /**
     /**
     * Returns all registrations in this hub
     *
     * @param top The maximum number of registrations to return (max 100)
     * @param continuationToken If not-null, continues iterating through a previously requested query.
     *
     * @return Registration collection.
     */
    Mono<CollectionResult> getRegistrationsAsync(int top, String continuationToken);

    /**
     /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device token)
     * @param channel The registration channel for the PNS type.
     * @return Registration Collection.
     */
    Mono<CollectionResult> getRegistrationsByChannelAsync(String channel);

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device token)
     * @param channel The registration channel for the PNS type.
     * @param top The maximum number of registrations to return (max 100)
     * @param continuationToken If not-null, continues iterating through a previously requested query.
     * @return Registration Collection
     */
	Mono<CollectionResult> getRegistrationsByChannelAsync(String channel, int top, String continuationToken);

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag the tag to find the registrations for.
     *
     * @return Registration Collection
     */
    Mono<CollectionResult> getRegistrationsByTagAsync(String tag);

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag the tag to find the registrations for.
     * @param top The maximum number of registrations to return (max 100)
     * @param continuationToken If not-null, continues iterating through a previously requested query.
     *
     * @return Registration Collection
     */
	Mono<CollectionResult> getRegistrationsByTagAsync(String tag, int top, String continuationToken);

    /**
     * Sends a notification to all eligible registrations (i.e. only correct platform, if notification is platform specific)
     *
     * @param notification The notification to be sent
     * @return The notification outcome
     */
    Mono<NotificationOutcome> sendNotificationAsync(Notification notification);

    /**
     * Sends a notifications to all eligible registrations with at least one of the specified tags
     *
     * @param notification The notification to send
     * @param tags the tags used for targeting the notification
     * @return The notification outcome
     */
    Mono<NotificationOutcome> sendNotificationAsync(Notification notification, Set<String> tags);

    /**
     * Sends a notifications to all eligible registrations that satisfy the provided tag expression
     *
     * @param notification The notification to send
     * @param tagExpression The tag expression used for targeting
     * @return The notification outcome
     */
    Mono<NotificationOutcome> sendNotificationAsync(Notification notification, String tagExpression);
}
