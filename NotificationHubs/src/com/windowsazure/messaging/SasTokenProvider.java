//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * This class handles the creation of the SAS tokens.
 */
public class SasTokenProvider {

    private final String sasKeyName;
    private final String sasKeyValue;

    /**
     * Creates a new instance of the SasTokenProvider class.
     * @param sasKeyName The SAS key name from the access policy connection string.
     * @param sasKeyValue The SAS key value from the access policy connection string.
     */
    public SasTokenProvider(String sasKeyName, String sasKeyValue) {
        this.sasKeyName = sasKeyName;
        this.sasKeyValue = sasKeyValue;
    }

    /**
     * Creates a SAS token based upon the target URI and SAS key name and SAS key value.
     * @param uri The target URI for the SAS token.
     * @return The SAS token which is good for one hour.
     */
    public String generateSasToken(URI uri) {
        String targetUri;
        try {
            targetUri = URLEncoder
                .encode(uri.toString().toLowerCase(), "UTF-8")
                .toLowerCase();

            long expiresOnDate = System.currentTimeMillis();
            expiresOnDate += (long)SdkGlobalSettings.getAuthorizationTokenExpirationInMinutes() * 60 * 1000;
            long expires = expiresOnDate / 1000;
            String toSign = targetUri + "\n" + expires;

            // Get a hmac_sha1 key from the raw key bytes
            byte[] keyBytes = sasKeyValue.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA256");

            // Get a hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));

            // Convert raw bytes to Hex
            String signature = URLEncoder.encode(
                Base64.encodeBase64String(rawHmac), "UTF-8");

            // construct authorization string
            return "SharedAccessSignature sr=" + targetUri + "&sig="
                + signature + "&se=" + expires + "&skn=" + sasKeyName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
