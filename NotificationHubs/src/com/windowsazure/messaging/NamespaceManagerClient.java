package com.windowsazure.messaging;

import reactor.core.publisher.Mono;
import java.util.List;

public interface NamespaceManagerClient {

    /**
     * Gets a Notification Hub by the hub name asynchronously.
     * @param hubPath The hub name
     * @return The notification hub retrieved by name as a Reactor Mono
     */
    Mono<NotificationHubDescription> getNotificationHubAsync(String hubPath);

    /**
     * Gets all notification hubs per namespace asynchronously.
     * @return A list of notification hubs for the namespace as a Reactor Mono.
     */
    Mono<List<NotificationHubDescription>> getNotificationHubsAsync();

    /**
     * Creates a notification hub asynchronously.
     * @param hubDescription The notification hub description.
     * @return The created Notification Hub description as a Reactor Mono.
     */
    Mono<NotificationHubDescription> createNotificationHubAsync(NotificationHubDescription hubDescription);

    /**
     * Updates a notification hub asynchronously.
     * @param hubDescription The notification hub description.
     * @return The updated Notification Hub description via a Reactor Mono.
     */
    Mono<NotificationHubDescription> updateNotificationHubAsync(NotificationHubDescription hubDescription);

    /**
     * Deletes a notification hub with the given name asynchronously.
     * @param hubPath The Notification Hub name.
     * @return Returns a Mono void for completions.
     */
    Mono<Void> deleteNotificationHubAsync(String hubPath);
}
