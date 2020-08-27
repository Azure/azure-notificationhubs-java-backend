//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * This class represents a Notification Hub description.
 */
public class NotificationHubDescription {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><NotificationHubDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String XML_FOOTER = "</NotificationHubDescription></content></entry>";
    private String path;
    private AdmCredential admCredential;
    private ApnsCredential apnsCredential;
    private WindowsCredential windowsCredential;
    private MpnsCredential mpnsCredential;
    private GcmCredential gcmCredential;
    private FcmCredential fcmCredential;
    private BaiduCredential baiduCredential;

    private static final AtomicReference<Digester> singleEntryParser = new AtomicReference<>();
    private static final AtomicReference<Digester> collectionParser = new AtomicReference<>();

    static {
        singleEntryParser.set(new Digester());
        setupSingleEntryParser(singleEntryParser.get());

        collectionParser.set(new Digester());
        setupCollectionParser(collectionParser.get());
    }

    /**
     * Creates a new notification hub description.
     */
    public NotificationHubDescription() {
        this(null);
    }

    /**
     * Creates a new notification hub description with path.
     *
     * @param path The notification hub path.
     */
    public NotificationHubDescription(String path) {
        super();
        this.path = path;
    }

    /**
     * Gets the notification hub path.
     *
     * @return The notification hub path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the notification hub path.
     *
     * @param path The notification hub path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the ADM credentials for the notification hub.
     *
     * @return The ADM credentials for the notification hub.
     */
    public AdmCredential getAdmCredential() {
        return admCredential;
    }

    /**
     * Sets the ADM credentials for the notification hub.
     *
     * @param admCredential The ADM credentials for the notification hub.
     */
    public void setAdmCredential(AdmCredential admCredential) {
        this.admCredential = admCredential;
    }

    /**
     * Gets the APNS credentials for the notification hub.
     *
     * @return The APNS credentials for the notification hub.
     */
    public ApnsCredential getApnsCredential() {
        return apnsCredential;
    }

    /**
     * Sets the APNS credentials for the notification hub.
     *
     * @param apnsCredential The APNS credentials for the notification hub.
     */
    public void setApnsCredential(ApnsCredential apnsCredential) {
        this.apnsCredential = apnsCredential;
    }

    /**
     * Gets the WNS credentials for the notification hub.
     *
     * @return THe WNS credentials for the notification hub.
     */
    public WindowsCredential getWindowsCredential() {
        return windowsCredential;
    }

    /**
     * Sets the WNS credentials for the notification hub.
     *
     * @param windowsCredential The WNS credentials for the notification hub.
     */
    public void setWindowsCredential(WindowsCredential windowsCredential) {
        this.windowsCredential = windowsCredential;
    }

    /**
     * Gets the Windows Phone credentials for the notification hub.
     *
     * @return The Windows Phone credentials for the notification hub.
     */
    public MpnsCredential getMpnsCredential() {
        return mpnsCredential;
    }

    /**
     * Sets the Windows Phone credentials for the notification hub.
     *
     * @param mpnsCredential The Windows Phone credentials for the notification hub.
     */
    public void setMpnsCredential(MpnsCredential mpnsCredential) {
        this.mpnsCredential = mpnsCredential;
    }

    /**
     * Gets the GCM credentials for the notification hub.
     *
     * @return The GCM credentials for the notification hub.
     */
    public GcmCredential getGcmCredential() {
        return gcmCredential;
    }

    /**
     * Sets the GCM credentials for the notification hub
     *
     * @param gcmCredential The GCM credentials for the notification hub.
     */
    public void setGcmCredential(GcmCredential gcmCredential) {
        this.gcmCredential = gcmCredential;
    }

    /**
     * Gets the FCM credentials for the notification hub.
     *
     * @return The FCM credentials for the notification hub.
     */
    public FcmCredential getFcmCredential() {
        return fcmCredential;
    }

    /**
     * Sets the FCM credentials for the notification hub.
     *
     * @param fcmCredential The FCM credentials for the notification hub.
     */
    public void setFcmCredential(FcmCredential fcmCredential) {
        this.fcmCredential = fcmCredential;
    }

    /**
     * Gets the Baidu credentials for the notification hub.
     *
     * @return The Baidu credentials for the notification hub.
     */
    public BaiduCredential getBaiduCredential() {
        return baiduCredential;
    }

    /**
     * Sets the Baidu credentials for the notification hub.
     *
     * @param baiduCredential The Baidu credentials for the notification hub.
     */
    public void setBaiduCredential(BaiduCredential baiduCredential) {
        this.baiduCredential = baiduCredential;
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
        if (this.apnsCredential != null) buf.append(this.apnsCredential.getXml());
        if (this.windowsCredential != null) buf.append(this.windowsCredential.getXml());
        if (this.gcmCredential != null) buf.append(this.gcmCredential.getXml());
        if (this.fcmCredential != null) buf.append(this.fcmCredential.getXml());
        if (this.mpnsCredential != null) buf.append(this.mpnsCredential.getXml());
        if (this.admCredential != null) buf.append(this.admCredential.getXml());
        if (this.baiduCredential != null) buf.append(this.baiduCredential.getXml());
        buf.append(XML_FOOTER);
        return buf.toString();
    }

    private static void setupCollectionParser(Digester digester) {
        digester.addObjectCreate("*/feed", LinkedList.class);
        setupSingleEntryParser(digester);
        digester.addSetNext("*/entry", "add", NotificationHubDescription.class.getName());
    }

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
