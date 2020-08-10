//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;
import static org.junit.Assert.*;
import org.junit.Test;

public class WindowsNotificationTest {
	private static final String ToastWnsBody = "<toast><visual><binding template=\"ToastText01\"><text id=\"1\">From any .NET App!</text></binding></visual></toast>";
	private static final String AttrToastWnsBody = "<toast launch=\"test\"><visual><binding template=\"ToastText01\"><text id=\"1\">From any .NET App! Second take!</text></binding></visual></toast>";
	private static final String TileWnsBody = "<tile><visual><binding template=\"TileText01\"><text id=\"1\">From any .NET App!</text></binding></visual></tile>";
	private static final String BadgeWnsBody = "<badge><visual><binding template=\"BadgeText01\"><text id=\"1\">From any .NET App!</text></binding></visual></badge>";

	@Test
	public void CreateToastWnsNotificationTest() {
		Notification n = Notification.createWindowsNotification(ToastWnsBody);

		assertEquals(ContentType.APPLICATION_XML, n.contentType);
		assertEquals("wns/toast", n.headers.get("X-WNS-Type"));
		assertEquals("windows", n.headers.get("ServiceBusNotification-Format"));
		assertEquals(ToastWnsBody, n.body);
	}

	@Test
	public void CreateAttrToastWnsNotificationTest() {
		Notification n = Notification.createWindowsNotification(AttrToastWnsBody);

		assertEquals(ContentType.APPLICATION_XML, n.contentType);
		assertEquals("wns/toast", n.headers.get("X-WNS-Type"));
		assertEquals("windows", n.headers.get("ServiceBusNotification-Format"));
		assertEquals(AttrToastWnsBody, n.body);
	}

	@Test
	public void CreateTileWnsNotificationTest() {
		Notification n = Notification.createWindowsNotification(TileWnsBody);

		assertEquals(ContentType.APPLICATION_XML, n.contentType);
		assertEquals("wns/tile", n.headers.get("X-WNS-Type"));
		assertEquals("windows", n.headers.get("ServiceBusNotification-Format"));
		assertEquals(TileWnsBody, n.body);
	}

	@Test
	public void CreateBadgeWnsNotificationTest() {
		Notification n = Notification.createWindowsNotification(BadgeWnsBody);

		assertEquals(ContentType.APPLICATION_XML, n.contentType);
		assertEquals("wns/badge", n.headers.get("X-WNS-Type"));
		assertEquals("windows", n.headers.get("ServiceBusNotification-Format"));
		assertEquals(BadgeWnsBody, n.body);
	}
}
