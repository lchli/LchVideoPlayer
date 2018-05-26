package com.lch.video_player.internal;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.lch.video_player.helper.SingletonHolder;

public final class MediaCacheProxy {

    private MediaCacheProxy() {
    }

    private static final SingletonHolder<HttpProxyCacheServer> holder = new SingletonHolder<HttpProxyCacheServer>() {
        @Override
        protected HttpProxyCacheServer create(Object... objects) {
            return new HttpProxyCacheServer((Context) objects[0]);
        }
    };

    public static HttpProxyCacheServer get(Context context) {
        return holder.get(context.getApplicationContext());
    }

}
