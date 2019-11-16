//
//  RadioPlayerService.swift
//  flutter_radio_player
//
//  Created by Sithira on 11/15/19.
//  Copyright © 2019 Sithira. All rights reserved.
//

import Foundation
import AVFoundation
import MediaPlayer
import os.log

class RadioPlayerService {
    
    static var player: AVPlayer = AVPlayer()
    static var playerItem: AVPlayerItem?
    
    
    /**
     Starts the RadioPlayerService
     
     Initializes AVPlayer with given streamURL.
     Additionally it will setup lock screen controls as well as notification controls.
     
     */
    public func startService(streamURL: String) -> Void {
        let url = URL(string: streamURL)
        
        RadioPlayerService.playerItem = AVPlayerItem(url: url!)
        
        RadioPlayerService.player = AVPlayer(playerItem: RadioPlayerService.playerItem!)
        
        _initRemoteTransportControl()
    }
    
    /**
     Play configured stream from AVPlayer instance
     */
    internal func _play() -> Void {
        print("AVPlayer playing...")
        RadioPlayerService.player.play()
    }
    
    internal func _pause() -> Void {
        print("AVPlayer paused...")
        RadioPlayerService.player.pause()
    }
    
    public func stop() -> Void {
        RadioPlayerService.player = AVPlayer()
    }
    
    public func playOrPause() -> Void {
        print("Invoking play or pause method")
        print("Rate " + String(RadioPlayerService.player.rate))
        print("Errors \(String(describing: RadioPlayerService.player.error))")
        if (RadioPlayerService.player.rate != 0 && RadioPlayerService.player.error == nil) {
            self._pause()
        } else {
            self._play()
        }
    }
    
    /**
     Setups the LockScreen controls and
     */
    internal func _initRemoteTransportControl() {
        
        do {
            let commandCenter = MPRemoteCommandCenter.shared()
            
            // basic command center options
            commandCenter.togglePlayPauseCommand.isEnabled = true
            commandCenter.playCommand.isEnabled = true
            commandCenter.pauseCommand.isEnabled = true
            commandCenter.nextTrackCommand.isEnabled = false
            commandCenter.previousTrackCommand.isEnabled = false
            commandCenter.changePlaybackRateCommand.isEnabled = false
            commandCenter.skipForwardCommand.isEnabled = false
            commandCenter.skipBackwardCommand.isEnabled = false
            commandCenter.ratingCommand.isEnabled = false
            commandCenter.likeCommand.isEnabled = false
            commandCenter.dislikeCommand.isEnabled = false
            commandCenter.bookmarkCommand.isEnabled = false
            
            // only available in iOS 9
            if #available(iOS 9.0, *) {
                commandCenter.enableLanguageOptionCommand.isEnabled = false
                commandCenter.disableLanguageOptionCommand.isEnabled = false
            }
            
            commandCenter.changeRepeatModeCommand.isEnabled = false
            commandCenter.changeShuffleModeCommand.isEnabled = false
            
            commandCenter.playCommand.addTarget { (MPRemoteCommandEvent) -> MPRemoteCommandHandlerStatus in
                print("command center play command...")
                self._play()
                return .success
            }
            
            commandCenter.pauseCommand.addTarget { (MPRemoteCommandEvent) -> MPRemoteCommandHandlerStatus in
                print("command center pause command...")
                self._pause()
                return .success
            }
            
            commandCenter.stopCommand.addTarget { (MPRemoteCommandEvent) -> MPRemoteCommandHandlerStatus in
                print("command center stop command...")
                self.stop()
                return .success
            }
            
            let audioSession = AVAudioSession.sharedInstance()
            
            do {
                if #available(iOS 10.0, *) {
                    try audioSession.setCategory(AVAudioSessionCategoryPlayback, mode: AVAudioSessionCategoryAmbient, options: AVAudioSessionCategoryOptions.mixWithOthers)
                    try audioSession.overrideOutputAudioPort(.none)
                    try audioSession.setActive(true)
                }
            }
            
            UIApplication.shared.beginReceivingRemoteControlEvents()
        } catch {
            print("Something went wrong !")
        }
        
    }
    
}
