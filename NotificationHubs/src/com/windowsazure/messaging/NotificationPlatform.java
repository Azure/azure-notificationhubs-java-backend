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
	@SerializedName("adm")
	Adm,
}
