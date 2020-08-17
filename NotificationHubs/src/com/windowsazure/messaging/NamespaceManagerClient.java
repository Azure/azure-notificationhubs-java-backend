package com.windowsazure.messaging;

import reactor.core.publisher.Mono;
import java.util.List;

public interface NamespaceManagerClient {

    /**
     * Gets a Notification Hub by the hub name.
     * @param hubPath The hub name
     * @return The notification hub retrieved by name
     */
    Mono<NotificationHubDescription> getNotificationHubAsync(String hubPath);

    NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException;

    Mono<List<NotificationHubDescription>> getNotificationHubsAsync();

    List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException;

    Mono<NotificationHubDescription> createNotificationHubAsync(NotificationHubDescription hubDescription);

    NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException;

    Mono<NotificationHubDescription> updateNotificationHubAsync(NotificationHubDescription hubDescription);

    NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException;

    Mono<Void> deleteNotificationHubAsync(String hubPath);

    void deleteNotificationHub(String hubPath) throws NotificationHubsException;
}
