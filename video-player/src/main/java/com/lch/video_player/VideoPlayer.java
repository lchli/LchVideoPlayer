package com.lch.video_player;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.lch.video_player.model.BAFTimedText;

import java.io.FileDescriptor;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by bbt-team on 2017/9/28.
 */

public interface VideoPlayer {

    int MEDIA_INFO_UNKNOWN = IMediaPlayer.MEDIA_INFO_UNKNOWN;
    int MEDIA_INFO_STARTED_AS_NEXT = IMediaPlayer.MEDIA_INFO_STARTED_AS_NEXT;
    int MEDIA_INFO_VIDEO_RENDERING_START = IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;
    int MEDIA_INFO_VIDEO_TRACK_LAGGING = IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING;
    int MEDIA_INFO_BUFFERING_START = IMediaPlayer.MEDIA_INFO_BUFFERING_START;
    int MEDIA_INFO_BUFFERING_END = IMediaPlayer.MEDIA_INFO_BUFFERING_END;
    int MEDIA_INFO_NETWORK_BANDWIDTH = IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH;
    int MEDIA_INFO_BAD_INTERLEAVING = IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING;
    int MEDIA_INFO_NOT_SEEKABLE = IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE;
    int MEDIA_INFO_METADATA_UPDATE = IMediaPlayer.MEDIA_INFO_METADATA_UPDATE;
    int MEDIA_INFO_TIMED_TEXT_ERROR = IMediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR;
    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE;
    int MEDIA_INFO_SUBTITLE_TIMED_OUT = IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT;
    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED;
    int MEDIA_INFO_AUDIO_RENDERING_START = IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START;
    int MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE = IMediaPlayer.MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE;
    int MEDIA_ERROR_UNKNOWN = IMediaPlayer.MEDIA_ERROR_UNKNOWN;
    int MEDIA_ERROR_SERVER_DIED = IMediaPlayer.MEDIA_ERROR_SERVER_DIED;
    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
    int MEDIA_ERROR_IO = IMediaPlayer.MEDIA_ERROR_IO;
    int MEDIA_ERROR_MALFORMED = IMediaPlayer.MEDIA_ERROR_MALFORMED;
    int MEDIA_ERROR_UNSUPPORTED = IMediaPlayer.MEDIA_ERROR_UNSUPPORTED;
    int MEDIA_ERROR_TIMED_OUT = IMediaPlayer.MEDIA_ERROR_TIMED_OUT;
    int MEDIA_ERROR_EXCEPTION = -888;

    enum Type {
        ANDROID,
        EXO,
        IJK
    }

    abstract class StateListener {

        /**
         * 播放发生错误的回调。
         *
         * @param what  {@link VideoPlayer#MEDIA_ERROR_UNKNOWN},etc
         * @param extra
         */
        public void onError(int what, int extra) {

        }

        /**
         * 播放完成的回调。
         */
        public void onCompletion() {
        }

        /**
         * 播放缓冲更新时回调。
         *
         * @param percent 已经完成的百分比。范围(0-100)
         */
        public void onBufferingUpdate(int percent) {

        }

        /**
         * 播放进度更新回调。
         *
         * @param currentPositionMillsecs 当前进度，单位毫秒
         * @param totalMillsecs           总时间，单位毫秒
         */
        public void onProgress(long currentPositionMillsecs, long totalMillsecs) {

        }

        /**
         * 视频宽高改变。
         *
         * @param width
         * @param height
         */
        public void onVideoSizeChanged(int width, int height) {

        }

        /**
         * 视频文字回调。
         *
         * @param text
         */
        public void onTimedText(BAFTimedText text) {

        }


        /**
         * 播放准备好时的回调。
         */
        public void onPrepared() {

        }

        /**
         * 普通播放信息回调。
         *
         * @param what  {@link VideoPlayer#MEDIA_INFO_UNKNOWN},etc
         * @param extra
         */
        public void onInfo(int what, int extra) {

        }

