package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

public class NotificationHubDescription {
	private static final String XML_HEADER="<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><NotificationHubDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">"; 
	private static final String XML_FOOTER="</NotificationHubDescription></content></entry>";
	private String path;	
	private AdmCredential admCredential;
	private ApnsCredential apnsCredential;
	private WindowsCredential windowsCredential;
	private MpnsCredential mpnsCredential;
	private GcmCredential gcmCredential;	
	private BaiduCredential baiduCredential;
	
	private static final ThreadLocal<Digester> singleEntryParser;
	private static final ThreadLocal<Digester> collectionParser;
	
	static {
		singleEntryParser = new ThreadLocal<Digester>(){
			@Override protected Digester initialValue() {
				Digester instance = new Digester();
				setupSingleEntryParser(instance);
                return instance;
             }
		};
		
		collectionParser = new ThreadLocal<Digester>(){
			@Override protected Digester initialValue() {
				Digester instance = new Digester();
				setupCollectionParser(instance);
                return instance;
             }
		};
	}	
	
	public NotificationHubDescription(){
		this(null);
	}
	
	public NotificationHubDescription(String path){
		super();
		this.path=path;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public AdmCredential getAdmCredential() {
		return admCredential;
	}

	public void setAdmCredential(AdmCredential admCredential) {
		this.admCredential = admCredential;
	}

	public ApnsCredential getApnsCredential() {
		return apnsCredential;
	}

	public void setApnsCredential(ApnsCredential apnsCredential) {
		this.apnsCredential = apnsCredential;
	}

	public WindowsCredential getWindowsCredential() {
		return windowsCredential;
	}

	public void setWindowsCredential(WindowsCredential windowsCredential) {
		this.windowsCredential = windowsCredential;
	}

	public MpnsCredential getMpnsCredential() {
		return mpnsCredential;
	}

	public void setMpnsCredential(MpnsCredential mpnsCredential) {
		this.mpnsCredential = mpnsCredential;
	}

	public GcmCredential getGcmCredential() {
		return gcmCredential;
	}

	public void setGcmCredential(GcmCredential gcmCredential) {
		this.gcmCredential = gcmCredential;
	}	
	
	public BaiduCredential getBaiduCredential() {
		return baiduCredential;
	}

	public void setBaiduCredential(BaiduCredential baiduCredential) {
		this.baiduCredential = baiduCredential;
	}	
		
	public static NotificationHubDescription parseOne(InputStream content) throws IOException,	SAXException {
		return singleEntryParser.get().parse(content);
	}
	
	public static List<NotificationHubDescription> parseCollection(InputStream content) throws IOException,	SAXException {
		return collectionParser.get().parse(content);
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		buf.append(XML_HEADER);
		if(this.apnsCredential!=null) buf.append(this.apnsCredential.getXml());
		if(this.windowsCredential!=null) buf.append(this.windowsCredential.getXml());
		if(this.gcmCredential!=null) buf.append(this.gcmCredential.getXml());
		if(this.mpnsCredential!=null) buf.append(this.mpnsCredential.getXml());
		if(this.admCredential!=null) buf.append(this.admCredential.getXml());
		if(this.baiduCredential!=null) buf.append(this.baiduCredential.getXml());
		buf.append(XML_FOOTER);
		return buf.toString();
	}
	
	private static void setupCollectionParser(Digester digester){	
		digester.addObjectCreate("*/feed", LinkedList.class);
		setupSingleEntryParser(digester);
		digester.addSetNext("*/entry", "add", NotificationHubDescription.class.getName());
	}
	
	private static void setupSingleEntryParser(Digester digester){
		digester.addObjectCreate("*/entry", NotificationHubDescription.class);
		digester.addCallMethod("*/entry/title","setPath",1);
		digester.addCallParam("*/entry/title",0);
		digester.addObjectCreate("*/ApnsCredential", ApnsCredential.class);
		digester.addObjectCreate("*/AdmCredential", AdmCredential.class);
		digester.addObjectCreate("*/WnsCredential", WindowsCredential.class);
		digester.addObjectCreate("*/MpnsCredential", MpnsCredential.class);
		digester.addObjectCreate("*/GcmCredential", GcmCredential.class);
		digester.addObjectCreate("*/BaiduCredential", BaiduCredential.class);
		PnsCredential.setupDigister(digester);
		digester.addSetNext("*/ApnsCredential", "setApnsCredential", ApnsCredential.class.getName());
		digester.addSetNext("*/AdmCredential", "setAdmCredential", AdmCredential.class.getName());
		digester.addSetNext("*/WnsCredential", "setWindowsCredential", WindowsCredential.class.getName());
		digester.addSetNext("*/MpnsCredential", "setMpnsCredential", MpnsCredential.class.getName());
		digester.addSetNext("*/GcmCredential", "setGcmCredential", GcmCredential.class.getName());
		digester.addSetNext("*/BaiduCredential", "setBaiduCredential", BaiduCredential.class.getName());
	}	
}
