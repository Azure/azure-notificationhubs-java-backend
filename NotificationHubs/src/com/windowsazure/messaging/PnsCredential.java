//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import org.apache.commons.digester3.Digester;

/**
 * This class represents a platform notification service (PNS) credentials in Azure Notification Hubs.
 */
public abstract class PnsCredential {
    private static final String PROPERTIES_START = "<Properties>";
    private static final String PROPERTY_START = "<Property><Name>";
    private static final String PROPERTY_MIDDLE = "</Name><Value>";
    private static final String PROPERTY_END = "</Value></Property>";
    private static final String PROPERTIES_END = "</Properties>";

    /**
     * Attempts to call the setter for propertyName of this PNS credential.
     * @param propertyName The property name.
     * @param propertyValue The property value.
     * 
     * @throws Exception If invoking the setter fails.
     */
    public void setProperty(String propertyName, String propertyValue) throws Exception {
    	var setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        this.getClass().getMethod(setterName, String.class).invoke(this, propertyValue);
    }

    /**
     * Configures the Digester for a PNS credential.
     * @param digester The Digester to set up.
     */
    public static void setupDigester(Digester digester) {
        digester.addCallMethod("*/Property", "setProperty", 2);
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Value", 1);
    }

    /**
     * Gets the XML string representation of this PNS credential.
     * @return The XML string representation of this PNS credential.
     */
    public String getXml() {
        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(getRootTagName());
        buf.append(">");
        buf.append(PROPERTIES_START);
        for (SimpleEntry<String, String> property : getProperties()) {
            buf.append(PROPERTY_START);
            buf.append(property.getKey());
            buf.append(PROPERTY_MIDDLE);
            buf.append(property.getValue());
            buf.append(PROPERTY_END);
        }
        buf.append(PROPERTIES_END);
        buf.append("</");
        buf.append(getRootTagName());
        buf.append(">");
        return buf.toString();
    }

    /**
     * Gets the list of the name-value pairs of this PNS credential.
     * @return The list of the name-value pairs of this PNS credential.
     */
    public abstract List<SimpleEntry<String, String>> getProperties();

    /**
     * Gets the root XML tag name of this PNS credential.
     * @return The root XML tag name of this PNS credential.
     */
    public abstract String getRootTagName();
}
