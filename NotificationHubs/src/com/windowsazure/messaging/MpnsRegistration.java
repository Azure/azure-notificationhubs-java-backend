//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Class representing a native registration for a device using MPNS.
 */
public class MpnsRegistration extends Registration {
    private static final String MPNS_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><MpnsRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String MPNS_NATIVE_REGISTRATION2 = "<ChannelUri>";
    private static final String MPNS_NATIVE_REGISTRATION3 = "</ChannelUri></MpnsRegistrationDescription></content></entry>";

    protected URI channelUri;

    /**
     * Creates a new instance of the MpnsRegistration class.
     */
    public MpnsRegistration() {
    }

    /**
     * Creates a new instance of the MpnsRegistration class.
     * @param channelUri The Windows Phone PNS channel URI.
     */
    public MpnsRegistration(URI channelUri) {
        super();
        this.channelUri = channelUri;
    }

    /**
     * Creates a new instance of the MpnsRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param channelUri The Windows Phone PNS channel URI.
     */
    public MpnsRegistration(String registrationId, URI channelUri) {
        super(registrationId);
        this.channelUri = channelUri;
    }

    /**
     * Gets the Windows Phone PNS channel URI.
     * @return The Windows Phone PNS channel URI.
     */
    public URI getChannelUri() { return channelUri; }

    /**
     * Sets the Windows Phone PNS channel URI.
     * @param channelUri The Windows Phone PNS channel URI to set.
     */
    public void setChannelUri(String channelUri) {
        try {
            this.channelUri = new URI(channelUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(channelUri);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MpnsRegistration that = (MpnsRegistration) o;
        return Objects.equals(getChannelUri(), that.getChannelUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getChannelUri());
    }

    @Override
    public String getXml() {
        return MPNS_NATIVE_REGISTRATION1 +
            getTagsXml() +
            MPNS_NATIVE_REGISTRATION2 +
            channelUri.toString() +
            MPNS_NATIVE_REGISTRATION3;
    }
}
