package com.emilecode.android_notification_listener2

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONException
import org.json.JSONObject
import android.os.Bundle
import android.util.Log




/**
 * Notification listening service. Intercepts notifications if permission is given to do so.
 */
class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
      try
      {
          // Retrieve package name to set as title.
          val packageName = sbn.packageName
          // Retrieve extra object from notification to extract payload.
          val extras = sbn.notification.extras
          val packageMessage = extras?.getCharSequence(Notification.EXTRA_TEXT).toString()
          val packageText = extras?.getCharSequence("android.title").toString()
          val packageExtra = convertBumbleToJsonString(sbn.notification.extras)
          // Pass data from one activity to another.
          val intent = Intent(NOTIFICATION_INTENT)
          intent.putExtra(NOTIFICATION_PACKAGE_NAME, packageName)
          intent.putExtra(NOTIFICATION_PACKAGE_MESSAGE, packageMessage)
          intent.putExtra(NOTIFICATION_PACKAGE_TEXT, packageText)
          intent.putExtra(NOTIFICATION_PACKAGE_EXTRA, packageExtra)
          sendBroadcast(intent) 
      }
      catch (error :Exception ){
          Log.w( "Crashing aborded ", "An exception occured, I do not know yet what causes that error\nIt seams that a bundle is null on a notification received or it is just a bug\nIf you did not receive the notification please raise a complain on my github" );
      }
    }

    companion object {
        const val NOTIFICATION_INTENT = "notification_event"
        const val NOTIFICATION_PACKAGE_NAME = "package_name"
        const val NOTIFICATION_PACKAGE_MESSAGE = "package_message"
        const val NOTIFICATION_PACKAGE_TEXT = "package_text";
        const val NOTIFICATION_PACKAGE_EXTRA = "package_extra";
    }

    private fun convertBumbleToJsonString(extra: Bundle): String {
        val json = JSONObject()
        val keys = extra.keySet()
        for (key in keys) {
            try {

                json.put(key, JSONObject.wrap(extra.get(key)))
            } catch (e: JSONException) {
                //Handle exception here
            }

        }

        return json.toString()
    }
}