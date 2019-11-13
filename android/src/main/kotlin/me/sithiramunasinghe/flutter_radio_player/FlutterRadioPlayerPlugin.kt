package me.sithiramunasinghe.flutter_radio_player

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class FlutterRadioPlayerPlugin: MethodCallHandler {

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_radio_player")
      channel.setMethodCallHandler(FlutterRadioPlayerPlugin())
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

  val serviceConnection = object : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

  }
}
