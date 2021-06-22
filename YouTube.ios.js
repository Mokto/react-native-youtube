import React from 'react';
import PropTypes from 'prop-types';
import ReactNative, { requireNativeComponent, NativeModules, ViewPropTypes } from 'react-native';

const RCTYouTube = requireNativeComponent('RCTYouTube', null);

const parsePlayerParams = (props) => ({
  videoId: Array.isArray(props.videoIds) ? props.videoIds[0] : props.videoId,
  playlistId: props.playlistId,
  playerVars: {
    // videoIds are split to videoId and playlist (comma separated videoIds).
    // Also, looping a single video is unsupported by the iFrame player so we
    // must load the video as a 2 videos playlist, as suggested here:
    // https://developers.google.com/youtube/player_parameters#loop
    // whether its a looped videoId or a looped single video in videoIds
    playlist: Array.isArray(props.videoIds)
      ? props.loop && !props.videoIds[1]
        ? props.videoIds[0]
        : props.videoIds.slice(1).toString() || undefined
      : props.loop && props.videoId
      ? props.videoId
      : undefined,

    // No need to explicitly pass positive or negative defaults
    loop: props.loop === true ? 1 : undefined,
    playsinline: props.fullscreen === true ? undefined : 1,
    controls: props.controls,
    fs: props.showFullscreenButton === false ? 0 : undefined,
    showinfo: props.showinfo === false ? 0 : undefined,
    modestbranding: props.modestbranding === true ? 1 : undefined,
    rel: props.rel === false ? 0 : undefined,
    origin: props.origin,
  },
});

export default class YouTube extends React.Component {
  static propTypes = {
    videoId: PropTypes.string,
    videoIds: PropTypes.arrayOf(PropTypes.string),
    playlistId: PropTypes.string,
    play: PropTypes.bool,
    loop: PropTypes.bool,
    fullscreen: PropTypes.bool,
    controls: PropTypes.oneOf([0, 1, 2]),
    showinfo: PropTypes.bool,
    modestbranding: PropTypes.bool,
    showFullscreenButton: PropTypes.bool,
    rel: PropTypes.bool,
    origin: PropTypes.string,
    onError: PropTypes.func,
    onReady: PropTypes.func,
    onChangeState: PropTypes.func,
    onChangeQuality: PropTypes.func,
    onChangeFullscreen: PropTypes.func,
    onProgress: PropTypes.func,
    style: ViewPropTypes.style,
  };

  constructor(props) {
    super(props);

    // iOS uses a YouTube iFrame under the hood. We need to create its initial params
    // for a quick and clean load. After the initial loading, props changes will interact
    // with the iframe via its instance's methods so it won't need to load the iframe again.
    this.state = { playerParams: parsePlayerParams(props) };
  }

  shouldComponentUpdate() {
    // Prevent unnecessary renders before the native component is ready to accept them
    if (this._isReady) {
      return true;
    } else {
      return false;
    }
  }

  _onError = (event) => {
    if (this.props.onError) {
      this.props.onError(event.nativeEvent);
    }
  };

  _onReady = (event) => {
    // Force render to handle any props that have changed since mounting, and let the
    // component know it can render any future change
    this.forceUpdate();
    this._isReady = true;

    if (this.props.onReady) {
      this.props.onReady(event.nativeEvent);
    }
  };

  _onChangeState = (event) => {
    if (this.props.onChangeState) {
      this.props.onChangeState(event.nativeEvent);
    }
  };

  _onChangeQuality = (event) => {
    if (this.props.onChangeQuality) {
      this.props.onChangeQuality(event.nativeEvent);
    }
  };

  _onChangeFullscreen = (event) => {
    if (this.props.onChangeFullscreen) {
      this.props.onChangeFullscreen(event.nativeEvent);
    }
  };

  _onProgress = (event) => {
    if (this.props.onProgress) {
      this.props.onProgress(event.nativeEvent);
    }
  };

  seekTo(seconds) {
    NativeModules.YouTubeManager.seekTo(ReactNative.findNodeHandle(this), parseInt(seconds, 10));
  }

  nextVideo() {
    NativeModules.YouTubeManager.nextVideo(ReactNative.findNodeHandle(this));
  }

  previousVideo() {
    NativeModules.YouTubeManager.previousVideo(ReactNative.findNodeHandle(this));
  }

  playVideoAt(index) {
    NativeModules.YouTubeManager.playVideoAt(ReactNative.findNodeHandle(this), parseInt(index, 10));
  }

  getVideosIndex() {
    // Avoid calling the native method if there is only one video loaded for sure
    if ((Array.isArray(this.props.videoIds) && !this.props.videoIds[1]) || this.props.videoId) {
      return Promise.resolve(0);
    }

    return NativeModules.YouTubeManager.getVideosIndex(ReactNative.findNodeHandle(this));
  }

  getCurrentTime = () =>
    NativeModules.YouTubeManager.getCurrentTime(ReactNative.findNodeHandle(this));

  getDuration = () => NativeModules.YouTubeManager.getDuration(ReactNative.findNodeHandle(this));

  // iFrame vars like `playsInline`, `showinfo` etc. are set only on iFrame load.
  // This method will force a reload on the inner iFrame. Use it if you know the cost
  // and still wants to refresh the iFrame's vars
  reloadIframe() {
    this.setState({ playerParams: parsePlayerParams(this.props) });
  }

  setPlaybackRate(rate) {
    NativeModules.YouTubeManager.setPlaybackRate(ReactNative.findNodeHandle(this), rate);
  }

  getPlaybackRate() {
    return new Promise((resolve, reject) =>
      NativeModules.YouTubeManager.getPlaybackRate(ReactNative.findNodeHandle(this))
        .then(rate => resolve(rate))
        .catch(errorMessage => reject(errorMessage))
    );
  }

  render() {
    return (
      <RCTYouTube
        style={[{ overflow: 'hidden' }, this.props.style]}
        playerParams={this.state.playerParams}
        play={this.props.play}
        videoId={this.props.videoId}
        videoIds={this.props.videoIds}
        playlistId={this.props.playlistId}
        loopProp={this.props.loop}
        onError={this._onError}
        onReady={this._onReady}
        onChangeState={this._onChangeState}
        onChangeQuality={this._onChangeQuality}
        onChangeFullscreen={this._onChangeFullscreen}
        onProgress={this._onProgress}
      />
    );
  }
}
