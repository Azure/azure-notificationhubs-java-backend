//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a set of APNS credentials.
 */
public final class ApnsCredential extends PnsCredential {
    /**
     * The production endpoint for APNS V1 send operations.  This is the current default.
     */
    public static final String PROD_ENDPOINT = "gateway.push.apple.com";

    /**
     * The sandbox endpoint for APNS V1 operations.
     */
    public static final String SANDBOX_ENDPOINT = "gateway.sandbox.push.apple.com";

    /**
     * The production endpoint for APNS V2 operations.
     */
    public static final String APNS2_PROD_ENDPOINT = "https://api.push.apple.com:443/3/device";

    /**
     * The development endpoint for APNS V2 operations.
     */
    public static final String APNS2_DEV_ENDPOINT = "https://api.development.push.apple.com:443/3/device";

    private String endpoint;
    private String apnsCertificate;
    private String certificateKey;
    private String thumbprint;
    private String token;
    private String keyId;
    private String appName;
    private String appId;

    /**
     * Creates a new instance of the ApnsCredential class.
     */
    public ApnsCredential() {
        super();
    }

    /**
     * Creates a new instance of the ApnsCredential class.
     * @param endpoint The APNS API endpoint
     */
    public ApnsCredential(String endpoint) {
        super();
        this.endpoint = endpoint;
    }

    /**
     * Creates a new instance of the ApnsCredential class.
     * @param apnsCertificate The APNS certificate.
     * @param certificateKey  The APNS certificate key.
     */
    public ApnsCredential(String apnsCertificate, String certificateKey) {
        this(apnsCertificate, certificateKey, APNS2_PROD_ENDPOINT);
    }

    /**
     * Creates an Apple credential with APNS certificate, certificate key, and API
     * endpoint.
     *
     * @param apnsCertificate The APNS certificate.
     * @param certificateKey  The APNS certificate key.
     * @param endpoint        The API endpoint
     */
    public ApnsCredential(String apnsCertificate, String certificateKey, String endpoint) {
        super();
        this.apnsCertificate = apnsCertificate;
        this.certificateKey = certificateKey;
        this.endpoint = endpoint;
    }

    /**
     * Creates an APNS credential with token, key ID, app ID and app name.
     *
     * @param token   The APNS token from the P8 file.
     * @param keyId   The push key ID.
     * @param appId   The app ID.
     * @param appName The app name.
     */
    public ApnsCredential(String token, String keyId, String appId, String appName) {
        this(token, keyId, appId, appName, PROD_ENDPOINT);
    }

    /**
     * Creates an APNS credential with token, key ID, app ID and app name.
     *
     * @param token    The APNS token from the P8 file.
     * @param keyId    The push key ID.
     * @param appId    The app ID.
     * @param appName  The app name.
     * @param endpoint The API endpoint
     */
    public ApnsCredential(String token, String keyId, String appId, String appName, String endpoint) {
        this.token = token;
        this.keyId = keyId;
        this.appId = appId;
        this.appName = appName;
        this.endpoint = endpoint;
    }

    /**
     * Gets the APNS API endpoint
     *
     * @return The APNS API endpoint.
     */
    public String getEndpoint() { return endpoint; }

    /**
     * Sets the APNS API endpoint.
     *
     * @param value The APNS API endpoint.
     */
    public void setEndpoint(String value) { this.endpoint = value; }

    /**
     * Gets the APNS certificate.
     *
     * @return The APNS certificate.
     */
    public String getApnsCertificate() { return apnsCertificate; }

    /**
     * Sets the APNS certificate.
     *
     * @param value The APNS certificate.
     */
    public void setApnsCertificate(String value) { apnsCertificate = value; }

    /**
     * Gets the certificate thumbprint.
     *
     * @return The certificate thumbprint.
     */
    public String getThumbprint() { return thumbprint; }

    /**
     * Sets the certificate Thumbprint
     *
     * @param value The certificate thumbprint.
     */
    public void setThumbprint(String value) { thumbprint = value; }

    /**
     * Gets the APNS Certificate key.
     *
     * @return The APNS certificate key.
     */
    public String getCertificateKey() { return certificateKey; }

    /**
     * Gets the APNS certificate key.
     *
     * @param value The APNS certificate key.
     */
    public void setCertificateKey(String value) { certificateKey = value; }

    /**
     * Gets the APNS token for authentication.
     *
     * @return The APNS token for authentication.
     */
    public String getToken() { return token; }

    /**
     * Sets the APNS token for authentication.
     *
     * @param value The APNS token for authentication.
     */
    public void setToken(String value) { token = value; }

    /**
     * Gets the APNS key ID.
     *
     * @return The APNS key ID.
     */
    public String getKeyId() { return keyId; }

    /**
     * Sets the APNS key ID.
     *
     * @param value The APNS key ID.
     */
    public void setKeyId(String value) { keyId = value; }

    /**
     * Gets the Apple App name.
     *
     * @return The Apple App name.
     */
    public String getAppName() { return appName; }

    /**
     * Sets the Apple App name
     *
     * @param value The Apple App name.
     */
    public void setAppName(String value) { this.appName = value; }

    /**
     * Gets the Apple App ID.
     *
     * @return The Apple App ID.
     */
    public String getAppId() { return appId; }

    /**
     * Sets the Apple App ID.
     *
     * @param value The Apple App ID.
     */
    public void setAppId(String value) { appId = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        result.add(new SimpleEntry<>("Endpoint", getEndpoint()));
        result.add(new SimpleEntry<>("ApnsCertificate", getApnsCertificate()));
        result.add(new SimpleEntry<>("CertificateKey", getCertificateKey()));
        result.add(new SimpleEntry<>("Thumbprint", getThumbprint()));
        result.add(new SimpleEntry<>("Token", getToken()));
        result.add(new SimpleEntry<>("KeyId", getKeyId()));
        result.add(new SimpleEntry<>("AppName", getAppName()));
        result.add(new SimpleEntry<>("AppId", getAppId()));
        return result;
    }

    @Override
    public String getRootTagName() {
        return "ApnsCredential";
    }
}
