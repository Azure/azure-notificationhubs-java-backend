package com.windowsazure.messaging;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LowerCaseEnumDeserializer implements JsonDeserializer<Enum> {
    @Override
    public Enum deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {
        try {
            if(type instanceof Class && ((Class<?>) type).isEnum()) {
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(json.getAsString().toLowerCase(), type);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
