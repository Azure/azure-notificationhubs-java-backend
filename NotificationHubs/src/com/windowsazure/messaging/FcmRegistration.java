//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * Class representing a native registration for devices using FCM.
 */
public class FcmRegistration extends Registration {
    private static final String FCM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String FCM_NATIVE_REGISTRATION2 = "<GcmRegistrationId>";
    private static final String FCM_NATIVE_REGISTRATION3 = "</GcmRegistrationId></GcmRegistrationDescription></content></entry>";

    protected String fcmRegistrationId;

    /**
     * Creates a new instance of the FcmRegistration class.
     */
    public FcmRegistration() {
        super();
    }

    /**
     * Creates a new instance of the FcmRegistration with a registration ID and FCM registration ID.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param fcmRegistrationId The Firebase Cloud Messaging registration ID.
     */
    public FcmRegistration(String registrationId, String fcmRegistrationId) {
        super(registrationId);
        this.fcmRegistrationId = fcmRegistrationId;
    }

    /**
     * Creates a new instance of the FcmRegistration with a Firebase Cloud Messaging registration ID.
     * @param fcmRegistrationId The Firebase Cloud Messaging registration ID.
     */
    public FcmRegistration(String fcmRegistrationId) {
        super();
        this.fcmRegistrationId = fcmRegistrationId;
    }

    /**
     * Gets the Firebase Messaging registration ID.
     * @return The Firebase Messaging registration ID.
     */
    public String getFcmRegistrationId() { return fcmRegistrationId; }

    /**
     * Sets the Firebase Messaging registration ID.
     * @param value The Firebase Messaging registration ID to set.
     */
    public void setFcmRegistrationId(String value) { fcmRegistrationId = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FcmRegistration that = (FcmRegistration) o;
        return Objects.equals(getFcmRegistrationId(), that.getFcmRegistrationId());
    }

    /**
     * Gets the PNS handle for getting devices by channel.
     * @return The PNS handle for getting devices by channel.
     */
    @Override
    public String getPnsHandle() { return fcmRegistrationId; }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFcmRegistrationId());
    }

    @Override
    public String getXml() {
        return FCM_NATIVE_REGISTRATION1 +
            getTagsXml() +
            FCM_NATIVE_REGISTRATION2 +
            fcmRegistrationId +
            FCM_NATIVE_REGISTRATION3;
    }

}
