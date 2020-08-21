//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents an Amazon Kindle Fire registration.
 */
public class AdmRegistration extends Registration {
	private static final String ADM_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AdmRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String ADM_NATIVE_REGISTRATION2 = "<AdmRegistrationId>";
	private static final String ADM_NATIVE_REGISTRATION3 = "</AdmRegistrationId></AdmRegistrationDescription></content></entry>";

	protected String admRegistrationId;

	/**
	 * Creates an ADM registration.
	 */
	public AdmRegistration() {
		super();
	}

	/**
	 * Creates an ADM registration with registration ID and ADM registration ID.
	 * @param registrationId The registration ID.
	 * @param admRegistrationId The ADM registration ID.
	 */
	public AdmRegistration(String registrationId, String admRegistrationId) {
		super(registrationId);
		this.admRegistrationId = admRegistrationId;
	}

	/**
	 * Creates an ADM registration with an ADM registration ID.
	 * @param admRegistrationId The ADM registration ID.
	 */
	public AdmRegistration(String admRegistrationId) {
		super();
		this.admRegistrationId = admRegistrationId;
	}

	/**
	 * Gets the ADM registration ID.
	 * @return The ADM registration ID.
	 */
	public String getAdmRegistrationId() {
		return admRegistrationId;
	}

	/**
	 * Sets the ADM registration ID.
	 * @param admRegistrationId The ADM registration ID.
	 */
	public void setAdmRegistrationId(String admRegistrationId) {
		this.admRegistrationId = admRegistrationId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((admRegistrationId == null) ? 0 : admRegistrationId
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
		AdmRegistration other = (AdmRegistration) obj;
		if (admRegistrationId == null) {
			return other.admRegistrationId == null;
		} else return admRegistrationId.equals(other.admRegistrationId);
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