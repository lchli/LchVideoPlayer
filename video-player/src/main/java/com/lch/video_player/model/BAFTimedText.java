package com.lch.video_player.model;

import android.graphics.Rect;

/**
 * Created by bbt-team on 2017/9/28.
 */

public class BAFTimedText {

    private Rect mTextBounds = null;
    private String mTextChars = null;

    public BAFTimedText(Rect bounds, String text) {
        this.mTextBounds = bounds;
        this.mTextChars = text;
    }

    public Rect getBounds() {
        return mTextBounds;
    }

    public String getText() {
        return mTextChars;
    }
}
