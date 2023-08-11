//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Windows Phone credentials for Azure Notification Hubs.
 */
public final class MpnsCredential extends PnsCredential {
    private String mpnsCertificate;
    private String certificateKey;

    /**
     * Creates a new instance of the MpnsCredential class.
     */
    public MpnsCredential() {
        super();
    }

    /**
     * Creates a new instance of the MpnsCredential class with certificate and certificate key.
     * @param mpnsCertificate The Windows Phone PNS certificate.
     * @param certificateKey THe Windows Phone PNS certificate key.
     */
    public MpnsCredential(String mpnsCertificate, String certificateKey) {
        super();
        this.mpnsCertificate = mpnsCertificate;
        this.certificateKey = certificateKey;
    }

    /**
     * Gets the Windows Phone PNS certificate.
     * @return The Windows Phone PNS certificate.
     */
    public String getMpnsCertificate() { return mpnsCertificate; }

    /**
     * Sets the Windows Phone PNS certificate.
     * @param value The Windows Phone PNS certificate to set.
     */
    public void setMpnsCertificate(String value) { mpnsCertificate = value; }

    /**
     * Gets the Windows Phone PNS certificate key.
     * @return The Windows Phone PNS certificate key.
     */
    public String getCertificateKey() { return certificateKey; }

    /**
     * Sets the Windows Phone PNS certificate key.
     * @param value THe Windows Phone PNS certificate key.
     */
    public void setCertificateKey(String value) { certificateKey = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        if (getMpnsCertificate() != null || getCertificateKey() != null) {
            result.add(new SimpleEntry<>("MpnsCertificate", getMpnsCertificate()));
            result.add(new SimpleEntry<>("CertificateKey", getCertificateKey()));
        }
        return result;
    }

    @Override
    public String getRootTagName() {
        return "MpnsCredential";
    }
}
