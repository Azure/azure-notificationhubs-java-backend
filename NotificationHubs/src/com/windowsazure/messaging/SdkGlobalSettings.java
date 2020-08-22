//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents the SDK global settings.
 */
public class SdkGlobalSettings {
    private static int authorizationTokenExpirationInMinutes = 60;

    /**
     * Gets the authorization token expiration in minutes.
     *
     * @return The authorization token expiration in minutes.
     */
    public static int getAuthorizationTokenExpirationInMinutes() {
        return SdkGlobalSettings.authorizationTokenExpirationInMinutes;
    }

    /**
     * Sets the authorization token expiration in minutes.
     *
     * @param value The authorization token expiration in minutes.
     */
    public static void setAuthorizationTokenExpirationInMinutes(int value) {
        SdkGlobalSettings.authorizationTokenExpirationInMinutes = value;
    }
}
