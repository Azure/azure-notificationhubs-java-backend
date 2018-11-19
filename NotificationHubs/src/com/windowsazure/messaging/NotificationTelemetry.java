package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

public class NotificationTelemetry {
	
	private String notificationId;
	private String location;
	private NotificationStatus notificationStatus;
	private Date enqueueTime;
	private Date startTime;
	private Date endTime;
	private String notificationBody;
	private String targetPlatforms;
	private Map<String, Integer> apnsOutcomeCounts;
	private Map<String, Integer> mpnsOutcomeCounts;
	private Map<String, Integer> wnsOutcomeCounts;
	private Map<String, Integer> gcmOutcomeCounts;
	private Map<String, Integer> fcmOutcomeCounts;
	private Map<String, Integer> admOutcomeCounts;
	private Map<String, Integer> baiduOutcomeCounts;
	private String pnsErrorDetailsUri;	
	
	private static final ThreadLocal<Digester> parser;
	
	static {
		parser = new ThreadLocal<Digester>(){
			@Override protected Digester initialValue() {
				Digester instance = new Digester();
				setupParser(instance);
                return instance;
             }
		};		
	}	
	
	public static NotificationTelemetry parseOne(InputStream content) throws IOException,	SAXException {
		return parser.get().parse(content);
	}
	
	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public NotificationStatus getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatusFromString(String status) {
		this.notificationStatus = Enum.valueOf(NotificationStatus.class, status);
	}
	
	public void setNotificationStatus(NotificationStatus notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public Date getEnqueueTime() {
		return enqueueTime;
	}
	
	public void setEnqueueTimeFromString(String enqueueTime) {
		this.enqueueTime = javax.xml.bind.DatatypeConverter.parseDateTime(enqueueTime).getTime();
	}	

	public void setEnqueueTime(Date enqueueTime) {
		this.enqueueTime = enqueueTime;
	}

	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTimeFromString(String startTime) {
		this.startTime = javax.xml.bind.DatatypeConverter.parseDateTime(startTime).getTime();
	}	

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTimeFromString(String endTime) {
		this.endTime = javax.xml.bind.DatatypeConverter.parseDateTime(endTime).getTime();
	}	

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getNotificationBody() {
		return notificationBody;
	}

	public void setNotificationBody(String notificationBody) {
		this.notificationBody = notificationBody;
	}

	public String getTargetPlatforms() {
		return targetPlatforms;
	}

	public void setTargetPlatforms(String targetPlatforms) {
		this.targetPlatforms = targetPlatforms;
	}

	public Map<String, Integer> getApnsOutcomeCounts() {
		return apnsOutcomeCounts;
	}

	public void setApnsOutcomeCounts(Map<String, Integer> apnsOutcomeCounts) {
		this.apnsOutcomeCounts = apnsOutcomeCounts;
	}

	public Map<String, Integer> getMpnsOutcomeCounts() {
		return mpnsOutcomeCounts;
	}

	public void setMpnsOutcomeCounts(Map<String, Integer> mpnsOutcomeCounts) {
		this.mpnsOutcomeCounts = mpnsOutcomeCounts;
	}

	public Map<String, Integer> getWnsOutcomeCounts() {
		return wnsOutcomeCounts;
	}

	public void setWnsOutcomeCounts(Map<String, Integer> wnsOutcomeCounts) {
		this.wnsOutcomeCounts = wnsOutcomeCounts;
	}

	public Map<String, Integer> getGcmOutcomeCounts() {
		return gcmOutcomeCounts;
	}

	public void setGcmOutcomeCounts(Map<String, Integer> gcmOutcomeCounts) {
		this.gcmOutcomeCounts = gcmOutcomeCounts;
	}

	public Map<String, Integer> getFcmOutcomeCounts() {
		return fcmOutcomeCounts;
	}

	public void setFcmOutcomeCounts(Map<String, Integer> fcmOutcomeCounts) {
		this.fcmOutcomeCounts = fcmOutcomeCounts;
	}

	public Map<String, Integer> getBaiduOutcomeCounts() {
		return baiduOutcomeCounts;
	}

	public void setBaiduOutcomeCounts(Map<String, Integer> baiduOutcomeCounts) {
		this.baiduOutcomeCounts = baiduOutcomeCounts;
	}

	public Map<String, Integer> getAdmOutcomeCounts() {
		return admOutcomeCounts;
	}

	public void setAdmOutcomeCounts(Map<String, Integer> admOutcomeCounts) {
		this.admOutcomeCounts = admOutcomeCounts;
	}

	public String getPnsErrorDetailsUri() {
		return pnsErrorDetailsUri;
	}

	public void setPnsErrorDetailsUri(String pnsErrorDetailsUri) {
		this.pnsErrorDetailsUri = pnsErrorDetailsUri;
	}
	
	private static void setupParser(Digester digester){
		digester.addObjectCreate("*/NotificationDetails", NotificationTelemetry.class);
		digester.addCallMethod("*/NotificationId", "setNotificationId",1);
		digester.addCallParam("*/NotificationId", 0);
		digester.addCallMethod("*/Location", "setLocation",1);
		digester.addCallParam("*/Location", 0);
		digester.addCallMethod("*/State", "setNotificationStatusFromString",1);
		digester.addCallParam("*/State", 0);
		digester.addCallMethod("*/EnqueueTime", "setEnqueueTimeFromString",1);
		digester.addCallParam("*/EnqueueTime", 0);
		digester.addCallMethod("*/StartTime", "setStartTimeFromString",1);
		digester.addCallParam("*/StartTime", 0);
		digester.addCallMethod("*/EndTime", "setEndTimeFromString",1);
		digester.addCallParam("*/EndTime", 0);
		digester.addCallMethod("*/NotificationBody", "setNotificationBody",1);
		digester.addCallParam("*/NotificationBody", 0);
		digester.addCallMethod("*/TargetPlatforms", "setTargetPlatforms",1);
		digester.addCallParam("*/TargetPlatforms", 0);
		digester.addCallMethod("*/PnsErrorDetailsUri", "setPnsErrorDetailsUri",1);
		digester.addCallParam("*/PnsErrorDetailsUri", 0);
		
		digester.addObjectCreate("*/ApnsOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/ApnsOutcomeCounts", "setApnsOutcomeCounts", Map.class.getName());
		
		digester.addObjectCreate("*/MpnsOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/MpnsOutcomeCounts", "setMpnsOutcomeCounts", Map.class.getName());
		
		digester.addObjectCreate("*/WnsOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/WnsOutcomeCounts", "setWnsOutcomeCounts", Map.class.getName());
		
		digester.addObjectCreate("*/GcmOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/GcmOutcomeCounts", "setGcmOutcomeCounts", Map.class.getName());

		digester.addObjectCreate("*/FcmOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/FcmOutcomeCounts", "setFcmOutcomeCounts", Map.class.getName());
		
		digester.addObjectCreate("*/AdmOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/AdmOutcomeCounts", "setAdmOutcomeCounts", Map.class.getName());

		digester.addObjectCreate("*/BaiduOutcomeCounts", HashMap.class);		
		digester.addCallMethod("*/Outcome", "put",2, new Class[]{String.class, Integer.class} );
		digester.addCallParam("*/Name", 0);
		digester.addCallParam("*/Count", 1);
		digester.addSetNext("*/BaiduOutcomeCounts", "setBaiduOutcomeCounts", Map.class.getName());
	}
}
