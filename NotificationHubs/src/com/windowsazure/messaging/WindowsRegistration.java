//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class representing a native registration for a device using WNS.
 */
public class WindowsRegistration extends Registration {
    private static final String WNS_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><WindowsRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String WNS_NATIVE_REGISTRATION2 = "<ChannelUri>";
    private static final String WNS_NATIVE_REGISTRATION3 = "</ChannelUri></WindowsRegistrationDescription></content></entry>";

    protected URI channelUri;

    public WindowsRegistration() {
    }

    public WindowsRegistration(URI channelUri) {
        super();
        this.channelUri = channelUri;
    }

    public WindowsRegistration(String registrationId, URI channelUri) {
        super(registrationId);
        this.channelUri = channelUri;
    }

    public URI getChannelUri() {
        return channelUri;
    }

    public void setChannelUri(String channelUri) {
        try {
            this.channelUri = new URI(channelUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(channelUri);
        }
    }

    @Override
    public String getPnsHandle() {
        return channelUri.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((channelUri == null) ? 0 : channelUri.hashCode());
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
        WindowsRegistration other = (WindowsRegistration) obj;
        if (channelUri == null) {
            if (other.channelUri != null)
                return false;
        } else if (!channelUri.equals(other.channelUri))
            return false;
        return true;
    }

    @Override
    public String getXml() {
        return WNS_NATIVE_REGISTRATION1 +
            getTagsXml() +
            WNS_NATIVE_REGISTRATION2 +
            channelUri.toString() +
            WNS_NATIVE_REGISTRATION3;
    }

}
