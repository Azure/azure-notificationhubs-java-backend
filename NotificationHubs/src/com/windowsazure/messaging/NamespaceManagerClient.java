package com.windowsazure.messaging;

import org.apache.http.concurrent.FutureCallback;

import java.util.List;

/**
 * This interface represents the operations that can be performed by the Azure Notification Hub management API.
 */
public interface NamespaceManagerClient {

    /**
     * Gets a notification hub by the hub path asynchronously.
     * @param hubPath The path of the notification hub.
     * @param callback A callback that returns the notification hub description.
     */
    void getNotificationHubAsync(String hubPath, FutureCallback<NotificationHubDescription> callback);

    /**
     * Gets a notification hub by the hub path.
     * @param hubPath The path of the notification hub.
     * @return The notification hub description.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException;

    /**
     * Gets all notification hubs for the namespace.
     * @param callback A callback, when invoked, returns a list of all the namespace's registration descriptions.
     */
    void getNotificationHubsAsync(FutureCallback<List<NotificationHubDescription>> callback);

    /**
     * Gets all notification hubs for the namespace.
     * @return A list of all the namespace's registration descriptions.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException;

    /**
     * Creates a notification hub with the given notification hub description.
     * @param hubDescription The notification hub description containing the information for the notification hub.
     * @param callback A callback, when invoked, returns the populated notification hub description.
     */
    void createNotificationHubAsync(NotificationHubDescription hubDescription, FutureCallback<NotificationHubDescription> callback);

    /**
     * Creates a notification hub with the given notification hub description.
     * @param hubDescription The notification hub description containing the information for the notification hub.
     * @return The populated notification hub description
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException;

    /**
     * Updates a notification hub via the notification hub description.
     * @param hubDescription The notification hub description to update.
     * @param callback A callback, when invoked, returns the populated notification hub description.
     */
    void updateNotificationHubAsync(NotificationHubDescription hubDescription, FutureCallback<NotificationHubDescription> callback);

    /**
     * Updates a notification hub via the notification hub description.
     * @param hubDescription The notification hub description to update.
     * @return Returns the populated notification hub description.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException;

    /**
     * Deletes the notification hub with the given hub name.
     * @param hubPath The name of the notification hub.
     * @param callback A callback, when invoked, returns nothing.
     */
    void deleteNotificationHubAsync(String hubPath, FutureCallback<Object> callback);

    /**
     * Deletes the notification hub with the given hub name.
     * @param hubPath The name of the notification hub.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void deleteNotificationHub(String hubPath) throws NotificationHubsException;
}
