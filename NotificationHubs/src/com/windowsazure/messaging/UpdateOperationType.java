//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.annotations.SerializedName;

/**
 * This enum represents the possible update operation types.
 */
public enum UpdateOperationType {
    /**
     * The add update operation type.
     */
    @SerializedName("add")
    Add,
    /**
     * The remove update operation type.
     */
    @SerializedName("remove")
    Remove,
    /**
     * The replace update operation type.
     */
    @SerializedName("replace")
    Replace
}
