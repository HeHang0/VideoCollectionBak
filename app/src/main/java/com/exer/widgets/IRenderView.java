package com.exer.widgets;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by HeHang on 2018/1/27.
 */

public interface IRenderView {
    int AR_ASPECT_FIT_PARENT = 0; // without clip
    int AR_ASPECT_FILL_PARENT = 1; // may clip
    int AR_ASPECT_WRAP_CONTENT = 2;
    int AR_MATCH_PARENT = 3;
    int AR_16_9_FIT_PARENT = 4;
    int AR_4_3_FIT_PARENT = 5;

    /**获取外层界面*/
    View getView();

    /**
     * 是否需要等待重置大小
     */
    boolean shouldWaitForResize();

    /**
     * 设置视频界面大小
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置视频裁剪方式
     */
    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    /**
     * 设置视频旋转角度
     */
    void setVideoRotation(int degree);

    /**
     * 设置视频裁剪方式
     */
    void setAspectRatio(int aspectRatio);

    /**
     * 添加视频渲染回调
     */
    void addRenderCallback(@NonNull IRenderCallback callback);

    /**
     * 移除视频渲染回调
     */
    void removeRenderCallback(@NonNull IRenderCallback callback);

    interface ISurfaceHolder {

        /**
         * surface界面绑定到mediaplay上
         */
        void bindToMediaPlayer(IMediaPlayer mp);

        /**
         * 获取渲染的view
         */
        @NonNull
        IRenderView getRenderView();

        /**
         * 获取渲染使用的具体view surface
         */
        @Nullable
        SurfaceHolder getSurfaceHolder();

        /**
         * 打开surface界面
         */
        @Nullable
        Surface openSurface();

        /**
         * 获取渲染使用的具体view texture
         */
        @Nullable
        SurfaceTexture getSurfaceTexture();
    }

    interface IRenderCallback {
        /**
         * 创建surface界面大小
         *
         * @param holder
         * @param width  could be 0
         * @param height could be 0
         */
        void onSurfaceCreated(@NonNull ISurfaceHolder holder, int width, int height);

        /**
         * surface界面大小改变监听
         *
         * @param holder
         * @param format could be 0
         * @param width
         * @param height
         */
        void onSurfaceChanged(@NonNull ISurfaceHolder holder, int format, int width, int height);

        /**
         * 界面回收
         */
        void onSurfaceDestroyed(@NonNull ISurfaceHolder holder);
    }
}
