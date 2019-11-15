import 'package:flutter/material.dart';
import 'package:flutter_radio_player/flutter_radio_player.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
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
