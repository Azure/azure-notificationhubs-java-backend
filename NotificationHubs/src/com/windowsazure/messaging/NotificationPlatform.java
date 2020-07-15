//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.annotations.SerializedName;

public enum NotificationPlatform {
	@SerializedName("wns")
	Wns,
	@SerializedName("apns")
	Apns,
	@SerializedName("mpns")
	Mpns,
	@SerializedName("gcm")
	Gcm,
	@SerializedName("fcm")
	Fcm,
	@SerializedName("adm")
	Adm,
}
