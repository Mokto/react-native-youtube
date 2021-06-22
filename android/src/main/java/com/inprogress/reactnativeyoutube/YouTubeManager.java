package com.inprogress.reactnativeyoutube;

import androidx.annotation.Nullable;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;


public class YouTubeManager extends SimpleViewManager<YouTubeView> {

    private static final int COMMAND_SEEK_TO = 1;
    private static final int COMMAND_NEXT_VIDEO = 2;
    private static final int COMMAND_PREVIOUS_VIDEO = 3;
    private static final int COMMAND_PLAY_VIDEO_AT = 4;

    @Override
    public String getName() {
        return "ReactYouTube";
    }

    @Override
    protected YouTubeView createViewInstance(ThemedReactContext themedReactContext) {
        return new YouTubeView(themedReactContext);
    }

    @Override
    public Map<String,Integer> getCommandsMap() {
        return MapBuilder.of(
            "seekTo",
            COMMAND_SEEK_TO,
            "nextVideo",
            COMMAND_NEXT_VIDEO,
            "previousVideo",
            COMMAND_PREVIOUS_VIDEO,
            "playVideoAt",
            COMMAND_PLAY_VIDEO_AT
        );
    }

    @Override
    public void receiveCommand(YouTubeView view, int commandType, @Nullable ReadableArray args) {
        if (view == null || args == null) {
            throw new AssertionError();
        }
        switch (commandType) {
            case COMMAND_SEEK_TO: {
                view.seekTo(args.getInt(0));
                return;
            }
            case COMMAND_NEXT_VIDEO: {
                view.nextVideo();
                return;
            }
            case COMMAND_PREVIOUS_VIDEO: {
                view.previousVideo();
                return;
            }
            case COMMAND_PLAY_VIDEO_AT: {
                view.playVideoAt(args.getInt(0));
                return;
            }
            default:
                throw new IllegalArgumentException(
                  String.format("Unsupported command %d received by %s.", commandType, getClass().getSimpleName())
                );
        }
    }

    @Override
    public @Nullable Map <String,Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
            "error",
            (Object) MapBuilder.of("registrationName", "onYouTubeError"),
            "ready",
            (Object) MapBuilder.of("registrationName", "onYouTubeReady"),
            "state",
            (Object) MapBuilder.of("registrationName", "onYouTubeChangeState"),
            "quality",
            (Object) MapBuilder.of("registrationName", "onYouTubeChangeQuality"),
            "fullscreen",
            (Object) MapBuilder.of("registrationName", "onYouTubeChangeFullscreen")
        );
    }

    public int getCurrentTime(YouTubeView view) {
        return view.getCurrentTime();
    }

    public int getDuration(YouTubeView view) {
        return view.getDuration();
    }

    public int getVideosIndex(YouTubeView view) {
        return view.getVideosIndex();
    }

    @ReactProp(name = "apiKey")
    public void setApiKey(YouTubeView view, @Nullable String param) {
        view.setApiKey(param);
    }

    @ReactProp(name = "videoId")
    public void setPropVideoId(YouTubeView view, @Nullable String param) {
        view.setVideoId(param);
    }

    @ReactProp(name = "videoIds")
    public void setPropVideoIds(YouTubeView view, @Nullable ReadableArray param) {
        view.setVideoIds(param);
    }

    @ReactProp(name = "playlistId")
    public void setPropPlaylistId(YouTubeView view, @Nullable String param) {
        view.setPlaylistId(param);
    }

    @ReactProp(name = "play")
    public void setPropPlay(YouTubeView view, @Nullable boolean param) {
        view.setPlay(param);
    }

    @ReactProp(name = "loop")
    public void setPropLoop(YouTubeView view, @Nullable boolean param) {
        view.setLoop(param);
    }

    @ReactProp(name = "fullscreen")
    public void setPropFullscreen(YouTubeView view, @Nullable boolean param) {
        view.setFullscreen(param);
    }

    @ReactProp(name = "controls")
    public void setPropControls(YouTubeView view, @Nullable int param) {
        view.setControls(param);
    }

    @ReactProp(name = "showFullscreenButton")
    public void setPropShowFullscreenButton(YouTubeView view, @Nullable boolean param) {
        view.setShowFullscreenButton(param);
    }

    @ReactProp(name = "resumePlayAndroid")
    public void setPropResumePlay(YouTubeView view, @Nullable boolean param) {
        view.setResumePlay(param);
    }
}
