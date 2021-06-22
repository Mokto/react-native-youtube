package com.inprogress.reactnativeyoutube;

import android.app.FragmentManager;
import android.os.Build;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;


public class YouTubeView extends FrameLayout {

    private YouTubePlayerController mYouTubeController;
    private VideoFragment mVideoFragment;
    private boolean mHasSavedInstance = false;
    private String mApiKey;

    public YouTubeView(ReactContext context) {
        super(context);
        init();
    }

    public ReactContext getReactContext() {
        return (ReactContext) getContext();
    }

    public void init() {
        inflate(getContext(), R.layout.youtube_layout, this);
        mVideoFragment = VideoFragment.newInstance(this);
        mYouTubeController = new YouTubePlayerController(this);
        mApiKey = null;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        mHasSavedInstance = true;
        return super.onSaveInstanceState();
    }

    @Override
    protected void onAttachedToWindow() {
        if (!mHasSavedInstance) {
            FragmentManager fragmentManager = getReactContext().getCurrentActivity().getFragmentManager();
            fragmentManager.beginTransaction().add(getId(), mVideoFragment).commitAllowingStateLoss();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (getReactContext().getCurrentActivity() != null) {
            FragmentManager fragmentManager = getReactContext().getCurrentActivity().getFragmentManager();

            // Code crashes with java.lang.IllegalStateException: Activity has been destroyed
            // if our activity has been destroyed when this runs
            if (mVideoFragment != null) {
                boolean isDestroyed = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    isDestroyed = getReactContext().getCurrentActivity().isDestroyed();
                }

                if (!isDestroyed) {
                    // https://stackoverflow.com/a/34508430/61072
                    fragmentManager.beginTransaction().remove(mVideoFragment).commitAllowingStateLoss();
                }
            }
        }
        super.onDetachedFromWindow();
    }

    public void seekTo(int second) {
        mYouTubeController.seekTo(second);
    }

    public int getCurrentTime() {
        return mYouTubeController.getCurrentTime();
    }

    public int getDuration() {
        return mYouTubeController.getDuration();
    }

    public void nextVideo() {
        mYouTubeController.nextVideo();
    }

    public void previousVideo() {
        mYouTubeController.previousVideo();
    }

    public void playVideoAt(int index) {
        mYouTubeController.playVideoAt(index);
    }

    public int getVideosIndex() {
        return mYouTubeController.getVideosIndex();
    }

    public void onVideoFragmentResume() {
        mYouTubeController.onVideoFragmentResume();
    }

    public void receivedError(String param) {
        WritableMap event = Arguments.createMap();
        ReactContext reactContext = getReactContext();
        event.putString("error", param);
        event.putInt("target", getId());
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "error", event);
    }

    public void playerViewDidBecomeReady() {
        WritableMap event = Arguments.createMap();
        ReactContext reactContext = getReactContext();
        event.putInt("target", getId());
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "ready", event);
    }

    public void didChangeToSeeking(int milliSeconds) {
        WritableMap event = Arguments.createMap();
        event.putString("state", "seeking");
        event.putInt("currentTime", milliSeconds / 1000);
        event.putInt("target", getId());
        ReactContext reactContext = getReactContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "state", event);
    }

    public void didChangeToState(String param) {
        WritableMap event = Arguments.createMap();
        event.putString("state", param);
        event.putInt("target", getId());
        ReactContext reactContext = getReactContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "state", event);
    }

    public void didChangeToQuality(String param) {
        WritableMap event = Arguments.createMap();
        event.putString("quality", param);
        event.putInt("target", getId());
        ReactContext reactContext = getReactContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "quality", event);
    }

    public void didChangeToFullscreen(boolean isFullscreen) {
        WritableMap event = Arguments.createMap();
        ReactContext reactContext = getReactContext();
        event.putBoolean("isFullscreen", isFullscreen);
        event.putInt("target", getId());
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "fullscreen", event);
    }

    public void initPlayer() {
        try {
            mVideoFragment.initialize(mApiKey, mYouTubeController);
        } catch (Exception e) {
            receivedError(e.getMessage());
        }
    }

    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
        initPlayer();
    }

    public void setVideoId(String str) {
        mYouTubeController.setVideoId(str);
    }

    public void setVideoIds(ReadableArray arr) {
        mYouTubeController.setVideoIds(arr);
    }

    public void setPlaylistId(String str) {
        mYouTubeController.setPlaylistId(str);
    }

    public void setPlay(boolean bool) {
        mYouTubeController.setPlay(bool);
    }

    public void setLoop(boolean bool) {
        mYouTubeController.setLoop(bool);
    }

    public void setFullscreen(boolean bool) {
        mYouTubeController.setFullscreen(bool);
    }

    public void setControls(int nb) {
        mYouTubeController.setControls(nb);
    }

    public void setShowFullscreenButton(boolean bool) {
        mYouTubeController.setShowFullscreenButton(bool);
    }

    public void setResumePlay(boolean bool) {
        mYouTubeController.setResumePlay(bool);
    }
}
