package com.windowsazure.messaging;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.entity.ContentType;

/**
 * 
 * Class representing a generic notification.
 * 
 */
public class Notification {

	private Map<String, String> headers = new HashMap<String, String>();
	private String body;
	private ContentType contentType;

	/**
	 * Utility method to set up a native notification for WNS. Sets the
	 * X-WNS-Type headers based on the body provided. If you want to send raw
	 * notifications you have to set the X-WNS header and ContentType after creating this
	 * notification or use createWindowsRawNotification method
	 * 
	 * @param body
	 * @return
	 */
	public static Notification createWindowsNotification(String body) {
		Notification n = new Notification();
		n.body = body;

		n.headers.put("ServiceBusNotification-Format", "windows");

		if (body.contains("<toast>"))
			n.headers.put("X-WNS-Type", "wns/toast");
		if (body.contains("<tile>"))
			n.headers.put("X-WNS-Type", "wns/tile");
		if (body.contains("<badge>"))
			n.headers.put("X-WNS-Type", "wns/badge");

		if (body.startsWith("<")) {
			n.contentType = ContentType.APPLICATION_XML;
		}

		return n;
	}
	
	/**
	 * Utility method to set up a native notification for WNS. Sets the
	 * X-WNS-Type header to "wns/raw" in order of sending of raw notification.
	 * 
	 * @param body
	 * @return
	 */
	public static Notification createWindowsRawNotification(String body) {
		Notification n = new Notification();
		n.body = body;
		n.headers.put("ServiceBusNotification-Format", "windows");
		n.headers.put("X-WNS-Type", "wns/raw");
		n.contentType = ContentType.APPLICATION_OCTET_STREAM;
		return n;
	}

    /**
     * Utility method to set up a native notification for APNs.
     * An expiry Date of 1 day is set by default.
     * @param body
     * @return
     */
    public static Notification createAppleNotifiation(String body) {

        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        return createAppleNotification(body, tomorrow);

    }

    /**
     * Utility method to set up a native notification for APNs.
     * Enables to set the expiry date of the notification for the APNs QoS.
     * @param body
     * @param expiry - the expiration date of this notification.
     *               a null value will be interpreted as 0 seconds.
     * @return
     */
    public static Notification createAppleNotification(String body, Date expiry) {
        Notification n = new Notification();
        n.body = body;
        n.contentType = ContentType.APPLICATION_JSON;

        n.headers.put("ServiceBusNotification-Format", "apple");

        if(expiry != null){
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String expiryString = formatter.format(expiry.getTime());

            n.headers.put("ServiceBusNotification-Apns-Expiry", expiryString);
        }

        return n;
    }

	/**
	 * Utility method to set up a native notification for GCM.
	 * @deprecated use {@link #createFcmNotifiation()} instead.
	 * 
	 * @param body
	 * @return
	 */
	@Deprecated
	public static Notification createGcmNotifiation(String body) {
		Notification n = new Notification();
		n.body = body;
		n.contentType = ContentType.APPLICATION_JSON;

		n.headers.put("ServiceBusNotification-Format", "gcm");

		return n;
	}

	/**
	 * Utility method to set up a native notification for FCM.
	 * 
	 * @param body
	 * @return
	 */
	public static Notification createFcmNotifiation(String body) {
		Notification n = new Notification();
		n.body = body;
		n.contentType = ContentType.APPLICATION_JSON;

		n.headers.put("ServiceBusNotification-Format", "fcm");

		return n;
	}
	
	/**
	 * Utility method to set up a native notification for ADM.
	 * 
	 * @param body
	 * @return
	 */
	public static Notification createAdmNotifiation(String body) {
		Notification n = new Notification();
		n.body = body;
		n.contentType = ContentType.APPLICATION_JSON;

		n.headers.put("ServiceBusNotification-Format", "adm");

		return n;
	}
	
	/**
	 * Utility method to set up a native notification for Baidu PNS.
	 * 
	 * @param body
	 * @return
	 */
	public static Notification createBaiduNotifiation(String body) {
		Notification n = new Notification();
		n.body = body;
		n.contentType = ContentType.APPLICATION_JSON;

		n.headers.put("ServiceBusNotification-Format", "baidu");

		return n;
	}

	/**
	 * Utility method to set up a native notification for MPNS. Sets the
	 * X-WindowsPhone-Target and X-NotificationClass headers based on the body
	 * provided. Raw notifications are not supported for MPNS.
	 * 
	 * @param body
	 * @return
	 */
	public static Notification createMpnsNotifiation(String body) {
		Notification n = new Notification();
		n.body = body;

		n.headers.put("ServiceBusNotification-Format", "windowsphone");

		if (body.contains("<wp:Toast>")) {
			n.headers.put("X-WindowsPhone-Target", "toast");
			n.headers.put("X-NotificationClass", "2");
		}
		if (body.contains("<wp:Tile>")) {
			n.headers.put("X-WindowsPhone-Target", "tile");
			n.headers.put("X-NotificationClass", "1");
		}

		if (body.startsWith("<")) {
			n.contentType = ContentType.APPLICATION_XML;
		}

		return n;
	}

	/**
	 * Utility method to create a notification object representing a template notification.
	 * 
	 * @param properties
	 * @return
	 */
	public static Notification createTemplateNotification(
			Map<String, String> properties) {
		Notification n = new Notification();
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		for (Iterator<String> iterator = properties.keySet().iterator(); iterator
				.hasNext();) {
			String key = iterator.next();
			buf.append("\"" + key + "\":\"" + properties.get(key) + "\"");
			if (iterator.hasNext())
				buf.append(",");
		}
		buf.append("}");
		n.body = buf.toString();

		n.contentType = ContentType.APPLICATION_JSON;

		n.headers.put("ServiceBusNotification-Format", "template");

		return n;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

}
