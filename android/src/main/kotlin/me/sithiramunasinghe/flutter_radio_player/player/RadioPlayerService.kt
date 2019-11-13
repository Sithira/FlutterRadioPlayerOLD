package me.sithiramunasinghe.flutter_radio_player.player

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class RadioPlayerService : IntentService("flutter_radio_plugin"), PlayerNotificationManager.NotificationListener {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL = "flutter_radio_plugin_channel"
    }

    private lateinit var playerHolder: PlayerHolder

    private lateinit var playerNotificationManager: PlayerNotificationManager


    inner class PlayerServiceBinder : Binder() {
        fun getPlayerHolderInstance() = playerHolder
    }

    override fun onCreate() {
        super.onCreate()

        // Build a player holder
        playerHolder = PlayerModule.getPlayerHolder(this)

        /** Build a notification manager for our player, set a notification listener to this,
         * and assign the player just created.
         *
         * It is very important to note we need to get a [PlayerNotificationManager] instance
         * via the [PlayerNotificationManager.createWithNotificationChannel] because targeting Android O+
         * when building a notification we need to create a channel to which assign it.
         */


        playerNotificationManager = PlayerModule.getPlayerNotificationManager(this, this)
                .also {
                    it.setPlayer(playerHolder.audioFocusPlayer)
                }
    }

    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            playerHolder.start()
        }
        return PlayerServiceBinder()
    }

    override fun onHandleIntent(p0: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNotificationCancelled(notificationId: Int) {}

    override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
        startForeground(notificationId, notification)
    }
}