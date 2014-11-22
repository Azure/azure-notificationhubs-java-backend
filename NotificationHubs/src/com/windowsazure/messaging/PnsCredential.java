package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import org.apache.commons.digester3.Digester;

public abstract class PnsCredential {	
	private static final String PROPERTIES_START="<Properties>";
	private static final String PROPERTY_START="<Property><Name>";
	private static final String PROPERTY_MIDDLE="</Name><Value>";
	private static final String PROPERTY_END="</Value></Property>";
	private static final String PROPERTIES_END="</Properties>";
	
    public static void setupDigister(Digester digester){
		digester.addCallMethod("*/Property", "setProperty",2);
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Value", 1);
	}
	
	public void setProperty(String propertyName, String propertyValue) throws Exception{
		this.getClass().getMethod("set"+propertyName, String.class).invoke(this, propertyValue);
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append("<");
		buf.append(getRootTagName());
		buf.append(">");
		buf.append(PROPERTIES_START);
		for (SimpleEntry<String,String> property : getProperties()) {
			buf.append(PROPERTY_START);
			buf.append(property.getKey());
			buf.append(PROPERTY_MIDDLE);
			buf.append(property.getValue());
			buf.append(PROPERTY_END);
		}
		buf.append(PROPERTIES_END);
		buf.append("</");
		buf.append(getRootTagName());
		buf.append(">");
		return buf.toString();
	}
	
	public abstract List<SimpleEntry<String,String>> getProperties();
	
	public abstract String getRootTagName();
}
