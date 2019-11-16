#import "FlutterRadioPlayerPlugin.h"
#import <flutter_radio_player/flutter_radio_player-Swift.h>

@implementation FlutterRadioPlayerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterRadioPlayerPlugin registerWithRegistrar:registrar];
}
@end
