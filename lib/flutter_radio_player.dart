import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class FlutterRadioPlayer {
  static const MethodChannel _channel =
      const MethodChannel('flutter_radio_player');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> init(String title, String channel, String url,
      String albumCover, String appIcon) async {
    String checkIfNull(String toCheck) {
      if (toCheck == null) {
        return "theGivenResourceIsNull";
      } else {
        return toCheck;
      }
    }

    debugPrint("init invoked... calling native methods");

    await checkIfBound();

    await _channel.invokeMethod("startService", {
      "title": title,
      "channel": channel,
      "url": url,
      "albumCover": checkIfNull(albumCover),
      "appIcon": checkIfNull(appIcon),
    });
  }

  static Future<void> stop() async {
    await checkIfBound();
    await _channel.invokeMethod("stop");
  }

  /// Will pause the player. If player already paused will do nothing
  static Future<void> pause() async {
    await checkIfBound();
    await _channel.invokeMethod("pause");
  }

  /// Will resume playback (only if already loaded earlier and paused afterwards). If already playing will do nothing
  static Future<void> resume() async {
    await checkIfBound();
    await _channel.invokeMethod("resume");
  }

  static Future<void> unbind() async {
    await checkIfBound();
    await _channel.invokeMethod("unbind");
  }

  static Future<void> checkIfBound() async {
    await _channel.invokeMethod("checkIfBound");
  }
}
