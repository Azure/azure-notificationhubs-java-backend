package com.windowsazure.messaging;

import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BaseInstallationDeserializer implements JsonDeserializer<BaseInstallation> {

    @Override
    public BaseInstallation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement jsonType = jsonObject.get("platform");
        String platformString = jsonType.getAsString();

        BaseInstallation installation;

        if (platformString.equalsIgnoreCase("browser")) {
            installation = jsonDeserializationContext.deserialize(jsonElement, BrowserInstallation.class);
        } else {
            installation = jsonDeserializationContext.deserialize(jsonElement, Installation.class);
        }

        return installation;
    }
}
