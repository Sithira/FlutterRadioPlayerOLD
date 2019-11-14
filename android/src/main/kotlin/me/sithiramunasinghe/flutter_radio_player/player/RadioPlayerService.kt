package me.sithiramunasinghe.flutter_radio_player.player

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.session.MediaSession
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.util.Util.getUserAgent
import me.sithiramunasinghe.flutter_radio_player.FlutterRadioPlayerPlugin.Companion.methodChannel
import me.sithiramunasinghe.flutter_radio_player.R

class RadioPlayerService : Service() {

    private var isBound = false

    private val iBinder = LocalBinder()

    // context
    private val context = this

    // class instances
    private var player: SimpleExoPlayer? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    private var mediaSession: MediaSession? = null
    private val playerNotificationManager: PlayerNotificationManager? = null

    // session keys
    private val playbackChannelId = "flutter_radio_player_channel_id"
    private val playbackNotificationId = 1025
    private val mediaSessionId = "flutter_radio_radio_media_session"

    // stream URL
    var streamURL: String? = null

    inner class LocalBinder : Binder() {
        internal val service: RadioPlayerService
            get() = this@RadioPlayerService
    }

    override fun onDestroy() {
        super.onDestroy()

        streamURL = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSession?.release()
        }

        mediaSessionConnector?.setPlayer(null)
        playerNotificationManager?.setPlayer(null)
        player?.release()
        player = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // get details
        val title = intent!!.getStringExtra("title")
        val channel = intent.getStringExtra("channel")
        val url = intent.getStringExtra("url")

        streamURL = url

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())

        val dataSourceFactory = DefaultDataSourceFactory(context, getUserAgent(context, "flutter_radio_player"))

        val audioSource = buildMediaSource(dataSourceFactory, streamURL!!)

        val playerEvents = object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when {
                    playbackState == SimpleExoPlayer.STATE_IDLE -> methodChannel?.invokeMethod("onPlayerStateChanged", "idle")
                    playbackState == SimpleExoPlayer.STATE_BUFFERING -> methodChannel?.invokeMethod("onPlayerStateChanged", "buffering")
                    // add buffering string
                    playbackState == SimpleExoPlayer.STATE_ENDED -> {
                        //methodChannel.invokeMethod("onPlayerCompleted", null)
                        methodChannel?.invokeMethod("onPlayerStateChanged", "idle")
                        stopSelf()
                    }
                    playWhenReady -> methodChannel?.invokeMethod("onPlayerStateChanged", "playing")
                    // add playing
                    else -> methodChannel?.invokeMethod("onPlayerStateChanged", "paused")
                    // add paused
                }
            }
        }

        // set exo player configs
        player?.let {
            it.addListener(playerEvents)
            it.playWhenReady = true
            it.prepare(audioSource)
        }

        val playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context,
                playbackChannelId,
                R.string.channel_name,
                R.string.channel_description,
                playbackNotificationId,
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): String? {
                        return title
                    }

                    @Nullable
                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        /*
                        Intent intent = new Intent(context, MainActivity.class);
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        */
                        return null
                    }

                    @Nullable
                    override fun getCurrentContentText(player: Player): String? {
                        return channel
                    }

                    @Nullable
                    override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                        return if (intent.getStringExtra("bigIcon") == null) {
                            null
                        } else {

                            // todo: plugin reg
//                            val resourceId = pluginRegistrar.context().getResources().getIdentifier(
//                                    intent.getStringExtra("bigIcon"),
//                                    "drawable",
//                                    pluginRegistrar.context().getPackageName())
//                            BitmapFactory.decodeResource(
//                                    pluginRegistrar.context().getResources(),
//                                    resourceId
//                            )

                            return null

                        }
                    }
                },
                object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                        isBound = false
                        // audioService = null
                        stopSelf()
                    }

                    override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
                        startForeground(notificationId, notification)
                    }
                }
        )


        val mediaSession = MediaSessionCompat(context, mediaSessionId)
        mediaSession.isActive = true


        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector?.setPlayer(player)

        playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setUseNavigationActions(false)
        playerNotificationManager.setPlayer(player)
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)

        return START_STICKY
    }

    /**
     *
     */
    private fun buildMediaSource(dataSourceFactory: DefaultDataSourceFactory, streamUrl: String): MediaSource {

        val uri = Uri.parse(streamUrl)

        return when (val type = Util.inferContentType(uri)) {
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }


    fun pauseAudio() {
        player?.playWhenReady = false
    }

    fun resumeAudio() {
        player?.playWhenReady = true
    }

    fun stopService() {
        stopSelf()
    }

}