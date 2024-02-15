//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * This class represents a serializer for installations from JSON.
 */
public class BaseInstallationDeserializer implements JsonDeserializer<BaseInstallation> {

    @Override
    public BaseInstallation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement jsonType = jsonObject.get("platform");
        String platformString = jsonType.getAsString();

        return jsonDeserializationContext.deserialize(jsonElement, getClass(platformString));
    }

    private static Class<?> getClass(String platformString) {
        switch (platformString) {
            case "adm":
                return AdmInstallation.class;
            case "apple":
                return AppleInstallation.class;
            case "baidu":
                return BaiduInstallation.class;
            case "fcm":
                return FcmInstallation.class;
            case "fcmv1":
                return FcmV1Installation.class;
            case "mpns":
                return MpnsInstallation.class;
            case "wns":
                return WindowsInstallation.class;
            default:
                return Installation.class;
        }
    }
}
