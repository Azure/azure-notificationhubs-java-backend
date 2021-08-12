//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * This class represents an Amazon device registration.
 */
public class AdmRegistration extends Registration {
    private static final String ADM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AdmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String ADM_NATIVE_REGISTRATION2 = "<AdmRegistrationId>";
    private static final String ADM_NATIVE_REGISTRATION3 = "</AdmRegistrationId></AdmRegistrationDescription></content></entry>";

    protected String admRegistrationId;

    /**
     * Creates a new instance of the AdmRegistration class.
     */
    public AdmRegistration() {
        super();
    }

    /**
     * Creates a new instance of the AdmRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID for the device.
     * @param admRegistrationId The Amazon device registration ID.
     */
    public AdmRegistration(String registrationId, String admRegistrationId) {
        super(registrationId);
        this.admRegistrationId = admRegistrationId;
    }

    /**
     * Creates a new instance of the AdmRegistration class.
     * @param admRegistrationId The Amazon device registration ID.
     */
    public AdmRegistration(String admRegistrationId) {
        super();
        this.admRegistrationId = admRegistrationId;
    }

    /**
     * Gets the Amazon registration ID for the device.
     * @return The Amazon registration ID for the device.
     */
    public String getAdmRegistrationId() {
        return admRegistrationId;
    }

    /**
     * Sets the Amazon registration ID for the device.
     * @param value The Amazon registration ID for the device to set.
     */
    public void setAdmRegistrationId(String value) {
        admRegistrationId = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdmRegistration that = (AdmRegistration) o;
        return Objects.equals(getAdmRegistrationId(), that.getAdmRegistrationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAdmRegistrationId());
    }

    @Override
    public String getXml() {
        return ADM_NATIVE_REGISTRATION1 +
            getTagsXml() +
            ADM_NATIVE_REGISTRATION2 +
            admRegistrationId +
            ADM_NATIVE_REGISTRATION3;
    }

}
