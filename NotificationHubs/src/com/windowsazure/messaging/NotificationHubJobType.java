//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * Type of Notification Hub jobs.
 */
public enum NotificationHubJobType {
    /**
     * Export registrations
     */
	ExportRegistrations,
    /**
     * Import create registrations
     */
	ImportCreateRegistrations,
    /**
     * Import update registrations
     */
	ImportUpdateRegistrations,
    /**
     * Import delete registrations
     */
	ImportDeleteRegistrations
}
