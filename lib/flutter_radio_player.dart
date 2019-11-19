import 'dart:async';

import 'package:flutter/services.dart';

class FlutterRadioPlayer {

  static const MethodChannel _channel = const MethodChannel('flutter_radio_player');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Initializes Services, Binds Audio and Starts audio playback.
  static Future<void> init(String title, String channel, String url, String albumCover, String appIcon) async {

    await checkIfBound();

    await _channel.invokeMethod("startService", {
      "title": title,
      "channel": channel,
      "url": url,
      "albumCover": appIcon,
      "appIcon": appIcon,
    });

  }

  /// Stops and unbinds audio instances.
  static Future<void> stop() async {
    await checkIfBound();
    await _channel.invokeMethod("stop");
  }

  /// Pause playback if already playing
  static Future<void> pause() async {
    await checkIfBound();
    await _channel.invokeMethod("pause");
  }

  /// Toggles audio depending on current playback state.
  static Future<void> resume() async {
    await checkIfBound();
    await _channel.invokeMethod("resume");
  }

  /// Unbinds audio from services. Calling this will require to restart services
  static Future<void> unbind() async {
    await checkIfBound();
    await _channel.invokeMethod("unbind");
  }

  /// Checks and binds audio services.
  static Future<void> checkIfBound() async {
    await _channel.invokeMethod("checkIfBound");
  }
}
