# Android Notification Listener ( V2 )


![](https://github.com/PavieOlivier/AndroidNotificationListener/blob/master/Image/notif.png?raw=true)

**Note:** This pluggin is a Fix [for the original one](https://pub.dev/packages/android_notification_listener) which is depracated, please use this one instead


This plugin ( only availaible on Android due to iOS restrictions ) will allow you to listen to any incoming notifications on an Android device running on Android APi level 21 or more . In another word you can read notifications coming from all the installed applications with deep details


**Note:** This pluggin is a Fork of the original plugin made by [Cachet](https://pub.dev/packages/notifications) which has some bugs causing frequent crashes and is missing features and details, so I fixed it and added more features :)

## Features
Here are the details you will get from a notification
- The time Stamp
- The package name
- The package message
- The package text
- The package extra

**Note:** The `PackageExtra` contains more details about each individual notification. It is originally a bundle file. I converted it into a Json and give it to you as a Json String so if you need to use the `PackageExtra` you will have to json decode it as follow

```
var jsonDatax = json.decode(event.packageExtra);
print(jsonDatax);
```
You can refer the example folder for more detail

Here are the extra data you will get from the `PackageExtra`

- android.title
- android.reduced.images
- android.template
- android.text
- android.foregroundApps
- android.bigText

## Android Setup
#### Register a service in the Android Manifest
The plugin uses an Android system service to track notifications. To allow this service to run on your application, the following code should be put inside the Android manifest, between the <application></application> tags.

```xml
<service android:name="com.emilecode.android_notification_listener2.NotificationListener"
    android:label="notifications"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

#### Listen to notification events

```
AndroidNotificationListener _notifications;
StreamSubscription<NotificationEventV2> _subscription;
subscription = _notifications.notificationStream.listen(onData);
```

Where `the onData()` method handles the incoming `NotificationEventV2`.
An example could be:

```
void onData(NotificationEventV2 event) => print(event.toString());
```

# Conclusion

Thank you for using my package, if you have any question feel free to contact me on [instagram](https://www.instagram.com/emilecode/) or directly on my mail emile@emilecode.com

Here are my other packages

- [Ultimate Data Generator](https://pub.dev/packages/ultimate_data_generator): generate realistic datas
- [Sliding Card](https://pub.dev/packages/sliding_card): Animated card that slides down to reveal its hidden part
- [AdvFab](https://pub.dev/packages/adv_fab): An **Advanced floating action button** that expands itself to reveal its hidden widget. It can also be used as an **AnimatedbottomNavigationbar** or just an **AnimatedFloatingActionBar** with the same expansion capabilities.
