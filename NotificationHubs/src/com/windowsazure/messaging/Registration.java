//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreationFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Abstract class representing a registration.
 */
public abstract class Registration implements Cloneable {
	protected String registrationId;
	protected Set<String> tags = new HashSet<>();
	protected String etag;
	protected Date expirationTime;

	private static final AtomicReference<Digester> singleRegParser = new AtomicReference<>();
	private static final AtomicReference<Digester> multiRegParser = new AtomicReference<>();

	static {
		Digester singleReginstance = new Digester();
		addRegistrationRules(singleReginstance);
		singleRegParser.set( singleReginstance);

		Digester multiReginstance = new Digester();
		addRegistrationRules(multiReginstance);
		addCollectionRules(multiReginstance);
		multiRegParser.set( multiReginstance);
	}

	public Registration() {
	}

    /**
     * Clones a given registration.
     * @return The cloned registration.
     */
	public Registration clone() {
	    try {
			return (Registration) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Creates a new registration from the given registration ID.
     * @param registrationId The ID for the registration.
     */
	public Registration(String registrationId) {
		super();
		this.registrationId = registrationId;
	}

    /**
     * Creates a registration with the tags.
     * @param tags The tags for the registration
     */
	public Registration(Set<String> tags) {
		super();
		this.tags = tags;
	}

    /**
     * Gets an XML representation of the registration
     * @return An XML representation of the registration.
     */
	public abstract String getXml();

    /**
     * Gets the registration ID.
     * @return The ID associated with the registration.
     */
	public String getRegistrationId() {
		return registrationId;
	}

    /**
     * Sets the registration ID.
     * @param registrationId The ID for the registration.
     */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

    /**
     * Gets the registration tags.
     * @return The tags associated with the registration.
     */
	public Set<String> getTags() {
		return tags;
	}

    /**
     * Sets the tags for the registration.
     * @param tags The tags for the registration.
     */
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

    /**
     * Gets the ETag for the registration.
     * @return The ETag for the registration.
     */
	public String getEtag() {
		return etag;
	}

    /**
     * Sets the ETag for the registration
     * @param etag The ETag for the registration.
     */
	public void setEtag(String etag) {
		this.etag = etag;
	}

    /**
     * Sets tags from a string delimited by a comma (,).
     * @param string The list of tags delimited by a comma (,)
     */
	public void setTagsFromString(String string) {
		tags = new HashSet<>();
		String[] tagsArray = string.split(",");
        for (String s : tagsArray) {
            tags.add(s.trim());
        }
	}

    /**
     * Gets the tags in XML format.
     * @return The tags in XML format.
     */
	protected String getTagsXml() {
		StringBuilder buf = new StringBuilder();
		if (!tags.isEmpty()) {
			buf.append("<Tags>");
			for (Iterator<String> i = tags.iterator(); i.hasNext();) {
				buf.append(i.next());
				if (i.hasNext())
					buf.append(",");
			}
			buf.append("</Tags>");
		}
		return buf.toString();
	}

    /**
     * Gets the registration expiration time.
     * @return The registration expiration time.
     */
	public Date getExpirationTime() {
		return expirationTime;
	}

    /**
     * Sets the expiration time for the registration.
     * @param expirationTime The expiration time for the registration
     */
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

    /**
     * Sets the registration expiration from a string format.
     * @param expirationTimeString The expiration date in string format.
     * @throws ParseException Thrown if the string format is an invalid date.
     */
	public void setExpirationTimeFromString(String expirationTimeString) throws ParseException {
		this.expirationTime = javax.xml.bind.DatatypeConverter.parseDateTime(expirationTimeString).getTime();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((etag == null) ? 0 : etag.hashCode());
		result = prime * result
				+ ((expirationTime == null) ? 0 : expirationTime.hashCode());
		result = prime * result
				+ ((registrationId == null) ? 0 : registrationId.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Registration other = (Registration) obj;
		if (etag == null) {
			if (other.etag != null)
				return false;
		} else if (!etag.equals(other.etag))
			return false;
		if (expirationTime == null) {
			if (other.expirationTime != null)
				return false;
		} else if (!expirationTime.equals(other.expirationTime))
			return false;
		if (registrationId == null) {
			if (other.registrationId != null)
				return false;
		} else if (!registrationId.equals(other.registrationId))
			return false;
		if (tags == null) {
            return other.tags == null;
		} else return tags.equals(other.tags);
    }

    /**
     * Parses a registration from an InputStream
     * @param content The stream for reading the registration
     * @return A parsed registration from the stream.
     * @throws IOException If the reader has an exception.
     * @throws SAXException If the XML Parser has an issue.
     */
	public static Registration parse(InputStream content) throws IOException,
			SAXException {
		return singleRegParser.get().parse(content);
	}

    /**
     * Adds registration rules.
     * @param digester The digester for the registration rules.
     */
	private static void addRegistrationRules(Digester digester) {
		digester.addFactoryCreate("*/RegistrationDescription",
				new Registration.RegistrationCreationFactory());
		digester.addObjectCreate("*/WindowsRegistrationDescription",
				WindowsRegistration.class);
		digester.addObjectCreate("*/WindowsTemplateRegistrationDescription",
				WindowsTemplateRegistration.class);
		digester.addObjectCreate("*/AppleRegistrationDescription",
				AppleRegistration.class);
		digester.addObjectCreate("*/AppleTemplateRegistrationDescription",
				AppleTemplateRegistration.class);
		digester.addObjectCreate("*/GcmRegistrationDescription",
				GcmRegistration.class);
		digester.addObjectCreate("*/GcmTemplateRegistrationDescription",
				GcmTemplateRegistration.class);
		digester.addObjectCreate("*/FcmRegistrationDescription",
				FcmRegistration.class);
		digester.addObjectCreate("*/FcmTemplateRegistrationDescription",
				FcmTemplateRegistration.class);
		digester.addObjectCreate("*/MpnsRegistrationDescription",
				MpnsRegistration.class);
		digester.addObjectCreate("*/MpnsTemplateRegistrationDescription",
				MpnsTemplateRegistration.class);
		digester.addObjectCreate("*/AdmRegistrationDescription",
				AdmRegistration.class);
		digester.addObjectCreate("*/AdmTemplateRegistrationDescription",
				AdmTemplateRegistration.class);
		digester.addObjectCreate("*/BaiduRegistrationDescription",
				BaiduRegistration.class);
		digester.addObjectCreate("*/BaiduTemplateRegistrationDescription",
				BaiduTemplateRegistration.class);
		digester.addCallMethod("*/RegistrationId", "setRegistrationId", 1);
		digester.addCallParam("*/RegistrationId", 0);
		digester.addCallMethod("*/ETag", "setEtag", 1);
		digester.addCallParam("*/ETag", 0);
		digester.addCallMethod("*/ChannelUri", "setChannelUri", 1);
		digester.addCallParam("*/ChannelUri", 0);
		digester.addCallMethod("*/DeviceToken", "setDeviceToken", 1);
		digester.addCallParam("*/DeviceToken", 0);
		digester.addCallMethod("*/GcmRegistrationId", "setGcmRegistrationId", 1);
		digester.addCallParam("*/GcmRegistrationId", 0);
		digester.addCallMethod("*/FcmRegistrationId", "setFcmRegistrationId", 1);
		digester.addCallParam("*/FcmRegistrationId", 0);
		digester.addCallMethod("*/Tags", "setTagsFromString", 1);
		digester.addCallParam("*/Tags", 0);
		digester.addCallMethod("*/BodyTemplate", "setBodyTemplate", 1);
		digester.addCallParam("*/BodyTemplate", 0);
		digester.addCallMethod("*/WnsHeader", "addHeader", 2);
		digester.addCallParam("*/WnsHeader/Header", 0);
		digester.addCallParam("*/WnsHeader/Value", 1);
		digester.addCallMethod("*/MpnsHeader", "addHeader", 2);
		digester.addCallParam("*/MpnsHeader/Header", 0);
		digester.addCallParam("*/MpnsHeader/Value", 1);
		digester.addCallMethod("*/Expiry", "setExpiry", 1);
		digester.addCallParam("*/Expiry", 0);
		digester.addCallMethod("*/ExpirationTime", "setExpirationTimeFromString", 1);
		digester.addCallParam("*/ExpirationTime", 0);
		digester.addCallMethod("*/AdmRegistrationId", "setAdmRegistrationId", 1);
		digester.addCallParam("*/AdmRegistrationId", 0);
		digester.addCallMethod("*/BaiduUserId", "setBaiduUserId", 1);
		digester.addCallParam("*/BaiduUserId", 0);
		digester.addCallMethod("*/BaiduChannelId", "setBaiduChannelId", 1);
		digester.addCallParam("*/BaiduChannelId", 0);
	}

    /**
     * Parses registrations from an input stream
     * @param content The input stream containing the registrations
     * @return A collection of registrations.
     * @throws IOException If the reader has an exception
     * @throws SAXException If the XML Parser has an exception.
     */
	public static CollectionResult parseRegistrations(InputStream content)
			throws IOException, SAXException {
		return multiRegParser.get().parse(content);
	}

	private static void addCollectionRules(Digester digester) {
		digester.addObjectCreate("feed", CollectionResult.class);
		digester.addSetNext("*/RegistrationDescription", "addRegistration");
		digester.addSetNext("*/WindowsRegistrationDescription", "addRegistration");
		digester.addSetNext("*/WindowsTemplateRegistrationDescription", "addRegistration");
		digester.addSetNext("*/AppleRegistrationDescription", "addRegistration");
		digester.addSetNext("*/AppleTemplateRegistrationDescription", "addRegistration");
		digester.addSetNext("*/GcmRegistrationDescription", "addRegistration");
		digester.addSetNext("*/GcmTemplateRegistrationDescription", "addRegistration");
		digester.addSetNext("*/FcmRegistrationDescription", "addRegistration");
		digester.addSetNext("*/FcmTemplateRegistrationDescription", "addRegistration");
		digester.addSetNext("*/MpnsRegistrationDescription", "addRegistration");
		digester.addSetNext("*/MpnsTemplateRegistrationDescription", "addRegistration");
		digester.addSetNext("*/AdmRegistrationDescription", "addRegistration");
		digester.addSetNext("*/AdmTemplateRegistrationDescription", "addRegistration");
		digester.addSetNext("*/BaiduRegistrationDescription", "addRegistration");
		digester.addSetNext("*/BaiduTemplateRegistrationDescription", "addRegistration");
	}

	public static class RegistrationCreationFactory implements
			ObjectCreationFactory<Object> {
		private Digester digester;

		@Override
		public Object createObject(Attributes attributes) {
			if ("WindowsRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new WindowsRegistration();
			}
			if ("WindowsTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new WindowsTemplateRegistration();
			}
			if ("AppleRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new AppleRegistration();
			}
			if ("AppleTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new AppleTemplateRegistration();
			}
			if ("GcmRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new GcmRegistration();
			}
			if ("GcmTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new GcmTemplateRegistration();
			}
			if ("FcmRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new FcmRegistration();
			}
			if ("FcmTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new FcmTemplateRegistration();
			}
			if ("MpnsRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new MpnsRegistration();
			}
			if ("MpnsTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new MpnsTemplateRegistration();
			}
			if ("AdmRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new AdmRegistration();
			}
			if ("AdmTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new AdmTemplateRegistration();
			}
			if ("BaiduRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new BaiduRegistration();
			}
			if ("BaiduTemplateRegistrationDescription".equals(attributes
					.getValue("i:type"))) {
				return new BaiduTemplateRegistration();
			}
			throw new UnsupportedOperationException("unknown type: "
					+ attributes.getValue("i:type"));
		}

		@Override
		public Digester getDigester() {
			return digester;
		}

		@Override
		public void setDigester(Digester digester) {
			this.digester = digester;
		}
	}
}
