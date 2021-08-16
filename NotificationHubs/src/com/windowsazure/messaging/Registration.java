//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreationFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Abstract class representing a registration.
 */
public abstract class Registration implements Cloneable {
    protected String registrationId;
    protected Set<String> tags = new HashSet<>();
    protected String etag;
    protected Date expirationTime;

    private static final ThreadLocal<Digester> singleRegParser;
    private static final ThreadLocal<Digester> multiRegParser;

    static {
        singleRegParser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            addRegistrationRules(instance);
            return instance;
        });

        multiRegParser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            addRegistrationRules(instance);
            addCollectionRules(instance);
            return instance;
        });
    }

    /**
     * Creates a new instance of the Registration class.
     */
    public Registration() {
    }

    /**
     * Clones the current registration.
     * @return A clone of the current registration.
     */
    public Registration clone() {
        try {
            return (Registration) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new instance of the Registration class with the registration ID.
     * @param registrationId The registration ID.
     */
    public Registration(String registrationId) {
        super();
        this.registrationId = registrationId;
    }

    /**
     * Creates a new instance of the Registration class with a set of tags.
     * @param tags The tags for the registration.
     */
    public Registration(Set<String> tags) {
        super();
        this.tags = tags;
    }

    /**
     * Gets the registration ID.
     * @return The registration ID.
     */
    public String getRegistrationId() { return registrationId; }

    /**
     * Sets the registration ID.
     * @param value The registration ID to set.
     */
    public void setRegistrationId(String value) { registrationId = value; }

    /**
     * Gets the tags for the registration.
     * @return The tags for the registration.
     */
    public Set<String> getTags() { return tags; }

    /**
     * Sets the tags for the registration.
     * @param value The tags for the registration to set.
     */
    public void setTags(Set<String> value) { this.tags = value; }

    /**
     * Sets the tags from a comma separated string.
     * @param string The comma separated string containing the tags.
     */
    public void setTagsFromString(String string) {
        tags = new HashSet<>();
        String[] tagsArray = string.split(",");
        for (String s : tagsArray) {
            tags.add(s.trim());
        }
    }

    /**
     * Gets the etag for the registration.
     * @return The etag for the registration.
     */
    public String getEtag() { return etag; }

    /**
     * Sets the etag for the registration.
     * @param value The etag for the registration to set.
     */
    public void setEtag(String value) { etag = value; }

    /**
     * Gets the expiration time for the registration.
     * @return The expiration time for the registration.
     */
    public Date getExpirationTime() { return expirationTime; }

    /**
     * Sets the expiration time for the registration.
     * @param value The expiration time for the registration.
     */
    public void setExpirationTime(Date value) { expirationTime = value; }

    /**
     * Sets the expiration time for the registration from a string value.
     * @param expirationTimeString The expiration time for the registration string to be parsed.
     */
    public void setExpirationTimeFromString(String expirationTimeString) {
        this.expirationTime = javax.xml.bind.DatatypeConverter.parseDateTime(expirationTimeString).getTime();
    }

    /**
     * Gets the PNS handle for getting devices by channel.
     * @return The PNS handle for getting devices by channel.
     */
    public abstract String getPnsHandle();

    /**
     * Gets an XML representation of the current object.
     * @return The XML representation of the current object.
     */
    public abstract String getXml();

    protected String getTagsXml() {
        StringBuilder buf = new StringBuilder();
        if (!tags.isEmpty()) {
            buf.append("<Tags>");
            for (Iterator<String> i = tags.iterator(); i.hasNext(); ) {
                buf.append(i.next());
                if (i.hasNext())
                    buf.append(",");
            }
            buf.append("</Tags>");
        }
        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(getRegistrationId(), that.getRegistrationId()) && Objects.equals(getTags(), that.getTags()) && Objects.equals(getEtag(), that.getEtag()) && Objects.equals(getExpirationTime(), that.getExpirationTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistrationId(), getTags(), getEtag(), getExpirationTime());
    }

    public static <T extends Registration> T parse(InputStream content) throws IOException,
        SAXException {
        return singleRegParser.get().parse(content);
    }

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
