package com.windowsazure.messaging;

import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class PartialUpdateOperation {
	@SerializedName("op")
	private UpdateOperationType operation;
	private String path;
	private String value;
	
	public PartialUpdateOperation(){
		this(null, null, null);
	}
	
	public PartialUpdateOperation(UpdateOperationType operation, String path){
		this(operation, path, null);
	}

	public PartialUpdateOperation(UpdateOperationType operation, String path, String value){
		this.operation = operation;
		this.path = path;
		this.value = value;
	}
	
	public UpdateOperationType getOperation() {
		return operation;
	}
	
	public void setOperation(UpdateOperationType operation) {
		this.operation = operation;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}	
	
	public static String toJson(PartialUpdateOperation... operations){
		return new GsonBuilder().disableHtmlEscaping().create().toJson(operations);
	}
	
	public static String toJson(List<PartialUpdateOperation> operations){
		return new GsonBuilder().disableHtmlEscaping().create().toJson(operations);
	}
}
