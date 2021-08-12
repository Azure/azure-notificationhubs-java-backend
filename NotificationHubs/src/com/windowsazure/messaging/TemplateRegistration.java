package com.windowsazure.messaging;

/**
 * This interface represents a template registration.
 */
public interface TemplateRegistration {
    /**
     * Gets the registration template body.
     * @return The registration template body.
     */
    String getBodyTemplate();

    /**
     * Sets the registration template body.
     * @param value The registration template body to set.
     */
    void setBodyTemplate(String value);
}
