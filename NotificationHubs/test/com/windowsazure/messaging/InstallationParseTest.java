//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InstallationParseTest {

    @Test
    public void InstallationMinimal() throws IOException {
        InputStream inputJson = this.getClass().getResourceAsStream("InstallationMinimal");
        Installation installation = Installation.fromJson(inputJson);
        assertNotNull(installation);
        assertEquals("123", installation.getInstallationId());
        assertEquals(NotificationPlatform.Gcm, installation.getPlatform());
        assertEquals("qwe", installation.getPushChannel());
    }

    @Test
    public void InstallationWnsFactory() throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        InputStream inputJson = this.getClass().getResourceAsStream("InstallationWnsFull");
        WindowsInstallation installation = BaseInstallation.fromJson(inputJson);
        assertNotNull(installation);
        assertEquals("123", installation.getInstallationId());
        assertEquals(NotificationPlatform.Wns, installation.getPlatform());
        assertEquals("wns-push-channel1", installation.getPushChannel());
        assertNotNull(installation.getTemplates());
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>", installation.getTemplates().get("template1").getBody());
        Date expiration = installation.getExpirationTime();
        assertEquals(expiration.toString(), sdf.parse("Wed Nov 26 15:34:01 PST 2014").toString());
    }

    @Test
    public void InstallationWnsFull() throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        InputStream inputJson = this.getClass().getResourceAsStream("InstallationWnsFull");
        WindowsInstallation installation = BaseInstallation.fromJson(inputJson);
        assertNotNull(installation);
        assertEquals("123", installation.getInstallationId());
        assertEquals(NotificationPlatform.Wns, installation.getPlatform());
        assertEquals("wns-push-channel1", installation.getPushChannel());
        assertNotNull(installation.getTemplates());
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>", installation.getTemplates().get("template1").getBody());
        Date expiration = installation.getExpirationTime();
        assertEquals(expiration.toString(), sdf.parse("Wed Nov 26 15:34:01 PST 2014").toString());
    }

    @Test
    public void InstallationBrowser() throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        InputStream inputJson = this.getClass().getResourceAsStream("InstallationBrowser");
        BrowserInstallation installation = BaseInstallation.fromJson(inputJson);
        assertNotNull(installation);
        assertEquals("123", installation.getInstallationId());
        assertEquals(NotificationPlatform.Browser, installation.getPlatform());
        assertEquals("{\"endpoint\":\"foo\",\"p256dh\":\"bar\",\"auth\":\"baz\"}", installation.getPushChannel());
        assertNotNull(installation.getTemplates());
        assertEquals("{\"title\": \"$(message)\", \"message\": \"$(message)\"}", installation.getTemplates().get("template1").getBody());
        Date expiration = installation.getExpirationTime();
        assertEquals(expiration.toString(), sdf.parse("Wed Nov 26 15:34:01 PST 2024").toString());
    }

    @Test
    public void PartialUpdates() throws IOException {
        PartialUpdateOperation add = new PartialUpdateOperation(UpdateOperationType.Add, "/templates/template1", new InstallationTemplate("{\"data\":{\"key1\":\"value\"}}").toJson());
        PartialUpdateOperation remove = new PartialUpdateOperation(UpdateOperationType.Remove, "/remove/path");
        PartialUpdateOperation replace = new PartialUpdateOperation(UpdateOperationType.Replace, "/replace/path", "replace-value");

        String expectedResultJson = IOUtils.toString(this.getClass().getResourceAsStream("PartialUpdatesNoSpaces"), StandardCharsets.UTF_8);
        String actualResultJson = PartialUpdateOperation.toJson(add, remove, replace);
        assertEquals(expectedResultJson, actualResultJson);
    }

}
