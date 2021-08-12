//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * Class representing a native registration for a device using APNs.
 */
public class AppleRegistration extends Registration {
    private static final String APNS_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AppleRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String APNS_NATIVE_REGISTRATION2 = "<DeviceToken>";
    private static final String APNS_NATIVE_REGISTRATION3 = "</DeviceToken></AppleRegistrationDescription></content></entry>";

    protected String deviceToken;

    /**
     * Creates a new instance of the AppleRegistration class.
     */
    public AppleRegistration() {
        super();
    }

    /**
     * Creates a new instance of the AppleRegistration class.
     * @param deviceToken The APNS device token for the registration.
     */
    public AppleRegistration(String deviceToken) {
        super();
        this.deviceToken = deviceToken;
    }

    /**
     * Creates a new instance of the AppleRegistration class.
     * @param registrationId The registration ID for the device.
     * @param deviceToken The APNS device token for the registration.
     */
    public AppleRegistration(String registrationId, String deviceToken) {
        super(registrationId);
        this.deviceToken = deviceToken;
    }

    /**
     * Gets the APNS device token for the registration.
     * @return The APNS device token for the registration.
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Sets the APNS device token for the registration.
     * @param value The APNS device token for the registration to set.
     */
    public void setDeviceToken(String value) { deviceToken = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AppleRegistration that = (AppleRegistration) o;
        return Objects.equals(getDeviceToken(), that.getDeviceToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDeviceToken());
    }

    @Override
    public String getXml() {
        return APNS_NATIVE_REGISTRATION1 +
            getTagsXml() +
            APNS_NATIVE_REGISTRATION2 +
            deviceToken +
            APNS_NATIVE_REGISTRATION3;
    }

}
