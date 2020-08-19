//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.annotations.SerializedName;

/**
 * The update operation types.
 */
public enum UpdateOperationType {
    /**
     * Add operation
     */
	@SerializedName("add")
	Add,
    /**
     * Remove operation
     */
	@SerializedName("remove")
	Remove,
    /**
     * Replace operation.
     */
	@SerializedName("replace")
	Replace,
}
