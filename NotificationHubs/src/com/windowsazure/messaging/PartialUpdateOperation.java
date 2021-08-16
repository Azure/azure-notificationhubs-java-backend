//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents a partial update for patch operations.
 */
public class PartialUpdateOperation {
    @SerializedName("op")
    private UpdateOperationType operation;
    private String path;
    private String value;

    /**
     * Creates a new instance of the PartialUpdateOperation class.
     */
    public PartialUpdateOperation() {

    }

    /**
     * Creates a new instance of the PartialUpdateOperation class with operation type and path.
     * @param operation The operation type.
     * @param path The path to perform the operation.
     */
    public PartialUpdateOperation(UpdateOperationType operation, String path) {
        this.operation = operation;
        this.path = path;
    }

    /**
     * Creates a new instance of the PartialUpdateOperation class with operation type, path and value.
     * @param operation The operation type.
     * @param path The path to perform the operation.
     * @param value The value to set for the operation.
     */
    public PartialUpdateOperation(UpdateOperationType operation, String path, String value) {
        this.operation = operation;
        this.path = path;
        this.value = value;
    }

    /**
     * Gets the partial update operation type.
     * @return The partial update operation type.
     */
    public UpdateOperationType getOperation() { return operation; }

    /**
     * Sets the partial update operation type.
     * @param value The partial update operation type to set.
     */
    public void setOperation(UpdateOperationType value) { operation = value; }

    /**
     * Gets the partial update operation path.
     * @return The partial update operation path.
     */
    public String getPath() { return path; }

    /**
     * Sets the partial update operation path.
     * @param value The partial update operation path.
     */
    public void setPath(String value) { path = value; }

    /**
     * Gets the partial update operation value.
     * @return The partial update operation value.
     */
    public String getValue() { return value; }

    /**
     * Sets the partiaul update operation value.
     * @param value The partial update operation value.
     */
    public void setValue(String value) { this.value = value; }

    public static String toJson(PartialUpdateOperation... operations) {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(operations);
    }

    public static String toJson(List<PartialUpdateOperation> operations) {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(operations);
    }
}
