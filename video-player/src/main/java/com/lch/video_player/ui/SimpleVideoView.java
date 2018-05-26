package com.lch.video_player.ui;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * 默认的播放器UI,纯ui。如果觉得此UI不能满足需求请仿照此类自己实现一个即可。
 */

public class SimpleVideoView extends FrameLayout {


    private static final String TAG = "BAFVideoView";
    private static final int CTRL_SHOW_DURATION = 5_000;

    private final Handler handler = new Handler();
    private SimpleTextureRenderView mRenderView;
    private ProgressBar bufferProgressBar;
    private PlayerController mPlayerController;


    public SimpleVideoView(@NonNull Context context) {
        super(context);
        init();
    }

    public SimpleVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        addRenderView();

        bufferProgressBar = new ProgressBar(getContext());
        bufferProgressBar.setIndeterminate(true);
        bufferProgressBar.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        bufferProgressBar.setVisibility(GONE);

        addView(bufferProgressBar);

        LayoutParams lpCtrl = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        mPlayerController = new PlayerController(getContext());
        mPlayerController.setLayoutParams(lpCtrl);

        addView(mPlayerController);
    }

    private void addRenderView() {

        mRenderView = new SimpleTextureRenderView(getContext());
        mRenderView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        //mRenderView.addRenderCallback(mSHCallback);

        View realView = mRenderView;
        realView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER));

        addView(realView, 0);
    }


    public void resetUi() {
        mPlayerController.resetUI();
        hideLoading();


    }

    public SimpleTextureRenderView getRenderView() {
        return mRenderView;
    }

    /**
     * 获取播放控制器。
     *
     * @return
     */
    public PlayerController getPlayerController() {
        return mPlayerController;
    }

    /**
     * 设置屏幕分辨率
     *
     * @param aspectRatio {@link IRenderView#AR_ASPECT_FIT_PARENT} etc.
     */
    public void setAspectRatio(int aspectRatio) {
        mRenderView.setAspectRatio(aspectRatio);
    }


    public void showLoading() {
        bufferProgressBar.setVisibility(VISIBLE);
    }

    public void hideLoading() {
        bufferProgressBar.setVisibility(GONE);
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mRenderView.setVideoSize(videoWidth, videoHeight);
        requestLayout();
    }

    public void setVideoRotation(int degree) {
        mRenderView.setVideoRotation(degree);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mPlayerController.getVisibility() == VISIBLE) {
                    hideCtrl();
                } else {
                    showCtrl();
                }
                break;
        }
        return false;
    }

    private final Runnable hideCtrl = new Runnable() {
        @Override
        public void run() {
            mPlayerController.setVisibility(GONE);
        }
    };

    private void showCtrl() {
        handler.removeCallbacks(hideCtrl);

        mPlayerController.setVisibility(VISIBLE);
        handler.postDelayed(hideCtrl, CTRL_SHOW_DURATION);
    }

    private void hideCtrl() {
        mPlayerController.setVisibility(GONE);
    }


}
