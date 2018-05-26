package com.lch.video_player.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lch.video_player.R;


/**
 * Created by bbt-team on 2017/10/11.
 */

public class PlayerController extends FrameLayout {

    public ImageView ivPlayPause;
    public SeekBar seekBar;
    public TextView tvCurrentTime;
    public TextView tvTotalTime;
    public ImageView ivFullscreen;
    public ImageView ivForward;
    public ImageView ivBackward;
    public ImageView ivNext;
    public ImageView ivPre;
    public Listener mListener;


    public PlayerController(@NonNull Context context) {
        super(context);
        init();
    }

    public PlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.lch_vp_simple_player_view, this);
        ivPlayPause = (ImageView) findViewById(R.id.ivPlayPause);
        ivFullscreen = (ImageView) findViewById(R.id.ivFullscreen);
        ivForward = (ImageView) findViewById(R.id.ivForward);
        ivBackward = (ImageView) findViewById(R.id.ivBackward);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivPre = (ImageView) findViewById(R.id.ivPre);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
        tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mListener != null) {
                    mListener.onSeekbarChanged(seekBar);
                }

            }
        });
        ivPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPlayPauseClick(v);
                }
            }
        });
        ivFullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFullScreenClick(v);
                }
            }
        });
        ivForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onForwardClick(v);
                }
            }
        });
        ivBackward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onBackwardClick(v);
                }
            }
        });
        ivNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNextClick(v);
                }
            }
        });
        ivPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPreClick(v);
                }
            }
        });
    }


    public PlayerController resetUI() {
        setPlayIcon(android.R.drawable.ic_media_play);
        setProgress(0);
        setSecondProgress(0);
        setCurrentTime("00:00");
        setTotalTime("00:00");
        return this;
    }

    public PlayerController setPlayIcon(int resID) {
        ivPlayPause.setImageResource(resID);
        return this;
    }

    public PlayerController setProgress(int progress) {
        seekBar.setProgress(progress);
        return this;
    }

    public PlayerController setSecondProgress(int progress) {
        seekBar.setSecondaryProgress(progress);
        return this;
    }

    public int getMaxProgress() {
        return seekBar.getMax();
    }

    public PlayerController setCurrentTime(String time) {
        tvCurrentTime.setText(time);
        return this;
    }

    public PlayerController setTotalTime(String time) {
        tvTotalTime.setText(time);
        return this;
    }

    public PlayerController showPlayIcon(boolean isShow) {
        ivPlayPause.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public PlayerController showFullScreenIcon(boolean isShow) {
        ivFullscreen.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public PlayerController showForwardIcon(boolean isShow) {
        ivForward.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public PlayerController showBackwardIcon(boolean isShow) {
        ivBackward.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public PlayerController showNextIcon(boolean isShow) {
        ivNext.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public PlayerController showPreIcon(boolean isShow) {
        ivPre.setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    public PlayerController setListeners(Listener lsn) {
        mListener = lsn;
        return this;
    }

    public static abstract class Listener {

        public void onPlayPauseClick(View v) {
        }

        public void onFullScreenClick(View v) {
        }

        public void onForwardClick(View v) {
        }

        public void onBackwardClick(View v) {
        }

        public void onNextClick(View v) {
        }

        public void onPreClick(View v) {
        }

        public void onSeekbarChanged(SeekBar seekBar) {
        }
    }
}
