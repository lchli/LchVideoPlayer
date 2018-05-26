package com.lch.video_player;

import android.content.Context;
import android.support.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;
import com.lch.video_player.helper.Config;
import com.lch.video_player.internal.MediaCacheProxy;
import com.lch.video_player.internal.VideoPlayerProxy;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by bbt-team on 2017/9/28.
 */

public final class LchVideoPlayer {

    private LchVideoPlayer() {
    }

    /**
     * 创建一个默认播放器，基于ijk player实现。
     *
     * @param context
     * @return
     */
    @NonNull
    public static VideoPlayer newPlayer(@NonNull Context context) {
        return newPlayer(context, VideoPlayer.Type.IJK);
    }

    /**
     * 创建一个播放器，可指定类型。
     *
     * @param context
     * @param type    {@link VideoPlayer.Type#ANDROID},{@link VideoPlayer.Type#EXO},{@link VideoPlayer.Type#IJK}
     * @return
     */
    @NonNull
    public static VideoPlayer newPlayer(@NonNull Context context, @NonNull VideoPlayer.Type type) {
        return newPlayer(context, type, MediaCacheProxy.get(context));
    }


    /**
     * 创建一个播放器，可指定video缓存代理对象。
     *
     * @param context
     * @param type        {@link VideoPlayer.Type#ANDROID},{@link VideoPlayer.Type#EXO},{@link VideoPlayer.Type#IJK}
     * @param cacheServer 视频流缓存代理对象。
     * @return
     */
    @NonNull
    public static VideoPlayer newPlayer(@NonNull Context context, @NonNull VideoPlayer.Type type, @NonNull HttpProxyCacheServer cacheServer) {
        switch (type) {
            case ANDROID:
                return new VideoPlayerProxy(new AndroidMediaPlayer(), cacheServer);
            case EXO:
                return new VideoPlayerProxy(new IjkExoMediaPlayer(context.getApplicationContext()), cacheServer);
            case IJK:
                return new VideoPlayerProxy(new IjkMediaPlayer(), cacheServer);
        }
        return null;
    }

    /**
     * 日志开关。
     *
     * @param enable
     */
    public static void setLogEnable(boolean enable) {
        Config.logger.setLogEnable(enable);
    }
}
