package me.sithiramunasinghe.flutter_radio_player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import me.sithiramunasinghe.flutter_radio_player.player.PlayerItem
import me.sithiramunasinghe.flutter_radio_player.player.RadioPlayerService

class FlutterRadioPlayerPlugin : MethodCallHandler {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_radio_player")
            channel.setMethodCallHandler(FlutterRadioPlayerPlugin())

            pluginRegistrar = registrar
            context = pluginRegistrar?.activeContext()
            serviceIntent = Intent(context, RadioPlayerService::class.java)
        }

        // static members
        var pluginRegistrar: Registrar? = null
        var context: Context? = null

        var radioPlayerService: RadioPlayerService? = null
        var isBound = false
        var serviceIntent: Intent? = null

        const val TAG: String = "FlutterRadioPlayerPlgin"

    }

    override fun onMethodCall(call: MethodCall, result: Result) {

        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "startService" -> {

                Log.d(TAG, "start service invoked")

                // todo: move to private method.
                val title = call.argument<String>("title")
                val channel = call.argument<String>("channel")
                val url = call.argument<String>("url")
                val smallIcon = call.argument<String>("appIcon")
                val bigIcon = call.argument<String>("albumCover")

                val playerItem = PlayerItem(title!!, channel!!, url!!, smallIcon!!, bigIcon!!)

                if (radioPlayerService != null) {

                    if (radioPlayerService?.streamURL != url) {
                        radioPlayerService?.stopAudio()
                        serviceIntent = setIntentData(serviceIntent!!, playerItem)
                        context?.startService(serviceIntent)
                    } else {
                        Log.d(TAG, "Player is already playing..")
                    }
                } else {
                    serviceIntent = setIntentData(serviceIntent!!, playerItem)
                    context?.startService(serviceIntent)
                }

                result.success(null)

            }
            "stop" -> {
                Log.d(TAG, "stop service invoked")
                if (radioPlayerService != null) {
                    context?.unbindService(serviceConnection)
                    radioPlayerService?.stopAudio()
                }
                result.success(null)
            }
            "pause" -> {
                Log.d(TAG, "pause service invoked")
                if (radioPlayerService != null) {
                    radioPlayerService?.pauseAudio()
                }
                result.success(null)
            }
            "resume" -> {
                Log.d(TAG, "resume service invoked")
                if (radioPlayerService != null) {
                    radioPlayerService?.resumeAudio()
                }

                result.success(null)
            }
            "unbind" -> {
                Log.d(TAG, "unbind service invoked")
                if (radioPlayerService != null) {
                    context?.unbindService(serviceConnection)
                    result.success(null)
                }
            }
            "checkIfBound" -> {
                Log.d(TAG, "checking bound service invoked")
                if (!isBound) {
                    context?.bindService(serviceIntent, serviceConnection, Context.BIND_IMPORTANT)
                }
                result.success(null)
            }
            "isPlaying" -> {
                Log.d(TAG, "is playing service invoked")
                result.success(radioPlayerService?.isPlaying())
            }
            else -> result.notImplemented()
        }

    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            radioPlayerService = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as RadioPlayerService.LocalBinder
            radioPlayerService = localBinder.service
            isBound = true
        }
    }

    private fun setIntentData(intent: Intent, playerItem: PlayerItem): Intent {
        intent.putExtra("title", playerItem.title)
        intent.putExtra("channel", playerItem.channel)
        intent.putExtra("url", playerItem.url)
        intent.putExtra("bigIcon", playerItem.albumCover)
        intent.putExtra("appIcon", playerItem.appIcon)
        return intent
    }
}
