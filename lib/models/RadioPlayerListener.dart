import '../enums/PlayerSate.dart';

class RadioPlayerListener {
  RadioPlayerListener({
    Function onPlayerStateChanged,
    Function onPlayerPositionChanged,
    Function onPlayerCompleted,
  })  : _onPlayerStateChanged = onPlayerStateChanged,
        _onPlayerPositionChanged = onPlayerPositionChanged,
        _onPlayerCompleted = onPlayerCompleted;

  final Function _onPlayerStateChanged;
  final Function _onPlayerPositionChanged;
  final Function _onPlayerCompleted;

  /// Runs whenever PlayerState changes ex. audio paused, resumed, stopped, loading, idle.
  /// Useful for making dynamic play/pause buttons (example app will include later) or having different behaviour for certain
  /// UI elements in different player states
  onPlayerStateChanged(PlayerSate state) {
    if (_onPlayerStateChanged != null) {
      _onPlayerStateChanged(state);
    }
  }

  /// Runs whenever there is progress on audio playback. Useful to update a progress bar
  onPlayerPositionChanged(Duration position) {
    if (_onPlayerPositionChanged != null) {
      _onPlayerPositionChanged(position);
    }
  }

  /// Runs when the given audio source finishes. Could be used to implement playlists (plugin may include
  /// playlist somewhere in the future)
  onPlayerCompleted() {
    if (_onPlayerCompleted != null) {
      _onPlayerCompleted();
    }
  }
}
