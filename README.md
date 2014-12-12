Java REST wrapper for Windows Azure Notification Hubs
==========================

Java Back-end SDK for [Windows Azure Notification Hubs]. Using [REST APIs].

Implements most of the Notification Hubs REST operations to perform Hub CRUDs, registration/installation management and send notification.

Please send feedback/comments, and pull requests... :)

# New

* Baidu PNS support
* Import/Export jobs
* Scheduled notifications
* Installation API support
* Namespace Manager is introduced to perform CRUDs against Notification Hub(s)


#Use

##Compile and build
Use [Maven].

To build:

	mvn package

##Code

Create a client

	hub = new NotificationHub("connection string", "hubname");	

### Create/Update/Delete Registrations

Create Windows registration:

	WindowsRegistration reg = new WindowsRegistration(new URI(CHANNELURI));
	reg.getTags().add("myTag");
	reg.getTags().add("myOtherTag");	
	hub.createRegistration(reg);

Create iOS registration:

	AppleRegistration reg = new AppleRegistration(DEVICETOKEN);
	reg.getTags().add("myTag");
	reg.getTags().add("myOtherTag");
	hub.createRegistration(reg);

Analogous for Android (GCM), Windows Phone (MPNS), and Kindle Fire (ADM).

Create template registrations:

	WindowsTemplateRegistration reg = new WindowsTemplateRegistration(new URI(CHANNELURI), WNSBODYTEMPLATE);
	reg.getHeaders().put("X-WNS-Type", "wns/toast");
	hub.createRegistration(reg);

Create registrations using create registrationid+upsert pattern (removes duplicates deriving from lost responses if registration ids are stored on the device):

	String id = hub.createRegistrationId();
	WindowsRegistration reg = new WindowsRegistration(id, new URI(CHANNELURI));
	hub.upsertRegistration(reg);

Update registrations:

	hub.updateRegistration(reg);

Delete registrations:

	hub.deleteRegistration(regid);

### Query registrations

Get single registration:

	hub.getRegistration(regid);

All collection queries support $top and continuation tokens.

Get all registrations in hub:
	
	hub.getRegistrations();


Get registrations with tag:

	hub.getRegistrationsByTag("myTag");


Get registrations by channel:

	hub.getRegistrationsByChannel("devicetoken");


### Send Notifications

The Notification object is simply a body with headers, some utility methods help in building the native and template notifications objects.

Send Windows native:
	
	Notification n = Notification.createWindowsNotification("WNS body");
	
	// broadcast
	hub.sendNotification(n);
		
	// send to tags	
	Set<String> tags = new HashSet<String>();
	tags.add("boo");
	tags.add("foo");
	hub.sendNotification(n, tags);

	// send to tag expression		
	hub.sendNotification(n, "foo && ! bar");

Send iOS native:
	
	Notification n = Notification.createAppleNotifiation("APNS body");
	hub.sendNotification(n);


Analogous for Android, Windows Phone, Kindle Fire and Baidu PNS.

Send template notification:

	Map<String, String> prop =  new HashMap<String, String>();
	prop.put("prop1", "v1");
	prop.put("prop2", "v2");
	Notification n = Notification.createTemplateNotification(prop);
		
	hub.sendNotification(n);
	
### Schedule Notifications

The same as regular send but with one additional parameter - scheduledTime which says when notification should be delivered. Service accepts any point of time between now + 5 minutes and now + 7 days.

Schedule Windows native:
	
	Calendar c = Calendar.getInstance();
	c.add(Calendar.DATE, 1);	
	
	Notification n = Notification.createWindowsNotification("WNS body");
	
	hub.scheduleNotification(n, c.getTime());
	
### Installation API usage

Installation API is alternative mechanism for registration management. Instead of maintaining 1+ registrations which sometimes is not trivial and may be easily done wrong or inefficient it is now possible to use SINGLE Installation object. Installation contains everything you need: push channel (device token), tags, templates, secondary tiles (for WNS and APNS). You don't need to call Service to get Id anymore - just generate GUID or any other identifier, keep it on device and send to your backend together with push channel (device token). On the backend you should only do single call: CreateOrUpdateInstallation, it is fully idempotent, so feel free to retry if needed.

