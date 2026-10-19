package com.yesmin.ai.service.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class YesminNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Store or notify Yesmin if the user requests notification monitoring
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
    }

    companion object {
        var instance: YesminNotificationListener? = null
            private set

        fun isConnected(): Boolean = instance != null
    }
}
