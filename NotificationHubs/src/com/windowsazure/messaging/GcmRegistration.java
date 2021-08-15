//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * Class representing a native registration for devices using GCM.
 *
 * @deprecated use {@link com.windowsazure.messaging.FcmRegistration#FcmRegistration()} instead.
 */

@Deprecated
public class GcmRegistration extends Registration {
    private static final String GCM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String GCM_NATIVE_REGISTRATION2 = "<GcmRegistrationId>";
    private static final String GCM_NATIVE_REGISTRATION3 = "</GcmRegistrationId></GcmRegistrationDescription></content></entry>";

    protected String gcmRegistrationId;

    /**
     * Creates a new instance of the GcmRegistration class.
     */
    public GcmRegistration() {
        super();
    }

    /**
     * Creates a new instance of the GcmRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param gcmRegistrationId The Google Cloud Messaging registration ID.
     */
    public GcmRegistration(String registrationId, String gcmRegistrationId) {
        super(registrationId);
        this.gcmRegistrationId = gcmRegistrationId;
    }

    /**
     * Creates a new instance of the GcmRegistration class.
     * @param gcmRegistrationId The Google Cloud Messaging registration ID.
     */
    public GcmRegistration(String gcmRegistrationId) {
        super();
        this.gcmRegistrationId = gcmRegistrationId;
    }

    /**
     * Gets the Google Cloud Messaging registration ID.
     * @return The Google Cloud Messaging registration ID.
     */
    public String getGcmRegistrationId() { return gcmRegistrationId; }

    /**
     * Sets the Google Cloud Messaging registration ID.
     * @param value The Google Cloud Messaging registration ID to set.
     */
    public void setGcmRegistrationId(String value) { gcmRegistrationId = value; }

    /**
     * Gets the PNS handle for getting devices by channel.
     * @return The PNS handle for getting devices by channel.
     */
    @Override
    public String getPnsHandle() { return gcmRegistrationId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GcmRegistration that = (GcmRegistration) o;
        return Objects.equals(getGcmRegistrationId(), that.getGcmRegistrationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGcmRegistrationId());
    }

    @Override
    public String getXml() {
        return GCM_NATIVE_REGISTRATION1 +
            getTagsXml() +
            GCM_NATIVE_REGISTRATION2 +
            gcmRegistrationId +
            GCM_NATIVE_REGISTRATION3;
    }
}
