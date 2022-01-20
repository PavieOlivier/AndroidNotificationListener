import 'dart:async';
import 'package:flutter/services.dart';
import 'dart:io' show Platform;

/// Custom Exception for the plugin,
/// thrown whenever the plugin is used on platforms other than Android
class NotificationExceptionV2 implements Exception {
  String _cause;

  NotificationExceptionV2(this._cause);

  @override
  String toString() {
    return _cause;
  }
}

class NotificationEventV2 {
  String packageMessage;
  String packageName;
  String packageExtra;
  String packageText;
  DateTime timeStamp;

  NotificationEventV2({this.packageName, this.packageMessage, this.timeStamp , this.packageExtra , this.packageText});

  factory NotificationEventV2.fromMap(Map<dynamic, dynamic> map) {
    DateTime time = DateTime.now();
    String name = map['packageName'];
    String message = map['packageMessage'];
    String text = map['packageText'];
    String extra =  map['packageExtra'];

    return NotificationEventV2(packageName: name, packageMessage: message, timeStamp: time,packageText: text , packageExtra: extra);
  }

  @override
  String toString() {
    return "Notification Event \n Package Name: $packageName \n - Timestamp: $timeStamp \n - Package Message: $packageMessage";
  }
}

NotificationEventV2 _notificationEvent(dynamic data) {
  return new NotificationEventV2.fromMap(data);
}

class AndroidNotificationListener {
  EventChannel _notificationEventChannel;

  AndroidNotificationListener.withoutInit() {
    _notificationEventChannel = EventChannel('notifications.eventChannel');
  }

  factory AndroidNotificationListener() {
    final instance = AndroidNotificationListener.withoutInit();
    _init();
    return instance;
  }

  init() {
    _init();
  }

  bool get isInited => _inited;

  Future<bool> isPermissionGiven() {
    return _channel.invokeMethod("permissionGiven");
  }

  Future<void> requestPermission() {
    return _channel.invokeMethod("requestPermission");
  }

  Stream<NotificationEventV2> _notificationStream;

  Stream<NotificationEventV2> get notificationStream {
    if (Platform.isAndroid) {

      if (_notificationStream == null) {
        _notificationStream = _notificationEventChannel
            .receiveBroadcastStream()
            .map((event) => _notificationEvent(event));
      }
      return _notificationStream;
    }
    throw NotificationExceptionV2(
        'Notification API exclusively available on Android!');
  }
}

const MethodChannel _channel =
    const MethodChannel('notifications.commandChannel');

bool _inited = false;

void _init() {
  if (_inited) {
    throw new Exception("You can not initialize this plugin twice.");
  }
  _channel.invokeMethod("init");
  _inited = true;
}