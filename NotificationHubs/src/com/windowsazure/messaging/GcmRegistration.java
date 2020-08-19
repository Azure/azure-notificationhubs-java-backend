//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * Class representing a native registration for devices using GCM.
 * @deprecated use {@link com.windowsazure.messaging.FcmRegistration#FcmRegistration()} instead.
 */

@Deprecated
public class GcmRegistration extends Registration {
	private static final String GCM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String GCM_NATIVE_REGISTRATION2 = "<GcmRegistrationId>";
	private static final String GCM_NATIVE_REGISTRATION3 = "</GcmRegistrationId></GcmRegistrationDescription></content></entry>";

	protected String gcmRegistrationId;



	public GcmRegistration() {
		super();
	}

	public GcmRegistration(String registrationId, String gcmRegistrationId) {
		super(registrationId);
		this.gcmRegistrationId = gcmRegistrationId;
	}

	public GcmRegistration(String gcmRegistrationId) {
		super();
		this.gcmRegistrationId = gcmRegistrationId;
	}

	public String getGcmRegistrationId() {
		return gcmRegistrationId;
	}

	public void setGcmRegistrationId(String gcmRegistrationId) {
		this.gcmRegistrationId = gcmRegistrationId;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((gcmRegistrationId == null) ? 0 : gcmRegistrationId
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
		GcmRegistration other = (GcmRegistration) obj;
		if (gcmRegistrationId == null) {
            return other.gcmRegistrationId == null;
		} else return gcmRegistrationId.equals(other.gcmRegistrationId);
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
