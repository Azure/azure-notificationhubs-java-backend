//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * Class representing a native registration for devices using FCM.
 */
public class FcmRegistration extends Registration {
	// TODO replace content to FCM* when new version of backend will be released
	private static final String FCM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String FCM_NATIVE_REGISTRATION2 = "<GcmRegistrationId>";
	private static final String FCM_NATIVE_REGISTRATION3 = "</GcmRegistrationId></GcmRegistrationDescription></content></entry>";

	protected String fcmRegistrationId;

    /**
     * Creates a new FCM credential.
     */
	public FcmRegistration() {
		super();
	}

    /**
     * Creates a new FCM credential with registration ID and FCM registration ID.
     * @param registrationId The registration ID.
     * @param fcmRegistrationId The FCM registration ID.
     */
	public FcmRegistration(String registrationId, String fcmRegistrationId) {
		super(registrationId);
		this.fcmRegistrationId = fcmRegistrationId;
	}

    /**
     * Creates a new FCM credential with the FM registration ID.
     * @param fcmRegistrationId The FCM registration ID.
     */
	public FcmRegistration(String fcmRegistrationId) {
		super();
		this.fcmRegistrationId = fcmRegistrationId;
	}

    /**
     * Gets the FCM registration ID.
     * @return The FCM registration ID.
     */
	public String getFcmRegistrationId() {
		return fcmRegistrationId;
	}

    /**
     * Sets the FCM registration ID.
     * @param fcmRegistrationId The FCM registration ID.
     */
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
            return other.fcmRegistrationId == null;
		} else return fcmRegistrationId.equals(other.fcmRegistrationId);
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
