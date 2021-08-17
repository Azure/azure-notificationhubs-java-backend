//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * This class represents a notification hub description.
 */
public class NotificationHubDescription {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><NotificationHubDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String XML_FOOTER = "</NotificationHubDescription></content></entry>";
    private String path;
    private AdmCredential admCredential;
    private ApnsCredential apnsCredential;
    private WindowsCredential windowsCredential;
    private MpnsCredential mpnsCredential;
    @SuppressWarnings("deprecation")
    private GcmCredential gcmCredential;
    private FcmCredential fcmCredential;
    private BaiduCredential baiduCredential;

    private static final ThreadLocal<Digester> singleEntryParser;
    private static final ThreadLocal<Digester> collectionParser;

    static {
        singleEntryParser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            setupSingleEntryParser(instance);
            return instance;
        });

        collectionParser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            setupCollectionParser(instance);
            return instance;
        });
    }

    /**
     * Creates a new instance of the NotificationHubDescription class.
     */
    public NotificationHubDescription() {
    }

    /**
     * Creates a new instance of the NotificationHubDescription class.
     * @param path The notification hub path.
     */
    public NotificationHubDescription(String path) { this.path = path; }

    /**
     * Gets the notification hub path.
     * @return The notification hub path.
     */
    public String getPath() { return path;  }

    /**
     * Sets the notification hub path.
     * @param value The notification hub path to set.
     */
    public void setPath(String value) { path = value; }

    /**
     * Gets the Amazon PNS credentials for the notification hub.
     * @return The Amazon PNS credentials for the notification hub.
     */
    public AdmCredential getAdmCredential() { return admCredential; }

    /**
     * Sets the Amazon PNS credentials for the notification hub.
     * @param value The Amazon PNS credentials for the notification hub to set.
     */
    public void setAdmCredential(AdmCredential value) { admCredential = value; }

    /**
     * Sets the Apple PNS credentials for the notification hub.
     * @return The Apple PNS credentials for the notification hub.
     */
    public ApnsCredential getApnsCredential() { return apnsCredential; }

    /**
     * Sets the Apple PNS credentials for the notification hub.
     * @param value The Apple PNS credentials for the notification hub to set.
     */
    public void setApnsCredential(ApnsCredential value) { apnsCredential = value; }

    /**
     * Gets the Windows PNS credentials for the notification hub.
     * @return The Windows PNS credentials for the notification hub.
     */
    public WindowsCredential getWindowsCredential() { return windowsCredential; }

    /**
     * Sets the Windows PNS credentials for the notification hub.
     * @param value The Windows PNS credentials for the notification hub to set.
     */
    public void setWindowsCredential(WindowsCredential value) { windowsCredential = value; }

    /**
     * Gets the Windows Phone PNS credentials for the notification hub.
     * @return The Windows Phone PNS credentials for the notification hub.
     */
    public MpnsCredential getMpnsCredential() { return mpnsCredential; }

    /**
     * Sets the Windows Phone PNS credentials for the notification hub.
     * @param value The Windows Phone PNS credentials for the notification hub to set.
     */
    public void setMpnsCredential(MpnsCredential value) { mpnsCredential = value; }

    /**
     * Gets the Firebase PNS credentials for the notification hub.
     * @return The Firebase PNS credentials for the notification hub.
     */
    public FcmCredential getFcmCredential() { return fcmCredential; }

    /**
     * Sets the Firebase PNS credentials for the notification hub.
     * @param value The Firebase PNS credentials for the notification hub to set.
     */
    public void setFcmCredential(FcmCredential value) { fcmCredential = value; }

    /**
     * Gets the Baidu PNS credentials for the notification hub.
     * @return The Baidu PNS credentials for the notification hub.
     */
    public BaiduCredential getBaiduCredential() { return baiduCredential; }

    /**
     * Sets the Baidu PNS credentials for the notification hub.
     * @param value The Baidu PNS credentials for the notification hub to set.
     */
    public void setBaiduCredential(BaiduCredential value) { baiduCredential = value; }

    /**
     * Gets the Google Cloud Messaging PNS credentials for the notification hub.
     * @return The Google Cloud Messaging PNS credentials for the notification hub.
     * @deprecated GCM is deprecated.  Use getFcmCredential instead.
     */
    @Deprecated
    public GcmCredential getGcmCredential() { return gcmCredential; }

    /**
     * Sets the Google Cloud Messaging credentials for the notification hub.
     * @param value The Browser PNS credentials for the notification hub to set.
     * @deprecated Use GCM is deprecated, use setFcmCredential.
     */
    @Deprecated
    public void setGcmCredential(GcmCredential value) { gcmCredential = value; }

    public void setProperty(String propertyName, String propertyValue) throws Exception {
        this.getClass().getMethod("set" + propertyName, String.class).invoke(this, propertyValue);
    }

    public static NotificationHubDescription parseOne(InputStream content) throws IOException, SAXException {
        return singleEntryParser.get().parse(content);
    }

    public static List<NotificationHubDescription> parseCollection(InputStream content) throws IOException, SAXException {
        return collectionParser.get().parse(content);
    }

    public String getXml() {
        StringBuilder buf = new StringBuilder();
        buf.append(XML_HEADER);
        if (apnsCredential != null) buf.append(apnsCredential.getXml());
        if (windowsCredential != null) buf.append(windowsCredential.getXml());
        if (fcmCredential != null) buf.append(fcmCredential.getXml());
        if (gcmCredential != null) buf.append(gcmCredential.getXml());
        if (mpnsCredential != null) buf.append(mpnsCredential.getXml());
        if (admCredential != null) buf.append(admCredential.getXml());
        if (baiduCredential != null) buf.append(baiduCredential.getXml());
        buf.append(XML_FOOTER);
        return buf.toString();
    }

    private static void setupCollectionParser(Digester digester) {
        digester.addObjectCreate("*/feed", LinkedList.class);
        setupSingleEntryParser(digester);
        digester.addSetNext("*/entry", "add", NotificationHubDescription.class.getName());
    }

    @SuppressWarnings("deprecation")
    private static void setupSingleEntryParser(Digester digester) {
        digester.addObjectCreate("*/entry", NotificationHubDescription.class);
        digester.addCallMethod("*/entry/title", "setPath", 1);
        digester.addCallParam("*/entry/title", 0);
        digester.addObjectCreate("*/ApnsCredential", ApnsCredential.class);
        digester.addObjectCreate("*/AdmCredential", AdmCredential.class);
        digester.addObjectCreate("*/WnsCredential", WindowsCredential.class);
        digester.addObjectCreate("*/MpnsCredential", MpnsCredential.class);
        digester.addObjectCreate("*/GcmCredential", GcmCredential.class);
        digester.addObjectCreate("*/FcmCredential", FcmCredential.class);
        digester.addObjectCreate("*/BaiduCredential", BaiduCredential.class);
        PnsCredential.setupDigester(digester);
        digester.addSetNext("*/ApnsCredential", "setApnsCredential", ApnsCredential.class.getName());
        digester.addSetNext("*/AdmCredential", "setAdmCredential", AdmCredential.class.getName());
        digester.addSetNext("*/WnsCredential", "setWindowsCredential", WindowsCredential.class.getName());
        digester.addSetNext("*/MpnsCredential", "setMpnsCredential", MpnsCredential.class.getName());
        digester.addSetNext("*/GcmCredential", "setGcmCredential", GcmCredential.class.getName());
        digester.addSetNext("*/FcmCredential", "setFcmCredential", FcmCredential.class.getName());
        digester.addSetNext("*/BaiduCredential", "setBaiduCredential", BaiduCredential.class.getName());
    }
}
