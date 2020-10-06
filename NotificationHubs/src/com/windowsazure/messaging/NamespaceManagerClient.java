//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.http.concurrent.FutureCallback;

import java.util.List;

public interface NamespaceManagerClient {
    void getNotificationHubAsync(String hubPath, FutureCallback<NotificationHubDescription> callback);

    NotificationHubDescription getNotificationHub(String hubPath) throws NotificationHubsException;

    void getNotificationHubsAsync(FutureCallback<List<NotificationHubDescription>> callback);

    List<NotificationHubDescription> getNotificationHubs() throws NotificationHubsException;

    void createNotificationHubAsync(NotificationHubDescription hubDescription, FutureCallback<NotificationHubDescription> callback);

    NotificationHubDescription createNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException;

    void updateNotificationHubAsync(NotificationHubDescription hubDescription, FutureCallback<NotificationHubDescription> callback);

    NotificationHubDescription updateNotificationHub(NotificationHubDescription hubDescription) throws NotificationHubsException;

    void deleteNotificationHubAsync(String hubPath, FutureCallback<Object> callback);

    void deleteNotificationHub(String hubPath) throws NotificationHubsException;
}
