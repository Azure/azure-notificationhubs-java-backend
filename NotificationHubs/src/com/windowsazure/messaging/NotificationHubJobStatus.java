//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This represents the notification hub job status.
 */
public enum NotificationHubJobStatus {
	/**
	 * The job has started.
	 */
	Started,
	/**
	 * The job is running.
	 */
	Running,
	/**
	 * The job has completed.
	 */
	Completed,
	/**
	 * The job has failed.
	 */
	Failed
}