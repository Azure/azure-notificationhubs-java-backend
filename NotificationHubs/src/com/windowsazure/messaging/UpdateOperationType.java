package com.windowsazure.messaging;

import com.google.gson.annotations.SerializedName;

public enum UpdateOperationType {
	@SerializedName("add")
	Add,
	@SerializedName("remove")
	Remove,
	@SerializedName("replace")
	Replace,
}
