package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

import java.util.Map;
import java.util.Iterator;

/**
 * This class represents a template notification.
 */
public class TemplateNotification extends Notification {

    /**
     * Creates a new instance of the TemplateNotification class.
     * @param properties The properties for the template notification.
     */
    public TemplateNotification(Map<String, String> properties) {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for (Iterator<String> iterator = properties.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            buf.append("\"").append(key).append("\":\"").append(properties.get(key)).append("\"");
            if (iterator.hasNext())
                buf.append(",");
        }
        buf.append("}");
        this.body = buf.toString();

        this.contentType = ContentType.APPLICATION_JSON;

        this.headers.put("ServiceBusNotification-Format", "template");
    }
}
