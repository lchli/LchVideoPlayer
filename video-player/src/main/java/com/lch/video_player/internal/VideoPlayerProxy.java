package com.lch.video_player.internal;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.danikula.videocache.HttpProxyCacheServer;
import com.lch.video_player.VideoPlayer;
import com.lch.video_player.helper.Config;
import com.lch.video_player.model.BAFTimedText;

import java.io.FileDescriptor;
import java.util.Locale;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * Created by bbt-team on 2017/9/29.
 */

public class VideoPlayerProxy implements VideoPlayer {
    private static final String TAG = "VideoPlayerProxy";

    private IMediaPlayer delegate;
    private StateListener mStateListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isUpdatingProgress = false;
    private boolean isPrepared = false;
    private static HttpProxyCacheServer gHttpProxyCacheServer;
    private int bufferPercent;


    private final Runnable mTicker = new Runnable() {
        public void run() {
            if (mStateListener != null) {
                mStateListener.onProgress(getCurrentPosition(), getDuration());
            }

            long now = SystemClock.uptimeMillis();
            long next = now + 500;

            mHandler.postAtTime(mTicker, next);
        }
    };

    public VideoPlayerProxy(@NonNull IMediaPlayer delegator, @NonNull HttpProxyCacheServer cacheServer) {
        this.delegate = delegator;
        gHttpProxyCacheServer = cacheServer;

        delegate.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                isPrepared = true;
                bufferPercent = 0;

                if (mStateListener != null) {
                    mStateListener.onPrepared();
                }
            }
        });

        delegate.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
                bufferPercent = percent;

                if (mStateListener != null) {
                    mStateListener.onBufferingUpdate(percent);
                }
            }
        });

        delegate.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (mStateListener != null) {
                    mStateListener.onProgress(getDuration(), getDuration());
                    mStateListener.onCompletion();
                }

                if (!isLooping()) {
                    stopUpateProgress();
                }
            }
        });

        delegate.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                whenError(what, extra);
                return false;
            }
        });

        delegate.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                Config.logger.log(TAG, String.format(Locale.ENGLISH, "onInfo(what=%d,extra=%d)", what, extra));

                if (mStateListener != null) {
                    mStateListener.onInfo(what, extra);
                }
                return false;
            }
        });

        delegate.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                if (mStateListener != null) {
                    mStateListener.onVideoSizeChanged(width, height);
                }
            }
        });

        delegate.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
                if (mStateListener != null) {
                    mStateListener.onTimedText(new BAFTimedText(ijkTimedText.getBounds(), ijkTimedText.getText()));
                }
            }
        });
        delegate.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                if (mStateListener != null) {
                    mStateListener.onSeekComplete();
                }
            }
        });
    }


    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri) {
        setDataSource(context, uri, false);
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers) {
        setDataSource(context, uri, headers, false);
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri, boolean isUseCache) {
        try {
            if (isUseCache(isUseCache, uri.toString())) {
                uri = Uri.parse(gHttpProxyCacheServer.getProxyUrl(uri.toString()));
                Config.logger.logIfDebug(TAG, "proxy url=" + uri.toString());
            }
            delegate.setDataSource(context, uri);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri, @Nullable Map<String, String> headers, boolean isUseCache) {
        try {
            if (isUseCache(isUseCache, uri.toString())) {
                uri = Uri.parse(gHttpProxyCacheServer.getProxyUrl(uri.toString()));
                Config.logger.logIfDebug(TAG, "proxy url=" + uri.toString());
            }
            delegate.setDataSource(context, uri, headers);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    private static boolean isUseCache(boolean isCache, String url) {
        return isCache && url.startsWith("http") && !url.contains("127.0.0.1") && !url.contains(".m3u8");
    }

    @Override
    public void setDataSource(@NonNull FileDescriptor fd) {
        try {
            delegate.setDataSource(fd);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void prepareAsync() {
        try {
            delegate.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void stop() {
        try {
            delegate.stop();
            reprepare();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void start() {
        try {
            delegate.start();
            startUpateProgress();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }

    }

    @Override
    public void pause() {
        try {
            delegate.pause();
            stopUpateProgress();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void reset() {
        try {
            delegate.reset();
            reprepare();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void release() {
        try {
            delegate.release();
            reprepare();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    private void reprepare() {
        isPrepared = false;
        stopUpateProgress();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        try {
            delegate.setVolume(leftVolume, rightVolume);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void setLooping(boolean looping) {
        try {
            delegate.setLooping(looping);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void seekTo(int msec) {
        try {
            delegate.seekTo(msec);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        try {
            delegate.setAudioStreamType(streamtype);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void setStateListener(StateListener listener) {
        mStateListener = listener;
    }

    @Override
    public boolean isPlaying() {
        try {
            return delegate.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return false;
    }

    @Override
    public long getCurrentPosition() {
        try {
            long cur = delegate.getCurrentPosition();
            if (cur > delegate.getDuration()) {
                cur = delegate.getDuration();
            }
            return cur;
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        try {
            return delegate.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    @Override
    public boolean isLooping() {
        try {
            return delegate.isLooping();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return false;
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        try {
            delegate.setScreenOnWhilePlaying(screenOn);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public int getVideoWidth() {
        try {
            return delegate.getVideoWidth();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        try {
            return delegate.getVideoHeight();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    @Override
    public void setDisplay(@NonNull SurfaceHolder sh) {
        try {
            delegate.setDisplay(sh);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public void setSurface(Surface sf) {
        try {
            delegate.setSurface(sf);
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
    }

    @Override
    public int getBufferPercent() {
        return bufferPercent;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public int getAudioSessionId() {
        try {
            return delegate.getAudioSessionId();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        try {
            return delegate.getVideoSarNum();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        try {
            return delegate.getVideoSarDen();
        } catch (Exception e) {
            e.printStackTrace();
            whenError(MEDIA_ERROR_EXCEPTION, MEDIA_ERROR_EXCEPTION);
        }
        return 0;
    }

    private void stopUpateProgress() {
        if (isUpdatingProgress) {
            mHandler.removeCallbacks(mTicker);
            isUpdatingProgress = false;
        }
    }

    private void startUpateProgress() {
        if (!isUpdatingProgress) {
            mTicker.run();
            isUpdatingProgress = true;
        }
    }

    private void whenError(int what, int extra) {
        if (mStateListener != null) {
            mStateListener.onError(what, extra);
        }
    }
}
