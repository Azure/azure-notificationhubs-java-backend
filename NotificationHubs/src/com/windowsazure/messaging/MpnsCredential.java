//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of Windows Phone PNS Credentials
 */
public final class MpnsCredential extends PnsCredential {
	private String mpnsCertificate;
	private String certificateKey;

	/**
	 * Creates a new Windows Phone credentials.
	 */
	public MpnsCredential() {
		this(null,null);
	}

	/**
	 * Creates a new Windows Phone credentials with the certificate and certificate key.
	 * @param mpnsCertificate The Windows Phone certificate.
	 * @param certificateKey The Windows Phone certificate key.
	 */
	public MpnsCredential(String mpnsCertificate, String certificateKey) {
		super();
		this.setMpnsCertificate(mpnsCertificate);
		this.setCertificateKey(certificateKey);
	}

	/**
	 * Gets the Windows Phone certificate.
	 * @return The Windows Phone certificate.
	 */
	public String getMpnsCertificate() {
		return mpnsCertificate;
	}

	/**
	 * Sets the Windows Phone certificate.
	 * @param mpnsCertificate The Windows Phone certificate.
	 */
	public void setMpnsCertificate(String mpnsCertificate) {
		this.mpnsCertificate = mpnsCertificate;
	}

	/**
	 * Gets the Windows Phone certificate key.
	 * @return The Windows Phone certificate key.
	 */
	public String getCertificateKey() {
		return certificateKey;
	}

	/**
	 * Sets the Windows Phone certificate key.
	 * @param certificateKey The Windows Phone certificate key.
	 */
	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}

	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
		result.add(new SimpleEntry<>("MpnsCertificate", getMpnsCertificate()));
		result.add(new SimpleEntry<>("CertificateKey", getCertificateKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "MpnsCredential";
	}
}