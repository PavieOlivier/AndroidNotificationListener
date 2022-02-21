import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:convert';

import 'package:android_notification_listener2/android_notification_listener2.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  AndroidNotificationListener _notifications;
  StreamSubscription<NotificationEventV2> _subscription;
  bool _permissionGiven = false;
  bool _inited = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    startListening();
    updatePermissionState();
  }

  Future<void> updatePermissionState() async {
    var permissionGiven = await _notifications.isPermissionGiven();
    setState(() {
      _permissionGiven = permissionGiven;
    });
  }

  void onData(NotificationEventV2 event) {

    print(event);
    print('converting package extra to json');
    var jsonDatax = json.decode(event.packageExtra);
    print(jsonDatax);
  }

  void startListening() {
    _notifications = AndroidNotificationListener.withoutInit();
    try {
      _subscription = _notifications.notificationStream.listen(onData);
    } on NotificationExceptionV2 catch (exception) {
      print(exception);
    }
  }

  void stopListening() {
    _subscription.cancel();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(children: [
          _permissionGiven
              ? Text("Permission has granted")
              : Text("Permission has not grant"),
          _inited
              ? Text("Plugin inited")
              : Text("Plugin not inited"),
          TextButton(
            child: Text("Init"),
            onPressed: () {
              _notifications.init();
              setState(() {
                _inited = _notifications.isInited;
              });
            },
          ),
          TextButton(
            child: Text("request permission"),
            onPressed: () {
              _notifications.requestPermission();
            },
          ),
        ])),
    );
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      updatePermissionState();
    }
  }

  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);
  }
}
