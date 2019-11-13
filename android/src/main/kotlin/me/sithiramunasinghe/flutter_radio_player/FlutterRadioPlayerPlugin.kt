package me.sithiramunasinghe.flutter_radio_player

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import me.sithiramunasinghe.flutter_radio_player.player.RadioPlayerService

class FlutterRadioPlayerPlugin: MethodCallHandler {

  private lateinit var player: ExoPlayer

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_radio_player")
      channel.setMethodCallHandler(FlutterRadioPlayerPlugin())
      var intentService = Intent(registrar.context(), RadioPlayerService::class.java)
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

   var serviceConnection : ServiceConnection = object : ServiceConnection {
     override fun onServiceDisconnected(p0: ComponentName?) {
       TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
       if (service is RadioPlayerService.PlayerServiceBinder) {
         // Get the instance of the player from the service and set it as player to our playerView
         player = service.getPlayerHolderInstance().audioFocusPlayer
         player
       }
     }

   }
}
