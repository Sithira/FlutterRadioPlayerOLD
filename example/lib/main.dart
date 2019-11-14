import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_radio_player/flutter_radio_player.dart';
import 'package:flutter_radio_player/models/RadioPlayerListener.dart';
import 'package:flutter_radio_player/enums/PlayerSate.dart';

void main() => runApp(MyApp());



class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Duration audioLength = Duration(milliseconds: 0);
  Duration audioPosition = Duration(milliseconds: 0);

  @override
  void initState() {
    super.initState();

    RadioPlayerListener listener = RadioPlayerListener(
        onPlayerStateChanged: (PlayerSate playerState) {
          if (playerState == PlayerSate.IDLE) {
            setState(() {
              audioLength = Duration(milliseconds: 0);
              audioPosition = Duration(milliseconds: 0);
            });
          } else {
            // setAudioLength();
          }
        },
        onPlayerPositionChanged: (Duration playerPosition) {
          setState(() {
            audioPosition = playerPosition;
          });
        },
        onPlayerCompleted: () {
          print("Player completed");
          audioPosition = Duration(milliseconds: 0);
          audioLength = Duration(milliseconds: 0);
        }
    );

    FlutterRadioPlayer.setListeners(listener);
  }

  @override
  void dispose() {
    super.dispose();

    FlutterRadioPlayer.unbind();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Sakwala FM'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              RaisedButton(
                child: Text("Connect to service, load audio, start playback"),
                onPressed: () async {
                  await FlutterRadioPlayer.init(
                    "Sakwala FM",
                    "Live Radio",
                    "http://149.56.147.197:8173/stream",
                    "ic_launcher",  // IMPORTANT!
                    "app_icon",     // see README for details about usage
                  );
                  setState(() {

                  });
                },
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  IconButton(
                    icon: Icon(Icons.play_arrow),
                    onPressed: () async {
                      await FlutterRadioPlayer.resume();
                    },
                  ),
                  IconButton(
                    icon: Icon(Icons.pause),
                    onPressed: () async {
                      await FlutterRadioPlayer.pause();
                    },
                  ),
                  IconButton(
                    icon: Icon(Icons.stop),
                    onPressed: () async {
                      await FlutterRadioPlayer.stop();
                      setState(() {
                        audioLength = Duration(milliseconds: 0);
                        audioPosition = Duration(milliseconds: 0);
                      });
                    },
                  ),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
