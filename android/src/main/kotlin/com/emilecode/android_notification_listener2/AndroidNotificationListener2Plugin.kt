package com.emilecode.android_notification_listener2


/**
 * Flutter-specific
 */
import android.app.Activity
import android.app.Service
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.EventChannel.EventSink

/**
 * Android-specific
 */
import android.content.*
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.FlutterEngine

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.service.ServicePluginBinding
import java.lang.Exception
import java.lang.reflect.Method
import io.flutter.embedding.engine.plugins.service.ServiceAware as ServiceAware


/**
 * AndroidNotificationListener2Plugin
 */
class AndroidNotificationListener2Plugin : StreamHandler, FlutterPlugin {
  private var eventSink: EventSink? = null
  private var methodChannel: MethodChannel? = null
  private var context: Context? = null

  /**
   * Called whenever the event channel is subscribed to in Flutter
   */
  override fun onListen(o: Any?, eventSink: EventSink?) {
    this.eventSink = eventSink
    /*
      Start the notification service once permission has been given.
     */
    val listenerIntent = Intent(context, NotificationListener::class.java)
    context!!.startService(listenerIntent)
  }

  /**
   * Called whenever the event channel subscription is cancelled in Flutter
   */
  override fun onCancel(o: Any?) {
    eventSink = null
  }

  /**
   * For all enabled notification listeners, check if any of them matches the package name of this application.
   * If any match is found, return true. Otherwise if no matches were found, return false.
   */
  private fun permissionGiven(): Boolean {
    val packageName = context!!.packageName
    val flat = Settings.Secure.getString(context!!.contentResolver,
            ENABLED_NOTIFICATION_LISTENERS)
    if (!TextUtils.isEmpty(flat)) {
      val names = flat.split(":").toTypedArray()
      for (name in names) {
        val componentName = ComponentName.unflattenFromString(name)
        val nameMatch = TextUtils.equals(packageName, componentName?.packageName)
        if (nameMatch) {
          return true
        }
      }
    }
    return false
  }

  internal inner class NotificationReceiver : BroadcastReceiver() {
    val TAG = "NOTIFICATION_RECEIVER"
    override fun onReceive(context: Context, intent: Intent) {
      val packageName = intent.getStringExtra(NotificationListener.NOTIFICATION_PACKAGE_NAME)
      val packageMessage = intent.getStringExtra(NotificationListener.NOTIFICATION_PACKAGE_MESSAGE)
      val packageText = intent.getStringExtra(NotificationListener.NOTIFICATION_PACKAGE_TEXT)
      val packageExtra = intent.getStringExtra(NotificationListener.NOTIFICATION_PACKAGE_EXTRA)
      val map = HashMap<String, Any>()
      map["packageName"] = packageName
      map["packageMessage"] = packageMessage
      map["packageText"] = packageText
      map["packageExtra"] =packageExtra
      eventSink?.success(map)
    }
  }

  companion object {
    const val TAG = "NOTIFICATION_PLUGIN"
    private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    private const val ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    private const val EVENT_CHANNEL_NAME = "notifications.eventChannel"
    private const val COMMAND_CHANEL = "notifications.commandChannel"
  }

  /**
   * Plugin constructor setting the context and registering the notification service.
   */
  init {

  }

  fun lateInit() {
    /* Check if permission is given, if not then go to the notification settings screen. */
    if (!permissionGiven()) {
      requestPermission()
    }
    val receiver = NotificationReceiver()
    val intentFilter = IntentFilter()
    intentFilter.addAction(NotificationListener.NOTIFICATION_INTENT)
    context!!.registerReceiver(receiver, intentFilter)

    /* Start the notification service once permission has been given. */
    val listenerIntent = Intent(context, NotificationListener::class.java)
    context!!.startService(listenerIntent)
  }

  private fun requestPermission() {
    val intent = Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context?.startActivity(intent)
  }

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    this.context = binding.applicationContext
    EventChannel(binding.binaryMessenger, EVENT_CHANNEL_NAME).setStreamHandler(this)
    methodChannel = MethodChannel(binding.getBinaryMessenger(), COMMAND_CHANEL)
    methodChannel!!.setMethodCallHandler { call, result ->
      run {
        when (call.method) {
          "init" -> {
            lateInit()
            result.success(null)
          }
          "permissionGiven" -> result.success(permissionGiven())
          "requestPermission" -> {
            requestPermission()
            result.success(null)
          }
          else -> { // Note the block
            throw UnsupportedOperationException("Method ${call.method} is not supported")
          }
        }
      }
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel?.setMethodCallHandler(null)
    methodChannel = null
    this.context = null
  }
}