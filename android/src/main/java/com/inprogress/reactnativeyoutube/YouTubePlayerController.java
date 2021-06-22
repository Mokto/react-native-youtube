package com.inprogress.reactnativeyoutube;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.os.Handler;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;


public class YouTubePlayerController implements
        YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlayerStateChangeListener,
        YouTubePlayer.PlaybackEventListener,
        YouTubePlayer.OnFullscreenListener {

    private YouTubePlayer mYouTubePlayer;
    private YouTubeView mYouTubeView;

    private static final int VIDEO_MODE = 0;
    private static final int VIDEOS_MODE = 1;
    private static final int PLAYLIST_MODE = 2;

    private boolean mIsLoaded = false;
    private boolean mIsReady = false;
    private int mMode = 0;
    private int mVideosIndex = 0;

    private String mVideoId = null;
    private List<String> mVideoIds = new ArrayList<String>();
    private String mPlaylistId = null;
    private boolean mPlay = false;
    private boolean mLoop = false;
    private boolean mFullscreen = false;
    private int mControls = 1;
    private boolean mShowFullscreenButton = true;
    private boolean mResumePlay = true;

    public YouTubePlayerController(YouTubeView youTubeView) {
        mYouTubeView = youTubeView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            mYouTubePlayer = youTubePlayer;
            mYouTubePlayer.setPlayerStateChangeListener(this);
            mYouTubePlayer.setPlaybackEventListener(this);
            mYouTubePlayer.setOnFullscreenListener(this);
            updateFullscreen();
            updateShowFullscreenButton();
            updateControls();

            if (mVideoId != null) loadVideo();
            else if (!mVideoIds.isEmpty()) loadVideos();
            else if (mPlaylistId != null) loadPlaylist();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result.isUserRecoverableError()) {
            result.getErrorDialog(mYouTubeView.getReactContext().getCurrentActivity(), 0).show();
        }
        mYouTubeView.receivedError(result.toString());
    }

    @Override
    public void onPlaying() {
        mYouTubeView.didChangeToState("playing");
    }

    @Override
    public void onPaused() {
        mYouTubeView.didChangeToState("paused");
    }

    @Override
    public void onStopped() {
        mYouTubeView.didChangeToState("stopped");
    }

    @Override
    public void onBuffering(boolean buffering) {
        if (buffering) mYouTubeView.didChangeToState("buffering");

        //Trick to remove when YouTube will patch it
        ProgressBar progressBar;
        try {
            // As of 2016-02-16, the ProgressBar is at position 0 -> 3 -> 2 in the view tree of the Youtube Player Fragment
            ViewGroup child1 = (ViewGroup) mYouTubeView.getChildAt(0);
            ViewGroup child2 = (ViewGroup) child1.getChildAt(3);
            progressBar = (ProgressBar) child2.getChildAt(2);
        } catch (Throwable t) {
            // As its position may change, we fallback to looking for it
            progressBar = findProgressBar(mYouTubeView);
        }

        int visibility = buffering ? View.VISIBLE : View.INVISIBLE;
        if (progressBar != null) progressBar.setVisibility(visibility);
    }

    @Override
    public void onSeekTo(int i) {
        mYouTubeView.didChangeToSeeking(i);
    }

    @Override
    public void onLoading() {
        mYouTubeView.didChangeToState("loading");
    }

    @Override
    public void onLoaded(String videoId) {
        if (isVideosMode()) setVideosIndex(mVideoIds.indexOf(videoId));

        if (!mIsReady) {
            mYouTubeView.playerViewDidBecomeReady();
            setLoaded(true);
            mIsReady = true;
        }
    }

    @Override
    public void onAdStarted() {
        mYouTubeView.didChangeToState("adStarted");
    }

    @Override
    public void onVideoStarted() {
        mYouTubeView.didChangeToState("started");
    }

    @Override
    public void onVideoEnded() {
        mYouTubeView.didChangeToState("ended");
        if (isLoop()) {
            if (isVideoMode()) loadVideo();
            else if (isVideosMode() && getVideosIndex() == mVideoIds.size() - 1) playVideoAt(0);
        }
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        mYouTubeView.didChangeToFullscreen(isFullscreen);
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        mYouTubeView.receivedError(errorReason.toString());
    }

    public void seekTo(int second) {
        if (isLoaded()) mYouTubePlayer.seekToMillis(second * 1000);
    }

    public int getCurrentTime() {
      return mYouTubePlayer.getCurrentTimeMillis() / 1000;
    }

    public int getDuration() {
      return mYouTubePlayer.getDurationMillis() / 1000;
    }

    public void nextVideo() {
        if (isLoaded()) {
            if (mYouTubePlayer.hasNext()) mYouTubePlayer.next();
            else if (isLoop()) {
                if (isVideosMode()) playVideoAt(0);
                else if (isPlaylistMode()) loadPlaylist();
                else loadVideo();
            }
        }
    }

    public void previousVideo() {
        if (isLoaded()) {
            if (mYouTubePlayer.hasPrevious()) mYouTubePlayer.previous();
            else if (isLoop()) {
                if (isVideosMode()) playVideoAt(mVideoIds.size() - 1);
                else if (isPlaylistMode()) loadPlaylist();
                else loadVideo();
            }
        }
    }

    public void playVideoAt(int index) {
        if (isLoaded() && isVideosMode()) {
            boolean indexIsInRange = setVideosIndex(index);
            if (indexIsInRange) loadVideos();
            else mYouTubeView.receivedError("Video index is out of bound for videoIds[]");
        }
    }

    /**
     * Private methods
     **/

    private void loadVideo() {
        if (isPlay()) mYouTubePlayer.loadVideo(mVideoId);
        else mYouTubePlayer.cueVideo(mVideoId);
        setVideosIndex(0);
        setVideoMode();
    }

    private void loadVideos() {
        if (isPlay()) mYouTubePlayer.loadVideos(mVideoIds, getVideosIndex(), 0);
        else mYouTubePlayer.cueVideos(mVideoIds, getVideosIndex(), 0);
        setVideosMode();
    }

    private void loadPlaylist() {
        if (isPlay()) mYouTubePlayer.loadPlaylist(mPlaylistId);
        else mYouTubePlayer.cuePlaylist(mPlaylistId);
        setVideosIndex(0);
        setPlaylistMode();
    }

    private void updateControls() {
        switch (mControls) {
            case 0:
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                break;
            case 1:
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                break;
            case 2:
                mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                break;
        }
    }

    private void updateFullscreen() {
        mYouTubePlayer.setFullscreen(mFullscreen);
    }

    private void updateShowFullscreenButton() {
        mYouTubePlayer.setShowFullscreenButton(mShowFullscreenButton);
    }

    private ProgressBar findProgressBar(View view) {
        if (view instanceof ProgressBar) {
            return (ProgressBar) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                ProgressBar res = findProgressBar(viewGroup.getChildAt(i));
                if (res != null) return res;
            }
        }
        return null;
    }

    /**
     * Getters & Setters
     **/

    private void setLoaded(boolean loaded) {
        mIsLoaded = loaded;
    }

    private boolean isLoaded() {
        return mIsLoaded;
    }

    private void setVideoMode() {
        mMode = VIDEO_MODE;
    }

    private boolean isVideoMode() {
        return (mMode == VIDEO_MODE);
    }

    private void setVideosMode() {
        mMode = VIDEOS_MODE;
    }

    private boolean isVideosMode() {
        return (mMode == VIDEOS_MODE);
    }

    private void setPlaylistMode() {
        mMode = PLAYLIST_MODE;
    }

    private boolean isPlaylistMode() {
        return (mMode == PLAYLIST_MODE);
    }

    private boolean setVideosIndex(int index) {
        if (index >= 0 && index < mVideoIds.size()) {
            mVideosIndex = index;
            return true;
        } else return false;
    }

    public int getVideosIndex() {
        return mVideosIndex;
    }

    public void onVideoFragmentResume() {
        if (isResumePlay() && mYouTubePlayer != null) {
            // For some reason calling mYouTubePlayer.play() right away is ineffective
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mYouTubePlayer.play();
                    } catch (IllegalStateException e) {
                        mYouTubeView.initPlayer();
                    }
                }
            }, 1);
        }
    }

    private boolean isPlay() {
        return mPlay;
    }

    private boolean isLoop() {
        return mLoop;
    }

    private boolean isFullscreen() {
        return mFullscreen;
    }

    private int getControls() {
        return mControls;
    }

    private boolean isResumePlay() {
        return mResumePlay;
    }

    /**
     * React Props
     */

    public void setVideoId(String videoId) {
        mVideoId = videoId;
        if (isLoaded()) loadVideo();
    }

    public void setVideoIds(ReadableArray videoIds) {
        if (videoIds != null) {
            setVideosIndex(0);
            mVideoIds.clear();
            for (int i = 0; i < videoIds.size(); i++) {
                mVideoIds.add(videoIds.getString(i));
            }
            if (isLoaded()) loadVideos();
        }
    }

    public void setPlaylistId(String playlistId) {
        mPlaylistId = playlistId;
        if (isLoaded()) loadPlaylist();
    }

    public void setPlay(boolean play) {
        mPlay = play;
        if (isLoaded()) {
            if (isPlay()) mYouTubePlayer.play();
            else mYouTubePlayer.pause();
        }
    }

    public void setLoop(boolean loop) {
        mLoop = loop;
    }

    public void setFullscreen(boolean fullscreen) {
        mFullscreen = fullscreen;
        if (isLoaded()) updateFullscreen();
    }

    public void setControls(int controls) {
        if (controls >= 0 && controls <= 2) {
            mControls = controls;
            if (isLoaded()) updateControls();
        }
    }

    public void setShowFullscreenButton(boolean show) {
        mShowFullscreenButton = show;
        if (isLoaded()) updateShowFullscreenButton();
    }

    public void setResumePlay(boolean resumePlay) {
        mResumePlay = resumePlay;
    }
}
