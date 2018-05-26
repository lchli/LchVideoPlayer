package com.lch.video_player.helper;

import android.content.Context;
import android.media.AudioManager;

import com.lch.video_player.R;
import com.lch.video_player.VideoPlayer;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by lichenghang on 2018/5/26.
 */

public final class VideoHelper {

    private static StringBuilder mFormatBuilder = new StringBuilder();
    private static Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    public static void requestAudioFocus(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public static void releaseAudioFocus(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    public static String formatVideoTime(int timeMs) {

        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static boolean isAcceptVideoSize(int w, int h) {
        return w > 0 && h > 0;
    }


    public static String videoErrorToString(int what, Context context) {
        switch (what) {
            case VideoPlayer.MEDIA_ERROR_UNKNOWN:
                return context.getResources().getString(R.string.lch_vp_error_unknown);
            case VideoPlayer.MEDIA_ERROR_SERVER_DIED:
                return context.getResources().getString(R.string.lch_vp_error_server_died);
            case VideoPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                return context.getResources().getString(R.string.lch_vp_error_not_valid);
            case VideoPlayer.MEDIA_ERROR_IO:
                return context.getResources().getString(R.string.lch_vp_error_io);
            case VideoPlayer.MEDIA_ERROR_MALFORMED:
                return context.getResources().getString(R.string.lch_vp_error_malformed);
            case VideoPlayer.MEDIA_ERROR_UNSUPPORTED:
                return context.getResources().getString(R.string.lch_vp_error_unsupported);
            case VideoPlayer.MEDIA_ERROR_TIMED_OUT:
                return context.getResources().getString(R.string.lch_vp_error_timeout);
            case VideoPlayer.MEDIA_ERROR_EXCEPTION:
                return context.getResources().getString(R.string.lch_vp_error_exception);
            default:
                return context.getResources().getString(R.string.lch_vp_error_unknown);
        }
    }
}
