package me.sithiramunasinghe.flutter_radio_player.player

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import me.sithiramunasinghe.flutter_radio_player.R

object PlayerModule {

    fun getPlayerHolder(context: Context) = PlayerHolder(context, PlayerState())

    fun getPlayerNotificationManager(context: Context, listener: PlayerNotificationManager.NotificationListener): PlayerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(
                    context,
                    RadioPlayerService.NOTIFICATION_CHANNEL,
                    R.string.channel_name,
                    R.string.channel_description,
                    RadioPlayerService.NOTIFICATION_ID,
                    getDescriptionAdapter(context), listener).apply {
                setFastForwardIncrementMs(0)
                setUseNavigationActions(false)
                setRewindIncrementMs(0)
            }

    private fun getDescriptionAdapter(context: Context) = DescriptionAdapter(context)
}