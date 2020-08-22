//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents partial update operations for installations.
 */
public class PartialUpdateOperation {
    @SerializedName("op")
    private UpdateOperationType operation;
    private String path;
    private String value;

    /**
     * Creates a partial update operation.
     */
    public PartialUpdateOperation() {
        this(null, null, null);
    }

    /**
     * Creates a partial update operation with an operation and path to the property
     * to modify.
     *
     * @param operation The update operation type
     * @param path      The path to the property to patch.
     */
    public PartialUpdateOperation(UpdateOperationType operation, String path) {
        this(operation, path, null);
    }

    /**
     * Creates a partial update operation with an operation, a path to the property
     * to modify and the value.
     *
     * @param operation The update operation type
     * @param path      The path to the property to patch.
     * @param value     The value to put at the path.
     */
    public PartialUpdateOperation(UpdateOperationType operation, String path, String value) {
        this.operation = operation;
        this.path = path;
        this.value = value;
    }

    /**
     * Gets the update operation type.
     *
     * @return The update operation type.
     */
    public UpdateOperationType getOperation() {
        return operation;
    }

    /**
     * Sets the update operation type.
     *
     * @param operation The update operation type/
     */
    public void setOperation(UpdateOperationType operation) {
        this.operation = operation;
    }

    /**
     * Gets the path to a property to update.
     *
     * @return The path for the property to update.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path to the property to update.
     *
     * @param path The path to the property to update.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the value for the path.
     *
     * @return The value for the path.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value for the path.
     *
     * @param value The value for the path.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Creates a JSON string from the partial update operations.
     *
     * @param operations A list of partial update operations to turn into JSON.
     * @return The partial update operations as a JSON string.
     */
    public static String toJson(PartialUpdateOperation... operations) {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(operations);
    }

    /**
     * Creates a JSON string from the partial update operations.
     *
     * @param operations A list of partial update operations to turn into JSON.
     * @return The partial update operations as a JSON string.
     */
    public static String toJson(List<PartialUpdateOperation> operations) {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(operations);
    }
}
