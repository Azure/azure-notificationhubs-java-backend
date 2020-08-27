//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.annotations.SerializedName;

/**
 * This enum represents the types of notification platforms supported.
 */
public enum NotificationPlatform {
    /**
     * The WNS platform.
     */
    @SerializedName("wns")
    Wns,
    /**
     * The APNS platform.
     */
    @SerializedName("apns")
    Apns,
    /**
     * The MPNS platform for Windows Phone.
     */
    @SerializedName("mpns")
    Mpns,
    /**
     * The FCM Legacy Platform, see https://aka.ms/AA9dpaz
     */
    @SerializedName("gcm")
    Gcm,
    /**
     * The FCM platform which is currently NOT supported, see https://aka.ms/AA9dpaz
     */
    @SerializedName("fcm")
    Fcm,
    /**
     * The ADM platform.
     */
    @SerializedName("adm")
    Adm,
}
