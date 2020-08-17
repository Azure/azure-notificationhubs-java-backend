# Microsoft Azure Notification Hubs Java SDK

This repository contains source code for the Java SDK for [Microsoft Azure Notification Hubs](https://azure.microsoft.com/en-us/services/notification-hubs/).

## Building the Azure Notification Hubs Java SDK

To build, use [Maven](http://maven.apache.org/):

```bash
$ cd NotificationHubs
$ mvn package
```

## Getting Started

To get started, you can find all the classes in the `com.windowsazure.messaging` package, for example:

```java
import com.windowsazure.messaging.NotificationHub;
```

The Azure Notification Hubs SDK for Java support both synchrnous and asynchrnous operations on `NotificationHub/NotificationHubClient` and `NamespaceManager/NamespaceManagerClient`.  The asynchronous APIs are supported using the [Reactor](https://projectreactor.io/) Framework.  We will show synchrnous examples, however you can utilize the async methods, which are suffixed with `Async` such as the following:

```java
// Synchronous
NotificationHubDescription hub = new NotificationHubDescription("hubname");
hub.setWindowsCredential(new WindowsCredential("sid","key"));
hub = namespaceManager.createNotificationHub(hub);

// Asynchronous
NotificationHubDescription hub = new NotificationHubDescription("hubname");
hub.setWindowsCredential(new WindowsCredential("sid","key"));
Disposable subscription = namespaceManager.createNotificationHubAsync(hub)
    .subscribe(
        createdHub -> {
        // Handle success 
        },
        throwable -> {
            // Handle failure
        });
```


## Azure Notification Hubs Management Operations

This section details the usage of the Azure Notification Hubs SDK for Java management operations for CRUD operations on Notification Hubs and Notification Hub Namespaces.

### Create a namespace manager:

```java
NamespaceManagerClient namespaceManager = new NamespaceManager("connection string");
```

### Create an Azure Notification Hub:

```java
NotificationHubDescription hub = new NotificationHubDescription("hubname");
hub.setWindowsCredential(new WindowsCredential("sid","key"));
hub = namespaceManager.createNotificationHub(hub);
```

### Get a Azure Notification Hub:

```java	
hub = namespaceManager.getNotificationHub("hubname");
```	

### Update an Azure Notification Hub:

```java
hub.setMpnsCredential(new MpnsCredential("mpnscert", "mpnskey"));
hub = namespaceManager.updateNotificationHub(hub);
```

### Delete an Azure Notification Hub:

```java
namespaceManager.deleteNotificationHub("hubname");
```

## Azure Notification Hubs Operations

The `NotificationHub` class is the main entry point for installations/registrations, but also sending push notifications.  To create a `NotificationHub`, you need the connection string from your Access Policy with the desired permissions such as `Listen`, `Manage` and `Send`, and in addition, the hub name to use.

**Create an Azure Notification Hub Client:**

```java
NotificationHub hub = new NotificationHub("connection string", "hubname");
```

## Azure Notification Hubs Installation API 

An Installation is an enhanced registration that includes a bag of push related properties. It is the latest and best approach to registering your devices. 

The following are some key advantages to using installations:

- Creating or updating an installation is fully idempotent. So you can retry it without any concerns about duplicate registrations.
- The installation model supports a special tag format `($InstallationId:{INSTALLATION_ID})` that enables sending a notification directly to the specific device. For example, if the app's code sets an installation ID of `joe93developer` for this particular device, a developer can target this device when sending a notification to the `$InstallationId:{joe93developer}` tag. This enables you to target a specific device without having to do any additional coding.
- Using installations also enables you to do partial registration updates. The partial update of an installation is requested with a PATCH method using the JSON-Patch standard. This is useful when you want to update tags on the registration. You don't have to pull down the entire registration and then resend all the previous tags again.

Using this SDK, you can do these Installation API operations.  For example, we can create an installation for an Amazon Kindle Fire.

```java
Installation installation = new Installation("installation-id", NotificationPlatform.Adm, "adm-push-channel");
hub.createOrUpdateInstallation(installation);
```
	
An installation can have multiple tags and multiple templates with its own set of tags and headers.

```java
installation.addTag("foo");
installation.addTemplate("template1", new InstallationTemplate("{\"data\":{\"key1\":\"$(value1)\"}}","tag-for-template1"));
installation.addTemplate("template2", new InstallationTemplate("{\"data\":{\"key2\":\"$(value2)\"}}","tag-for-template2"));
hub.createOrUpdateInstallation(installation);
```

For advanced scenarios we have partial update capability which allows to modify only particular properties of the installation object. Basically partial update is subset of [JSON Patch](https://tools.ietf.org/html/rfc6902/) operations you can run against Installation object.

```java
PartialUpdateOperation addChannel = new PartialUpdateOperation(UpdateOperationType.Add, "/pushChannel", "adm-push-channel2");
PartialUpdateOperation addTag = new PartialUpdateOperation(UpdateOperationType.Add, "/tags", "bar");
PartialUpdateOperation replaceTemplate = new PartialUpdateOperation(UpdateOperationType.Replace, "/templates/template1", new InstallationTemplate("{\"data\":{\"key3\":\"$(value3)\"}}","tag-for-template1")).toJson());
hub.patchInstallation("installation-id", addChannel, addTag, replaceTemplate);
```

**Delete an Installation:**

```java
hub.deleteInstallation(installation.getInstallationId());
```
	
Keep in mind that CreateOrUpdate, Patch and Delete are eventually consistent with Get. In fact operation just goes to the system queue during the call and will be executed in background. Moreover Get is not designed for main runtime scenario but just for debug and troubleshooting purposes, it is tightly throttled by the service.

## Azure Notification Hub Registration API

A registration associates the Platform Notification Service (PNS) handle for a device with tags and possibly a template. The PNS handle could be a ChannelURI, device token, or FCM registration ID. Tags are used to route notifications to the correct set of device handles. Templates are used to implement per-registration transformation.  The Registration API handles requests for these operations.

### Create a Windows Registration:

```java
WindowsRegistration reg = new WindowsRegistration(new URI(CHANNELURI));
reg.getTags().add("myTag");
reg.getTags().add("myOtherTag");	
hub.createRegistration(reg);
```

### Create an Apple Registration:

```java
AppleRegistration reg = new AppleRegistration(DEVICETOKEN);
reg.getTags().add("myTag");
reg.getTags().add("myOtherTag");
hub.createRegistration(reg);
```

Analogous for Android (GCM), Windows Phone (MPNS), and Kindle Fire (ADM).

### Create Template Registrations:

```java
WindowsTemplateRegistration reg = new WindowsTemplateRegistration(new URI(CHANNELURI), WNSBODYTEMPLATE);
reg.getHeaders().put("X-WNS-Type", "wns/toast");
hub.createRegistration(reg);
```

Create registrations using create registrationid+upsert pattern (removes duplicates deriving from lost responses if registration ids are stored on the device):

```java
String id = hub.createRegistrationId();
WindowsRegistration reg = new WindowsRegistration(id, new URI(CHANNELURI));
hub.upsertRegistration(reg);
```

### Update a Registration:

```java
hub.updateRegistration(reg);
```

### Delete a Registration:

```java
hub.deleteRegistration(regid);
```

### Get a Single Registration:

```java
Registration registration = hub.getRegistration(regid);
```

All collection queries support $top and continuation tokens.

### Get All Registrations in an Azure Notification Hub:

```java
CollectionResult registrations = hub.getRegistrations();
```

### Get Registrations With a Given Tag:

```java
CollectionResult registratons = hub.getRegistrationsByTag("myTag");
```

### Get Registrations By Channel:

```java
CollectionResult registratons = hub.getRegistrationsByChannel("devicetoken");
```

## Send Notifications

The Notification object is simply a body with headers, some utility methods help in building the native and template notifications objects.

### Send Windows Native Notification:

```java
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
```

### Send an Apple Push Notification:

```java
Notification n = Notification.createAppleNotifiation("APNS body");
hub.sendNotification(n);
```

Analogous for Android, Windows Phone, Kindle Fire and Baidu PNS.

### Send a Template Notification

```java
Map<String, String> prop =  new HashMap<String, String>();
prop.put("prop1", "v1");
prop.put("prop2", "v2");
Notification n = Notification.createTemplateNotification(prop);

hub.sendNotification(n);
```

### Send To An Installation ID:
	
Send flow for Installations is the same as for Registrations. We've just introduced an option to target notification to the particular Installation - just use tag "InstallationId:{desired-id}". For case above it would look like this:

```java
Notification n = Notification.createWindowsNotification("WNS body");
hub.sendNotification(n, "InstallationId:{installation-id}");
```

### Send To An Installation Template For An Installation:

```java
Map<String, String> prop =  new HashMap<String, String>();
prop.put("value3", "some value");
Notification n = Notification.createTemplateNotification(prop);
hub.sendNotification(n, "InstallationId:{installation-id} && tag-for-template1");
```

## Scheduled Send Operations

**Note: This feature is only available for [STANDARD Tier](http://azure.microsoft.com/en-us/pricing/details/notification-hubs/).**

Scheduled send operations are similar to a normal send operations, with a scheduledTime parameter which says when notification should be delivered. The Azure Notification Hubs Service accepts any point of time between now + 5 minutes and now + 7 days.

### Schedule Windows Native Send Operation:

```java
Calendar c = Calendar.getInstance();
c.add(Calendar.DATE, 1);	

Notification n = Notification.createWindowsNotification("WNS body");

hub.scheduleNotification(n, c.getTime());
```
	
## Import and Export Registrations

**Note: This feature is only available for [STANDARD Tier](http://azure.microsoft.com/en-us/pricing/details/notification-hubs/).**

Sometimes it is required to perform bulk operation against registrations. Usually it is for integration with another system or just to update the tags. It is strongly not recomended to use Get/Update flow if you are modifying thousands of registrations. Import/Export capability is designed to cover the scenario. You provide an access to some blob container under your storage account as a source of incoming data and location for output.

### Submit an Export Job:

```java
NotificationHubJob job = new NotificationHubJob();
job.setJobType(NotificationHubJobType.ExportRegistrations);
job.setOutputContainerUri("container uri with SAS signature");
job = hub.submitNotificationHubJob(job);
```

### Submit an Import Job:

```java
NotificationHubJob job = new NotificationHubJob();
job.setJobType(NotificationHubJobType.ImportCreateRegistrations);
job.setImportFileUri("input file uri with SAS signature");
job.setOutputContainerUri("container uri with SAS signature");
job = hub.submitNotificationHubJob(job);
```
	
### Wait for Job Completion:

```java
while(true) {
	Thread.sleep(1000);
	job = hub.getNotificationHubJob(job.getJobId());
	if(job.getJobStatus() == NotificationHubJobStatus.Completed) {
		break;
	}
}
```
	
### et All jobs:

```java
List<NotificationHubJob> jobs = hub.getAllNotificationHubJobs();
```

## References:

[Microsoft Azure Notification Hubs Docs](https://docs.microsoft.com/en-us/azure/notification-hubs/)

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.microsoft.com.

When you submit a pull request, a CLA-bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., label, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
