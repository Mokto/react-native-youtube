import * as React from 'react';
import YouTube, { YouTubeStandaloneAndroid, YouTubeStandaloneIOS } from '../main';

const YouTubeTest = () => {
  YouTubeStandaloneAndroid.playVideo({
    apiKey: 'foo',
    videoId: 'bar',
  });
  YouTubeStandaloneIOS.playVideo('foo');
  const videoRef = React.useRef<YouTube>(null)
  React.useEffect(()=>{
    videoRef.current.getCurrentTime()
    videoRef.current.nextVideo()
    videoRef.current.previousVideo()
    videoRef.current.reloadIframe()
    videoRef.current.getDuration()
    videoRef.current.playVideoAt(1)
    videoRef.current.seekTo(1000)
  },[])
  return (
    <YouTube
      ref={videoRef}
      videoId={'foo'}
      videoIds={['foo', 'bar']}
      playlistId={'foo'}
      play
      loop
      fullscreen
      controls={1}
      showinfo
      modestbranding
      showFullscreenButton
      rel
      origin={'foo'}
      onError={(event) => {}}
      onReady={(event) => {}}
      onChangeState={(event) => {}}
      onChangeQuality={(event) => {}}
      onChangeFullscreen={(event) => {}}
      onProgress={(event) => {}}
      style={{ flex: 1 }}
    />
  );
};
