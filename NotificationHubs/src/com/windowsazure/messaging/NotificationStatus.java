//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This enum represents the status of a notification.
 */
public enum NotificationStatus {
    /**
     * The notification has been abandoned.
     */
    Abandoned,
    /**
     * This notification has been cancelled.
     */
    Canceled,
    /**
     * This notification has been completed.
     */
    Completed,
    /**
     * This notification has been enqueued into the system.
     */
    Enqueued,
    /**
     * This notification was not delivered as there were no targets found for the
     * notification.
     */
    NoTargetFound,
    /**
     * The notification is currently being processed by Azure Notification Hubs.
     */
    Processing,
    /**
     * The notification has been scheduled for a future time.
     */
    Scheduled,
    /**
     * The notification has an unknown status.
     */
    Unknown
}
