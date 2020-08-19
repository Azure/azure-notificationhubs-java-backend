//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * Class representing a native registration for a device using WNS.
 */
public class WindowsRegistration extends Registration {
	private static final String WNS_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><WindowsRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String WNS_NATIVE_REGISTRATION2 = "<ChannelUri>";
	private static final String WNS_NATIVE_REGISTRATION3 = "</ChannelUri></WindowsRegistrationDescription></content></entry>";

	protected URI channelUri;

    /**
     * Creates a new WNS registration.
     */
	public WindowsRegistration() {
	}

    /**
     * Creates a WNS registration with channel URI
     * @param channelUri The channel URI for the WNS registration.
     */
	public WindowsRegistration(URI channelUri) {
		super();
		this.channelUri = channelUri;
	}

    /**
     * Creates a WNS registration with registration ID and channel URI
     * @param registrationId The registration ID for the registration.
     * @param channelUri The channel URI for the registration.
     */
	public WindowsRegistration(String registrationId, URI channelUri) {
		super(registrationId);
		this.channelUri = channelUri;
	}

    /**
     * Gets the channel URI for the WNS registration.
     * @return The channel URI for the WNS registration.
     */
	public URI getChannelUri() {
		return channelUri;
	}

    /**
     * Sets the channel URI for the WNS registration.
     * @param channelUri The channel URI for the WNS registration.
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
            return other.channelUri == null;
		} else return channelUri.equals(other.channelUri);
    }

	@Override
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append(WNS_NATIVE_REGISTRATION1);
		buf.append(getTagsXml());
		buf.append(WNS_NATIVE_REGISTRATION2);
		buf.append(channelUri.toString());
		buf.append(WNS_NATIVE_REGISTRATION3);
		return buf.toString();
	}

}
