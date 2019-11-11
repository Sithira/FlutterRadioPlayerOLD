package me.sithiramunasinghe.flutter_radio_player.player

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import org.greenrobot.eventbus.EventBus

class RadioService : Service(), Player.EventListener, AudioManager.OnAudioFocusChangeListener {

    private val context: Context = this
    private var channelId = "ExoPlayerChannel"
    private var notificationId = 1
    private var streamUrl: String? = null

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var status: String

    private val binder = LocalBinder()

    inner class LocalBinder: Binder() {
        fun getService(): RadioService {
            return this@RadioService
        }
    }

    fun initExoPlayer() {

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context)
        exoPlayer.addListener(this)
        exoPlayer.setForegroundMode(true)

        val playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, channelId, R.string.exo_download_notification_channel_name, R.string.exo_download_description, notificationId, object: PlayerNotificationManager.MediaDescriptionAdapter {

            override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getCurrentContentText(player: Player?): String? {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getCurrentContentTitle(player: Player?): String {
                return "SakwalaFM"
            }

            override fun getCurrentLargeIcon(player: Player?, callback: PlayerNotificationManager.BitmapCallback?): Bitmap? {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }, object: PlayerNotificationManager.NotificationListener {

            override fun onNotificationPosted(notificationId: Int, notification: Notification?, ongoing: Boolean) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                stopSelf()
            }
        })

        playerNotificationManager.setPlayer(exoPlayer)
        playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setUseNavigationActions(false)

        status = PlaybackStatus.IDLE
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun playOrPause(url: String) {
        if (streamUrl != null && streamUrl == url) {
            if (!isPlaying()) {
                play(streamUrl!!)
            } else {
                pause()
            }
        } else {
            if (isPlaying()) {
                pause()
            }

            play(url)
        }
    }

    fun isPlaying() : Boolean {
        return this.status.equals(PlaybackStatus.PLAYING)
    }

    fun getStatus() : String = status

    private fun play(streamUrl: String) {
        this.streamUrl = streamUrl

        val dataSourceFactory = DefaultDataSourceFactory(this.context,
                Util.getUserAgent(this.context, "exoPlayerLibrary"))

        val mediaSource = buildMediaSource(streamUrl, dataSourceFactory)

        exoPlayer.stop()
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = true
        exoPlayer.setForegroundMode(true)

    }

    private fun buildMediaSource(streamUrl: String,
                                 dataSourceFactory: DefaultDataSourceFactory): MediaSource {
        val uri = Uri.parse(streamUrl)
        val type = Util.inferContentType(uri)
        return when (type) {
            C.TYPE_HLS   -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else         -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    private fun resume() {
        if (streamUrl != null)
            play(streamUrl!!)
    }

    private fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer.volume = 0.8f
                resume()
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                stop();
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (isPlaying()) pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (isPlaying())
                    exoPlayer.volume = 0.1f
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        status = when(playbackState) {
            Player.STATE_BUFFERING -> PlaybackStatus.LOADING
            Player.STATE_ENDED -> PlaybackStatus.STOPPED
            Player.STATE_READY -> if (playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED
            else -> PlaybackStatus.IDLE
        }

        if (EventBus.getDefault().hasSubscriberForEvent(String::class.java))
            EventBus.getDefault().post(status)
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        if (EventBus.getDefault().hasSubscriberForEvent(String::class.java)) {
            EventBus.getDefault().post(PlaybackStatus.ERROR)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}