As example for Amazon Kindle Fire it looks like this:

	Installation installation = new Installation("installation-id", NotificationPlatform.Adm, "adm-push-channel");
	hub.CreateOrUpdateInstallation(installation);
	
Need to add something new - just do it:

	installation.addTag("foo");
	installation.addTemplate("template1", new InstallationTemplate("{\"data\":{\"key1\":\"$(value1)\"}}","tag-for-template1"));
	installation.addTemplate("template2", new InstallationTemplate("{\"data\":{\"key2\":\"$(value2)\"}}","tag-for-template2"));
	hub.CreateOrUpdateInstallation(installation);

For advanced scenarios we have partial update capability which allows to modify only particular properties of the installation object. Basically partial update is subset of JSON Patch operations you can run against Installation object.

	PartialUpdateOperation addChannel = new PartialUpdateOperation(UpdateOperationType.Add, "/pushChannel", "adm-push-channel2");
	PartialUpdateOperation addTag = new PartialUpdateOperation(UpdateOperationType.Add, "/tags", "bar");
	PartialUpdateOperation replaceTemplate = new PartialUpdateOperation(UpdateOperationType.Replace, "/templates/template1", new InstallationTemplate("{\"data\":{\"key3\":\"$(value3)\"}}","tag-for-template1")).toJson());
	hub.PatchInstallation("installation-id", addChannel, addTag, replaceTemplate);
	
Send flow for Installations is the same as for Registrations. We've just introduced an option to target notification to the particular Installation - just use tag "InstallationId:{desired-id}". For case above it would look like this:
	
	Notification n = Notification.createWindowsNotification("WNS body");
	hub.sendNotification(n, "InstallationId:{installation-id}");
	
For the one of several templates:
	
	Map<String, String> prop =  new HashMap<String, String>();
	prop.put("value3", "some value");
	Notification n = Notification.createTemplateNotification(prop);
	hub.sendNotification(n, "InstallationId:{installation-id} && tag-for-template1");
	
## References:

[MSDN documentation]

[Windows Azure Notification Hubs Service Page]

Nice tutorials that are easy to translate in Java:

* [Get Started with Notification Hubs]
* [Send breaking news]
* [Send localized breaking news]
* [Send notifications to authenticated users]
* [Send cross-platform notifications to authenticated users]

#Dependencies

This project uses:

* Apache HttpComponents.
* Apache Commons Codec.
* Apache Commons IO.]
* Apache Commons Digester.
* Google Gson.

#Status
**Complete**:

* Hub CRUDs
* Registration Mgmt
* Installation Mgmt
* Regular sends
* Scheduled sedns
* Import/Export
* Supported platforms: APNS (iOS), GCM (Android), WNS (Windows Store apps), MPNS(Windows Phone), ADM (Amazon Kindle Fire), Baidu (Android without Google services) 

**To add**:

* Async operations via Java NIO
* Multi-factor authentication support
* Javadocs for bunch of recently implemented features

**Caveats**:

* Not tested for performance. You can plug in your own HttpClient instance if you want to improve connection pooling, adding retry policies and such.


[REST APIs]: http://msdn.microsoft.com/en-us/library/windowsazure/dn223264.aspx
[Maven]: http://maven.apache.org/
[JSON Patch]: https://tools.ietf.org/html/rfc6902
[Windows Azure Notification Hubs]: http://www.windowsazure.com/en-us/documentation/services/notification-hubs/
[MSDN documentation]: http://msdn.microsoft.com/en-us/library/windowsazure/jj891130.aspx
[Windows Azure Notification Hubs Service Page]: http://www.windowsazure.com/en-us/documentation/services/notification-hubs/
[Get Started with Notification Hubs]: http://www.windowsazure.com/en-us/manage/services/notification-hubs/getting-started-windows-dotnet/
[Send breaking news]: http://www.windowsazure.com/en-us/manage/services/notification-hubs/breaking-news-dotnet/
[Send localized breaking news]: http://www.windowsazure.com/en-us/manage/services/notification-hubs/breaking-news-localized-dotnet/
[Send notifications to authenticated users]: http://www.windowsazure.com/en-us/manage/services/notification-hubs/notify-users/
[Send cross-platform notifications to authenticated users]: http://www.windowsazure.com/en-us/manage/services/notification-hubs/notify-users-xplat-mobile-services/


