package com.liuy.airhockettouch;

import android.graphics.Color;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * description:
 * author: freed on 2020/1/29
 * email: 674919909@qq.com
 * version: 1.0
 */
public class NativeRender implements GLSurfaceView.Renderer {
    static {
        System.loadLibrary("native-lib");
    }
    private int color= Color.RED;

    public void setColor(int color) {
        this.color = color;
    }

    public native void surfaceCreated(int color);

    public native void sufaceChange(int width,int height);

    public native void onDrawFrame();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        surfaceCreated(color);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        sufaceChange(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame();
    }
}
