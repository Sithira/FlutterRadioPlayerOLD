import Flutter
import UIKit

public class SwiftFlutterRadioPlayerPlugin: NSObject, FlutterPlugin {
    
    private var radioPlayerService: RadioPlayerService = RadioPlayerService()
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_radio_player", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterRadioPlayerPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch (call.method) {
            case "startService":
                if let args = call.arguments as? Dictionary<String, Any>,
                    let streamURL = args["url"] as? String,
                    let title = args["title"] as? String,
                    let channel = args["channel"] as? String,
                    let albumCover = args["albumCover"] as? String,
                    let appIcon = args["appIcon"] as? String
                {
                    radioPlayerService.startService(streamURL: streamURL)
                    radioPlayerService.playOrPause()
                    result(nil)
                }
                break;
            case "resume":
                radioPlayerService.playOrPause()
                break
            case "pause":
                radioPlayerService.playOrPause()
                break
            case "stop":
                radioPlayerService.stop()
                break
            default:
                result(nil)
                break;
        }
    }
}
