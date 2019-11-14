import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:flutter_radio_player/enums/PlayerSate.dart';
import 'package:flutter_radio_player/models/RadioPlayerListener.dart';

class FlutterRadioPlayer {
  static const MethodChannel _channel =
      const MethodChannel('flutter_radio_player');

  static RadioPlayerListener _radioPlayerListener;

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void setListeners(RadioPlayerListener playerListener) {
    _radioPlayerListener = playerListener;

    _channel.setMethodCallHandler(_methodCallHandler);
  }

  static Future<dynamic> _methodCallHandler(MethodCall methodCall) {
    switch (methodCall.method) {
      case "onPlayerStateChanged":
        switch (methodCall.arguments) {
          case "idle":
            _radioPlayerListener.onPlayerStateChanged(PlayerSate.IDLE);
            break;

          case "buffering":
            _radioPlayerListener.onPlayerStateChanged(PlayerSate.LOADING);
            break;

          case "playing":
            _radioPlayerListener.onPlayerStateChanged(PlayerSate.PLAYING);
            break;

          case "paused":
            _radioPlayerListener.onPlayerStateChanged(PlayerSate.PAUSE);
            break;
        }

        break;

//      case "onPlayerPositionChanged":
//        _audioListener
//            .onPlayerPositionChanged(Duration(milliseconds: call.arguments));
//        break;
//
//      case "onPlayerCompleted":
//        _audioListener.onPlayerCompleted();
//        break;

      default:
        print("ERROR: method not implemented");
        break;
    }

    return null;
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

    await _channel.invokeMethod("startService", {
      "title": title,
      "channel": channel,
      "url": url,
      "albumCover": checkIfNull(albumCover),
      "appIcon": checkIfNull(appIcon),
    });
  }

  static Future<void> stop() async {
    await _channel.invokeMethod("stop");
  }

  /// Will pause the player. If player already paused will do nothing
  static Future<void> pause() async {
    await _channel.invokeMethod("pause");
  }

  /// Will resume playback (only if already loaded earlier and paused afterwards). If already playing will do nothing
  static Future<void> resume() async {
    await _channel.invokeMethod("resume");
  }

  static Future<void> unbind() async {
    await _channel.invokeMethod("unbind");
  }
}
