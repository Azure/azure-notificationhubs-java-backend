//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a WNS credential.
 */
public final class WindowsCredential extends PnsCredential {
	private String packageSid;
	private String secretKey;

    /**
     * Creates a WNS credential.
     */
	public WindowsCredential(){
		this(null,null);
	}

    /**
     * Creates a WNS credential with package SDI and secret key.
     * @param packageSid The package SID for the WNS credentials.
     * @param secretKey The secret key for the WNS credentials.
     */
	public WindowsCredential(String packageSid, String secretKey){
		super();
		this.setPackageSid(packageSid);
		this.setSecretKey(secretKey);
	}

    /**
     * Gets the package SID for the WNS credentials.
     * @return The package SID for the WNS credentials.
     */
	public String getPackageSid() {
		return packageSid;
	}

    /**
     * Sets the package SDI for the WNS credentials.
     * @param packageSid The package SDI for the WNS credentials.
     */
	public void setPackageSid(String packageSid) {
		this.packageSid = packageSid;
	}

    /**
     * Gets the secret key for the WNS credentials.
     * @return The secret key for the WNS credentials.
     */
	public String getSecretKey() {
		return secretKey;
	}

    /**
     * Sets the secret key for the WNS credentials.
     * @param secretKey The secret key for the WNS credentials.
     */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

    	public void setWindowsLiveEndpoint(String propertyValue) {
        	// fix for reflection that's calling 'setWindowsLiveEndpoint' of null.
        	// unused function
    	}

	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
		result.add(new SimpleEntry<>("PackageSid", getPackageSid()));
		result.add(new SimpleEntry<>("SecretKey", getSecretKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "WnsCredential";
	}
}
