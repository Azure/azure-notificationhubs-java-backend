//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * Class representing a native registration for devices using FCM.
 */
public class FcmRegistration extends Registration {
    private static final String FCM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String FCM_NATIVE_REGISTRATION2 = "<GcmRegistrationId>";
    private static final String FCM_NATIVE_REGISTRATION3 = "</GcmRegistrationId></GcmRegistrationDescription></content></entry>";

    protected String fcmRegistrationId;

    public FcmRegistration() {
        super();
    }

    public FcmRegistration(String registrationId, String fcmRegistrationId) {
        super(registrationId);
        this.fcmRegistrationId = fcmRegistrationId;
    }

    public FcmRegistration(String fcmRegistrationId) {
        super();
        this.fcmRegistrationId = fcmRegistrationId;
    }

    public String getFcmRegistrationId() {
        return fcmRegistrationId;
    }

    public void setFcmRegistrationId(String fcmRegistrationId) {
        this.fcmRegistrationId = fcmRegistrationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
            * result
            + ((fcmRegistrationId == null) ? 0 : fcmRegistrationId
            .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        FcmRegistration other = (FcmRegistration) obj;
        if (fcmRegistrationId == null) {
            if (other.fcmRegistrationId != null)
                return false;
        } else if (!fcmRegistrationId.equals(other.fcmRegistrationId))
            return false;
        return true;
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