        /**
         * 拖动完成时回调。
         */
        public void onSeekComplete() {

        }

    }

    /**
     * 设置播放源。
     *
     * @param context
     * @param uri        支持本地URI或网络URI。
     * @param isUseCache 是否边播放边缓存。默认不缓存。
     */
    void setDataSource(@NonNull Context context, @NonNull Uri uri, boolean isUseCache);

    /**
     * 设置播放源。
     *
     * @param context
     * @param uri     支持本地URI或网络URI。
     */
    void setDataSource(@NonNull Context context, @NonNull Uri uri);

    /**
     * 设置播放源。
     *
     * @param context
     * @param uri        支持本地URI或网络URI。
     * @param headers    网络URI对应的http header。
     * @param isUseCache 是否边播放边缓存。默认不缓存。
     */
    void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers, boolean isUseCache);

    /**
     * 设置播放源。
     *
     * @param context
     * @param uri     支持本地URI或网络URI。
     * @param headers 网络URI对应的http header。
     */
    void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers);

    /**
     * 设置播放源。
     *
     * @param fd 文件描述符。支持asset文件描述符和普通文件描述符。
     */
    void setDataSource(@NonNull FileDescriptor fd);

    /**
     * 异步准备。
     */
    void prepareAsync();

    /**
     * 开始播放。
     */
    void start();

    /**
     * 暂停播放。
     */
    void pause();

    /**
     * 停止播放。
     */
    void stop();


    /**
     * 重置。此方法调用后必须重新调用{@code setDataSource}
     */
    void reset();

    /**
     * 释放所有资源(内存，cpu占用等),通常在activity的{@code onDestroy}调用。
     * 此方法调用后此player已不可用，若需再用需要创建新的player。
     */
    void release();

    /**
     * 设置左右音量大小。
     * 音量范围 0.0 to 1.0
     *
     * @param leftVolume
     * @param rightVolume
     */
    void setVolume(float leftVolume, float rightVolume);

    /**
     * 设置是否循环播放。必须在{@code setDataSource}之后调用才生效。
     *
     * @param looping
     */
    void setLooping(boolean looping);

    /**
     * 拖动到指定时间位置进行播放。
     *
     * @param msec 单位毫秒。
     */
    void seekTo(int msec);

    /**
     * 设置音频流的类型。
     *
     * @param streamtype 常量定义查看{@see AudioManager#STREAM_MUSIC，etc}
     */
    void setAudioStreamType(int streamtype);

    /**
     * 设置播放状态监听器。
     *
     * @param listener
     */
    void setStateListener(StateListener listener);

    /**
     * 是否正在播放。
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 获取当前播放进度。
     *
     * @return 当前进度，单位毫秒。
     */
    long getCurrentPosition();

    /**
     * 获取总的播放时间。
     *
     * @return 总播放时间，单位毫秒。
     */
    long getDuration();

    /**
     * 是否处于循环播放模式。
     *
     * @return
     */
    boolean isLooping();

    /**
     * 播放时是否保持屏幕常亮。
     *
     * @param screenOn
     */
    void setScreenOnWhilePlaying(boolean screenOn);

    /**
     * 获取视频宽。
     *
     * @return
     */
    int getVideoWidth();

    /**
     * 获取视频高。
     *
     * @return
     */
    int getVideoHeight();

    /**
     * 设置display
     *
     * @param sh
     */
    void setDisplay(@NonNull SurfaceHolder sh);

    /**
     * 设置display
     *
     * @param sf
     */
    void setSurface(Surface sf);

    /**
     * 获取播放缓存百分比。范围(0-100)
     *
     * @return
     */
    int getBufferPercent();

    /**
     * 是否已准备好。在prepare完成后处于此状态。
     *
     * @return
     */
    boolean isPrepared();

    int getAudioSessionId();

    int getVideoSarNum();

    int getVideoSarDen();
}
