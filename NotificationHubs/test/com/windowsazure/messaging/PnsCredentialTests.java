package com.windowsazure.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.junit.Assert;
import org.junit.Test;

public class PnsCredentialTests {
	@Test
    public void DoesNotIncludeNullCertApnsCredential() {
		String token = "token";
		String keyId = "keyId";
		String appId = "appId";
		String appName = "appName";
		ApnsCredential cred = new ApnsCredential(token, keyId, appId, appName, ApnsCredential.APNS2_DEV_ENDPOINT);
		List<SimpleEntry<String, String>> result = cred.getProperties();
		
		ArrayList<SimpleEntry<String, String>> expected = new ArrayList<>();
		expected.add(new SimpleEntry<>("Token", token));
		expected.add(new SimpleEntry<>("KeyId", keyId));
		expected.add(new SimpleEntry<>("AppName", appName));
		expected.add(new SimpleEntry<>("AppId", appId));
		expected.add(new SimpleEntry<>("Endpoint", ApnsCredential.APNS2_DEV_ENDPOINT));
		
		Assert.assertEquals(expected, result);
    }
	
	@Test
    public void DoesNotIncludeNullTokenApnsCredential() {
		String cert = "cert";
		String certKey = "certKey";
		ApnsCredential cred = new ApnsCredential(cert, certKey, ApnsCredential.APNS2_DEV_ENDPOINT);
		List<SimpleEntry<String, String>> result = cred.getProperties();
		
		ArrayList<SimpleEntry<String, String>> expected = new ArrayList<>();
		expected.add(new SimpleEntry<>("ApnsCertificate", cert));
		expected.add(new SimpleEntry<>("CertificateKey", certKey));
		expected.add(new SimpleEntry<>("Thumbprint", null));
		expected.add(new SimpleEntry<>("Endpoint", ApnsCredential.APNS2_DEV_ENDPOINT));
		
		Assert.assertEquals(expected, result);
    }
}
