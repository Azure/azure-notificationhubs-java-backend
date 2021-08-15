package com.windowsazure.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BaseInstallationFactory {

    /**
     * Creates an installation from the JSON string.
     *
     * @param jsonString The JSON string that represents the installation.
     * @return An installation created from the JSON string.
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseInstallation> T createInstallation(String jsonString) {

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseInstallation.class, new BaseInstallationDeserializer())
            .create();

        return (T)gson.fromJson(jsonString, BaseInstallation.class);
    }

    /**
     * Creates an installation from the JSON stream.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON stream.
     * @throws IOException An exception reading from the stream occurred.
     */
    public static <T extends BaseInstallation> T createInstallation(InputStream json) throws IOException {
        return createInstallation(IOUtils.toString(json, StandardCharsets.UTF_8));
    }
}
