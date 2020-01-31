package com.liuy.helloopengles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String TAG = "testOpengles";
    private GLSurfaceView mGLSurfaceView;

    // Used to load the 'native-lib' library on application startup.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        isSupportsEs2();
        setupView();

    }

    private void setupView() {
        mGLSurfaceView =new GLSurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);//设置opengl的版本
        final ColorRender3 renderer=new ColorRender3(this);
        mGLSurfaceView.setRenderer(renderer);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event!=null){
                    final float normalizedX=event.getX()/(float) v.getWidth()*2-1;
                    final float normalizedY=
					-(event.getY()/(float) v.getHeight()*2-1);

                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchPress(normalizedX,normalizedY);
                            }
                        });
                    }else if(event.getAction()==MotionEvent.ACTION_MOVE){
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchDrag(normalizedX,normalizedY);
                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });
    }

    //设备是否支持opengl es 2.0
    private boolean isSupportsEs2(){
        ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        Log.i(TAG,"version="+configurationInfo.reqGlEsVersion+","+(configurationInfo.reqGlEsVersion>=0x2));
        return configurationInfo.reqGlEsVersion>=0x2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGLSurfaceView!=null){//这么做可以防止程序崩溃
            mGLSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGLSurfaceView!=null){//这么做可以防止程序崩溃
            mGLSurfaceView.onPause();
        }
    }
}
