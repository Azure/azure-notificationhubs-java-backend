//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Azure Notification Hubs credentials for WNS.
 */
public final class WindowsCredential extends PnsCredential {
    private String packageSid;
    private String secretKey;

    /**
     * Creates a new instance of the WindowsCredential class.
     */
    public WindowsCredential() {
        super();
    }

    /**
     * Creates a new instance of the WindowsCredential class.
     * @param packageSid The WNS Package SID.
     * @param secretKey The WNS secret key.
     */
    public WindowsCredential(String packageSid, String secretKey) {
        super();
        this.setPackageSid(packageSid);
        this.setSecretKey(secretKey);
    }

    /**
     * Gets the WNS Package SID.
     * @return The WNS Package SID.
     */
    public String getPackageSid() { return packageSid; }

    /**
     * Sets the WNS Package SID.
     * @param value The WNS Package SID to set.
     */
    public void setPackageSid(String value) { packageSid = value; }

    /**
     * Gets the WNS secret key.
     * @return The WNS secret key.
     */
    public String getSecretKey() { return secretKey; }

    /**
     * Sets the WNS secret key.
     * @param value The WNS secret key to set.
     */
    public void setSecretKey(String value) { secretKey = value; }

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
