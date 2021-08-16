//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Class representing a native registration for a device using WNS.
 */
public class WindowsRegistration extends Registration {
    private static final String WNS_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><WindowsRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String WNS_NATIVE_REGISTRATION2 = "<ChannelUri>";
    private static final String WNS_NATIVE_REGISTRATION3 = "</ChannelUri></WindowsRegistrationDescription></content></entry>";

    protected URI channelUri;

    /**
     * Creates a new instance of the WindowsRegistration class.
     */
    public WindowsRegistration() {
        super();
    }

    /**
     * Creates a new instance of the WindowsRegistration class.
     * @param channelUri The channel URI for the WNS registration.
     */
    public WindowsRegistration(URI channelUri) {
        super();
        this.channelUri = channelUri;
    }

    /**
     * Creates a new instance of the WindowsRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param channelUri The channel URI for the WNS registration.
     */
    public WindowsRegistration(String registrationId, URI channelUri) {
        super(registrationId);
        this.channelUri = channelUri;
    }

    /**
     * Gets the WNS channel URI.
     * @return The WNS channel URI.
     */
    public URI getChannelUri() { return channelUri;  }

    /**
     * Sets the WNS channel URI from a string.
     * @param channelUri The WNS channel URI string to set.
     */
    public void setChannelUri(String channelUri) {
        try {
            this.channelUri = new URI(channelUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(channelUri);
        }
    }

    /**
     * Gets the PNS handle for getting devices by channel.
     * @return The PNS handle for getting devices by channel.
     */
    @Override
    public String getPnsHandle() { return channelUri.toString(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WindowsRegistration that = (WindowsRegistration) o;
        return Objects.equals(getChannelUri(), that.getChannelUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getChannelUri());
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
