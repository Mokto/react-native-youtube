# react-native-youtube [![react-native-youtube](http://img.shields.io/npm/dm/react-native-youtube.svg)](https://www.npmjs.org/package/react-native-youtube) [![npm version](https://badge.fury.io/js/react-native-youtube.svg)](http://badge.fury.io/js/react-native-youtube) [![Dependency Status](https://david-dm.org/davidohayon669/react-native-youtube.svg)](https://david-dm.org/davidohayon669/react-native-youtube)

A `<YouTube />` component for React Native.

Uses [YoutubePlayer-in-WKWebView](https://github.com/hmhv/YoutubePlayer-in-WKWebView) for iOS and [YouTube Android Player API](https://developers.google.com/youtube/android/player/) for Android and exposes much of the API, as declaratively as possible, into React Native.

[Having problems with Android? Please read this first](https://github.com/davidohayon669/react-native-youtube#known-issues)

**Important!!!** - This README is for v2. [Latest README for v1 can be found here](https://github.com/davidohayon669/react-native-youtube/blob/v1.1.0/README.md)

## Table of Contents

- [Screenshot](#screenshot)
- [Install](#install)
- [Usage](#usage)
- [API](#api)
- [Known Issues](#known-issues)
- [Example App and Development](#example-app-and-development)
- [Authors](#authors)
- [License](#license)

## Screenshot

![Screenshot of the example app](https://github.com/davidohayon669/react-native-youtube/raw/master/Screenshot.png)

## Install

Install the latest version to your `package.json`:

`$ npm install react-native-youtube -S`

React Native automatically connects this native module to your iOS and Android projects. On Android this linking is supported with Gradle and is done automatically after installation. On iOS the linking is done by Cocoapods, without the need to add this library to the `Podfile`, Just run `pod install` after installation.

**IMPORTANT! (Android Only)**: The Android implementation of this component needs to have the official YouTube app installed on the device. Otherwise the user will be prompted to install / activate the app, and an error event will be triggered with `SERVICE_MISSING`/`SERVICE_DISABLED`.

#### OPTIONAL: Activated sound when iPhone (iOS) is on vibrate mode

Open AppDelegate.m and add :

- `#import <AVFoundation/AVFoundation.h>`

- `[[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error: nil];` in your didFinishLaunchingWithOptions method

## Usage

```jsx
<YouTube
  videoId="KVZ-P-ZI6W4" // The YouTube video ID
  play // control playback of video with true/false
  fullscreen // control whether the video should play in fullscreen or inline
  loop // control whether the video should loop when ended
  onReady={e => this.setState({ isReady: true })}
  onChangeState={e => this.setState({ status: e.state })}
  onChangeQuality={e => this.setState({ quality: e.quality })}
  onError={e => this.setState({ error: e.error })}
  style={{ alignSelf: 'stretch', height: 300 }}
/>
```

## API

### YouTube Component

#### Importing

```javascript
import YouTube from 'react-native-youtube';
```

#### Properties

- `apiKey` (string, _Android_): Your YouTube developer API Key. This parameter is **required**. [More Info](https://developers.google.com/youtube/android/player/register).
- `videoId` (string): The YouTube video ID to play. Can be changed while mounted to change the video playing.
- `videoIds` (strings array): YouTube video IDs to be played as an interactive playlist. Can be changed while mounted. Overridden at start by `videoId`.
- `playlistId` (string): A YouTube Playlist's ID to play as an interactive playlist.
  Can be changed while mounted. Overridden at start by `videoId` and `videoIds`.
- `play` (boolean): Controls playback of video with `true`/`false`. Setting it as `true` in the beginning itself makes the video autoplay on loading. Default: `false`.
- `loop` (boolean): Loops the video. Default: `false`.
- `fullscreen` (boolean): Controls whether the video should play inline or in fullscreen. Default: `false`.
- `controls` (number): Sets the player's controls scheme. Supported values are `0`, `1`, `2`. Default: `1`. On iOS the numbers conform to [These Parameters](https://developers.google.com/youtube/player_parameters?hl=en#controls). On Android the mapping is `0 = CHROMELESS`, `1 = DEFAULT`, `2 = MINIMAL` ([More Info](https://developers.google.com/youtube/android/player/reference/com/google/android/youtube/player/YouTubePlayer.PlayerStyle)).
- `showFullscreenButton` (boolean): Show or hide Fullscreen button. Default: `true`.
- `showinfo` (boolean, _iOS_): Setting the parameter's value to false causes the player to not display information like the video title and uploader before the video starts playing. Default: `true`.
- `modestbranding` (boolean, _iOS_): This parameter lets you use a YouTube player that does not show a YouTube logo. Default: `false`.
- `origin` (string, _iOS_): This parameter provides an extra security measure for the iFrame API.
- `rel` (boolean, _iOS_): Show related videos at the end of the video. Default: `true`.
- `resumePlayAndroid` (boolean, _Android_): Makes the video resume playback after the app resumes from background. Default: `true`.

The iOS implementation of this player uses the official YouTube iFrame under the hood, so most parameters behavior [can be further understood here.](https://developers.google.com/youtube/player_parameters)

#### Events

- `onReady`: Called once when the video player is setup.
- `onChangeState`: Sends the current state of the player on `e.state`. Common values are `buffering`/`playing`/`paused` and more (on Android there is also a `seeking` state that comes with the location of the playback in seconds on `e.currentTime`).
- `onChangeQuality`: Sends the current quality of video playback on `e.quality`.
- `onError`: Sends any errors before and during video playback on `e.error`.
- `onChangeFullscreen`: Called when the player enters or exits the fullscreen mode on `e.isFullscreen`.
- `onProgress` _(iOS)_: Called every 500ms with the time progress of the playback on `e.currentTime`.

#### Methods

- `seekTo(seconds)`: Seeks to a specified time in the video.
- `nextVideo()`: Skip to next video on a playlist (`videoIds` or `playlistId`). When `loop` is true, will skip to the first video from the last. If called on a single video, will restart the video.
- `previousVideo()`: opposite of `nextVideo()`.
- `playVideoAt(index)`: Will start playing the video at `index` (zero-based) position in a playlist (`videoIds` or `playlistId`. Not supported for `playlistId` on Android).
- `getVideosIndex()`: Returns a Promise that results with the `index` (zero-based) number of the video currently played in a playlist (`videoIds` or `playlistId`. Not supported for `playlistId` on Android) or errors with an errorMessage string.
- `getCurrentTime()`: Returns a Promise that results with the `currentTime` of the played video (in seconds) or errors with an errorMessage string. Should be used as an alternative for Android to `onProgress` event on iOS.
- `getDuration()`: Returns a Promise that results with the `duration` of the played video (in seconds) or errors with an errorMessage string. Should be used as an alternative for Android to `onProgress` event on iOS.
- `reloadIframe()` _(iOS)_: Specific props (`fullscreen`, `modestbranding`, `showinfo`, `rel`, `controls`, `origin`) can only be set at mounting and initial loading of the underlying WebView that holds the YouTube iFrame (Those are `<iframe>` parameters). If you want to change one of them during the lifecycle of the component, you should know the usability cost of loading the WebView again, and use this method right after the component received the updated prop.
* `getPlaybackRate()` *(iOS)*: Returns the playback rate of the currently playing video.
* `setPlaybackRate(rate)` *(iOS)*: Sets the suggested playback rate for the current video.

### Standalone Player (iOS)

#### Setup

Standalone iOS player Uses [XCDYoutubeKit](https://github.com/0xced/XCDYouTubeKit) (**Warning**: XCDYoutubeKit doesn't conform to YouTube's Terms Of Use). Add the next line to your `Podfile` and run `pod install` inside `/ios` folder:

```
pod 'XCDYouTubeKit', '~> 2.8'
```

#### Importing

```javascript
import { YouTubeStandaloneIOS } from 'react-native-youtube';
```

#### Example

```javascript
YouTubeStandaloneIOS.playVideo('KVZ-P-ZI6W4')
  .then(message => console.log(message))
  .catch(errorMessage => console.error(errorMessage));
```

#### `YouTubeStandaloneIOS.playVideo(videoId)` (Static)

A static method that returns a Promise to launch a standalone YouTube player with a specific video.

- `videoId` (string): The YouTube Video ID to play. **Required**.

### Standalone Player (Android)

#### Importing

```javascript
import { YouTubeStandaloneAndroid } from 'react-native-youtube';
```

#### Example

```javascript
YouTubeStandaloneAndroid.playVideo({
  apiKey: 'YOUR_API_KEY', // Your YouTube Developer API Key
  videoId: 'KVZ-P-ZI6W4', // YouTube video ID
  autoplay: true, // Autoplay the video
  startTime: 120, // Starting point of video (in seconds)
})
  .then(() => console.log('Standalone Player Exited'))
  .catch(errorMessage => console.error(errorMessage));
```

#### `YouTubeStandaloneAndroid.playVideo(options)` (Static)

A static method that returns a Promise to launch a standalone YouTube player with a specific video.

`options` is an object with properties:

- `apiKey` (string): Your developer YouTube API key. **Required**.
- `videoId` (string): The YouTube Video ID to play. **Required**.
- `autoplay` (boolean): Should the video start automatically, Default: `false`.
- `lightboxMode` (boolean): Should the video play inside a lightbox instead of fullscreen. Default: `false`.
- `startTime` (number): Optional starting time of the video (in seconds). Default: `0`.

#### `YouTubeStandaloneAndroid.playVideos(options)` (Static)

A static method that returns a Promise to launch a standalone YouTube player with a specific video.

`options` is an object with properties:

- `apiKey` (string): Your developer YouTube API key. **Required**.
- `videoIds` (strings array): The list of video IDs to be played. **Required**.
- `autoplay` (boolean): Should the video start automatically, Default: `false`.
- `lightboxMode` (boolean): Should the video play inside a lightbox instead of fullscreen. Default: `false`.
- `startIndex` (number): The index position of the video to play first. Default: `0`.
- `startTime` (number): Optional starting time of the video (in seconds). Default: `0`.

#### `YouTubeStandaloneAndroid.playPlaylist(options)` (Static)

A static method that returns a Promise to launch a standalone YouTube player with a specific video.

`options` is an object with properties:

- `apiKey` (string): Your developer YouTube API key. **Required**.
- `playlistId` (string): The YouTube Playlist ID to play. **Required**.
- `autoplay` (boolean): Should the video start automatically, Default: `false`.
- `lightboxMode` (boolean): Should the video play inside a lightbox instead of fullscreen. Default: `false`.
- `startIndex` (number): The index position of the video to play first. Default: `0`.
- `startTime` (number): Optional starting time of the video (in seconds). Default: `0`.

## Known Issues

#### `UNAUTHORIZED_OVERLAY` and `PLAYER_VIEW_TOO_SMALL` on Android

The Android version of this component is based on the official Java [YouTube Android Player API](https://developers.google.com/youtube/android/player/) which limits the ability to [cover player view](https://developers.google.com/youtube/android/player/reference/com/google/android/youtube/player/YouTubePlayer.ErrorReason.html#public-static-final-youtubeplayer.errorreason-unauthorized_overlay), or [render it too small](https://developers.google.com/youtube/android/player/reference/com/google/android/youtube/player/YouTubePlayer.ErrorReason.html#public-static-final-youtubeplayer.errorreason-player_view_too_small). Some React Native components such as navigation ones can interfere with these limitations in unpredictable ways. This issue is discussed in [#161](https://github.com/davidohayon669/react-native-youtube/issues/161).

#### Changing `videoId` / `videoIds` / `playlistId` while the component is mounted in Android

The Android API has bugs when props that identify the videos are changed during the lifecycle of the native component. For now it is recommended to re-mount a new `<YouTube />` instance each time there is a need to replace the video or playlist of a player. Also, looping through a `playlist` or `videoIds` is broken due to the underlying library's bugs.

#### Multiple `<YouTube />` instances on Android

The YouTube API for Android is a _singleton_. What it means is that unlike the iOS implementation, no two players can be mounted and play a video at the same time. If you have two scenes that happen to live together, or come one after the other (such as when navigating to a new scene), The new `<YouTube />` Will take the focus of the singleton and play the video, but after being unmounted, the older mounted `<YouTube />` will not be able to take the role back, and will need to be re-mounted.

## Example App and Development

This repository includes an example project that can be used for trying, developing and testing all functionalities on a dedicated clean app project.

First, copy the git repository and install the React-Native project inside `/example`:

```sh
$ git clone https://github.com/davidohayon669/react-native-youtube.git
$ cd react-native-youtube/example
$ npm install
```

For iOS, also install the Cocoapods

```sh
$ cd ios
$ pod install
```

Then build and run with `react-native run-ios` / `react-native run-android` or your preferred IDE.

#### For Developers

To be able to directly test your changes with the example app, re-install the package from the root directory with `npm run install-root` after each change. This command packs the root directory into an npm package `.tar` file and installs it locally to the example app.

## Authors

- Param Aggarwal (paramaggarwal@gmail.com)
- David Ohayon ([@davidohayon669](https://twitter.com/davidohayon669))

## License

MIT License
