//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class representing a native registration for a device using MPNS.
 */
public class MpnsRegistration extends Registration {
    private static final String MPNS_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><MpnsRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String MPNS_NATIVE_REGISTRATION2 = "<ChannelUri>";
    private static final String MPNS_NATIVE_REGISTRATION3 = "</ChannelUri></MpnsRegistrationDescription></content></entry>";

    protected URI channelUri;

    /**
     * Creates a Windows Phone registration.
     */
    public MpnsRegistration() {
    }

    /**
     * Creates a Windows Phone registration with a channel URI.
     *
     * @param channelUri The channel URI for the registration.
     */
    public MpnsRegistration(URI channelUri) {
        super();
        this.channelUri = channelUri;
    }

    /**
     * Creates a Windows Phone registration with registration ID and channel URI.
     *
     * @param registrationId The registration ID.
     * @param channelUri     The channel URI for the registration.
     */
    public MpnsRegistration(String registrationId, URI channelUri) {
        super(registrationId);
        this.channelUri = channelUri;
    }

    /**
     * Gets the Windows Phone channel URI.
     *
     * @return The Windows Phone channel URI.
     */
    public URI getChannelUri() {
        return channelUri;
    }

    /**
     * Sets the channel URI
     *
     * @param channelUri The channel URI.
     */
    public void setChannelUri(String channelUri) {
        try {
            this.channelUri = new URI(channelUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(channelUri);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((channelUri == null) ? 0 : channelUri.hashCode());
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
        MpnsRegistration other = (MpnsRegistration) obj;
        if (channelUri == null) {
            return other.channelUri == null;
        } else
            return channelUri.equals(other.channelUri);
    }

    @Override
    public String getXml() {
        return MPNS_NATIVE_REGISTRATION1 + getTagsXml() + MPNS_NATIVE_REGISTRATION2 + channelUri.toString()
                + MPNS_NATIVE_REGISTRATION3;
    }

}
