#import "RCTYouTubeManager.h"
#import "RCTYouTube.h"
#import <React/RCTUIManager.h>

@implementation RCTYouTubeManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;

- (UIView *)view {
    return [[RCTYouTube alloc] initWithBridge:self.bridge];
}

- (dispatch_queue_t)methodQueue {
    return _bridge.uiManager.methodQueue;
}

RCT_EXPORT_VIEW_PROPERTY(playerParams, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(videoId, NSString);
RCT_EXPORT_VIEW_PROPERTY(videoIds, NSArray);
RCT_EXPORT_VIEW_PROPERTY(playlistId, NSString);
RCT_EXPORT_VIEW_PROPERTY(play, BOOL);
RCT_EXPORT_VIEW_PROPERTY(loopProp, BOOL);

RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onReady, RCTDirectEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onChangeState, RCTDirectEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onChangeQuality, RCTDirectEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onChangeFullscreen, RCTDirectEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onProgress, RCTDirectEventBlock);

RCT_EXPORT_METHOD(seekTo:(nonnull NSNumber *)reactTag seconds:(nonnull NSNumber *)seconds)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];
        
        [youtube seekToSeconds:seconds.floatValue allowSeekAhead:YES];
    }];
}

RCT_EXPORT_METHOD(nextVideo:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube nextVideo];
    }];
}

RCT_EXPORT_METHOD(previousVideo:(nonnull NSNumber *)reactTag)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube previousVideo];
    }];
}

RCT_EXPORT_METHOD(playVideoAt:(nonnull NSNumber *)reactTag index:(nonnull NSNumber *)index)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube playVideoAt:(int)[index integerValue]];
    }];
}

RCT_EXPORT_METHOD(getVideosIndex:(nonnull NSNumber *)reactTag resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube getPlaylistIndex:^(int response, NSError * _Nullable error) {
            if (error) {
                reject(@"Error getting index of video from RCTYouTube", @"", error);
            } else {
                NSNumber *index = [NSNumber numberWithInt:response];
                resolve(index);
            }
        }];
    }];
}

RCT_EXPORT_METHOD(getCurrentTime:(nonnull NSNumber *)reactTag resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube getCurrentTime:^(float response, NSError * _Nullable error) {
            if (error) {
                reject(@"Error getting current time of video from RCTYouTube", @"", error);
            } else {
                NSNumber *currentTime = [NSNumber numberWithInt:response];
                resolve(currentTime);
            }
        }];
    }];
}

RCT_EXPORT_METHOD(getDuration:(nonnull NSNumber *)reactTag resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube getDuration:^(NSTimeInterval response, NSError * _Nullable error) {
            if (error) {
                reject(@"Error getting duration of video from RCTYouTube", @"", error);
            } else {
                NSNumber *duration = [NSNumber numberWithInt:response];
                resolve(duration);
            }
        }];
    }];
}

RCT_EXPORT_METHOD(playbackRate:(nonnull NSNumber *)reactTag resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];

        [youtube getPlaybackRate:^(float response, NSError * _Nullable error) {
            if (error) {
                reject(@"Error getting playback rate of video from RCTYouTube", @"", error);
            } else {
                NSNumber *rate = [NSNumber numberWithInt:response];
                resolve(rate);
            }
        }];
    }];
}

RCT_EXPORT_METHOD(setPlaybackRate:(nonnull NSNumber *)reactTag rate:(nonnull NSNumber *)rate)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        RCTYouTube *youtube = (RCTYouTube*)viewRegistry[reactTag];
        if ([youtube isKindOfClass:[RCTYouTube class]]) {
            [youtube setPlaybackRate:(float)[rate floatValue]];
        } else {
            RCTLogError(@"Cannot setPlaybackRate: %@ (tag #%@) is not RCTYouTube", youtube, reactTag);
        }
    }];
}

@end
